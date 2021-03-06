package io.college.cms.core.examination.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ConditionalOperator;
import com.vaadin.server.FileResource;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.application.Utils;
import io.college.cms.core.application.excel.services.ExcelService;
import io.college.cms.core.attendance.model.AttendanceModel;
import io.college.cms.core.courses.controller.constants.SubjectType;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.db.CourseModel.SubjectModel;
import io.college.cms.core.courses.service.ICourseDbService;
import io.college.cms.core.dynamodb.service.DynamoGenericService;
import io.college.cms.core.examination.model.ExaminationModel;
import io.college.cms.core.examination.model.ResultModel;
import io.college.cms.core.examination.model.TimeTableModel;
import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ExceptionHandler;
import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.ResourceDeniedException;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;
import io.college.cms.core.ui.listener.SecurityListener;
import io.college.cms.core.user.service.SecurityService;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExamResponseService {
	private IExamDbService examDbService;
	private ExamQrService examQrService;
	private ICourseDbService courseDbService;
	private SecurityService securityService;
	private DynamoGenericService<ResultModel, String> resultDbService;
	private DynamoGenericService<TimeTableModel, String> timeTableDbService;
	private ApplicationContext app;

	@Autowired
	public ExamResponseService(IExamDbService examDbService, ExamQrService examQrService,
			ICourseDbService courseDbService, SecurityService securityService,
			DynamoGenericService<ResultModel, String> resultDbService,
			DynamoGenericService<TimeTableModel, String> timeTableDbService, ApplicationContext app) {
		super();
		this.examDbService = examDbService;
		this.examQrService = examQrService;
		this.courseDbService = courseDbService;
		this.securityService = securityService;
		this.resultDbService = resultDbService;
		this.timeTableDbService = timeTableDbService;
		this.app = app;
		this.timeTableDbService.setClass(TimeTableModel.class);
		this.resultDbService.setClass(ResultModel.class);
	}

	@Async
	public void download(Consumer<File> downloadFileListener, Consumer<Float> progressListener,
			Consumer<String> errorListener, Runnable successListener, ResultModel attendance) {
		try {
			ExcelService excelService = app.getBean(ExcelService.class);
			excelService.setHeaderTitle("Sr.no", "Course", "Semester", "Subject", "Subject type", "Student",
					"Updated by", "Total Marks", "Marks", "Result");
			List<ResultModel> records = new ArrayList<>();
			progressListener.accept(5.0f);
			if (StringUtils.isNotEmpty(attendance.getUsername())) {
				DynamoDBScanExpression scan = new DynamoDBScanExpression();
				Condition condition = new Condition();
				condition.setComparisonOperator(ComparisonOperator.EQ);
				condition.withAttributeValueList(new AttributeValue().withS(attendance.getUsername()));
				scan.addFilterCondition("username", condition);
				progressListener.accept(15.0f);
				records = resultDbService.findBy(scan);
			} else {
				DynamoDBScanExpression scan = new DynamoDBScanExpression();
				scan.withFilterConditionEntry("courseName",
						new Condition().withComparisonOperator(ComparisonOperator.EQ)
								.withAttributeValueList(new AttributeValue().withS(attendance.getCourseName())))
						.withConditionalOperator(ConditionalOperator.AND)
						.withFilterConditionEntry("subjectName",
								new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(
										new AttributeValue().withS(attendance.getSubjectName())))
						.withConditionalOperator(ConditionalOperator.AND).withFilterConditionEntry("semester",
								new Condition().withComparisonOperator(ComparisonOperator.EQ)
										.withAttributeValueList(new AttributeValue().withS(attendance.getSemester())));
				progressListener.accept(15.0f);
				records = resultDbService.findBy(scan);

			}
			progressListener.accept(25.0f);
			if (CollectionUtils.isEmpty(records)) {
				errorListener.accept("<p>No records found</p>");
				return;
			}
			progressListener.accept(29.0f);
			float currentProgress = 29.0f;
			List<String> data = new ArrayList<>();
			int count = 0;
			for (ResultModel model : records) {
				data.add(String.valueOf(++count));
				data.add(model.getCourseName());
				data.add(model.getSemester());
				data.add(model.getSubjectName());
				data.add(String.valueOf(model.getSubjectType()));
				data.add(model.getUsername());
				data.add(model.getActionBy());
				data.add(model.getTotalMarks());
				data.add(model.getMarks());
				data.add(String.valueOf(model.isResult() ? "Pass" : "Fail"));
				progressListener.accept(currentProgress + 0.1f);
			}
			progressListener.accept(currentProgress + 5.0f);
			excelService.write(data, downloadFileListener, errorListener, progressListener, successListener);

		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage());
			errorListener.accept("Unable to process file.");
		}
	}

	public FactoryResponse findTimeTable(String examName) {
		FactoryResponse fr = null;
		try {

			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(examName), "Exam name is not provided.",
					ExceptionType.VALIDATION_EXCEPTION);
			DynamoDBScanExpression scan = new DynamoDBScanExpression();
			scan.withFilterConditionEntry("examName",
					new Condition().withAttributeValueList(new AttributeValue().withS(examName))
							.withComparisonOperator(ComparisonOperator.EQ));
			List<TimeTableModel> timeTableRecords = timeTableDbService.findBy(scan);
			ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(timeTableRecords), "No subjects found",
					ExceptionType.VALIDATION_EXCEPTION);
			fr = FactoryResponse.builder().response(timeTableRecords).summaryMessage(SummaryMessageEnum.SUCCESS)
					.build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Unable to load subjects for exam.")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse saveTimeTable(TimeTableModel model) {
		FactoryResponse fr = null;
		try {

			timeTableDbService.save(model);
			fr = FactoryResponse.builder().response("Saved!").summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Unable to load subjects for exam.")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse deleteByExamName(String examName) {
		FactoryResponse fr = null;
		try {

			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(examName), "Exam name is not provided.",
					ExceptionType.VALIDATION_EXCEPTION);
			examDbService.deleteExam(examName);
			fr = FactoryResponse.builder().response(examName).summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ResourceDeniedException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.ACCESS_DENIED).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Application dont feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse saveExamMetadata(ExaminationModel model) {
		FactoryResponse fr = null;
		try {
			ValidationHandler.throwExceptionIfNull(model, "Request json is not provided.",
					ExceptionType.VALIDATION_EXCEPTION);

			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(model.getCourseName()),
					"Course name is not provided.", ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(model.getExamName()),
					"Exam name is not provided.", ExceptionType.VALIDATION_EXCEPTION);

			ValidationHandler.throwExceptionIfNull(model.getExamStartDate(), "Exam start date is not provided.",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfNull(model.getExamEndDate(), "Exam end date is not provided.",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(model.getExamStartDate().compareTo(model.getExamEndDate()) > 1,
					"Exam Start date cannot be greater than exam end date.", ExceptionType.VALIDATION_EXCEPTION);
			examDbService.saveUpdateExam(model);
			fr = FactoryResponse.builder().response("Exam is successfully scheduled!")
					.summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ResourceDeniedException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.ACCESS_DENIED).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Application dont feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse getExamByExamId(HttpServletRequest request, String examName) {
		FactoryResponse fr = null;
		try {
			ExaminationModel exam = examDbService.findByExamName(examName);
			fr = FactoryResponse.builder().response(exam).summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ResourceDeniedException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.ACCESS_DENIED).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Application dont feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse getExamsScheduled(HttpServletRequest request) {
		FactoryResponse fr = null;
		try {
			List<ExaminationModel> exams = examDbService.findAllExams();
			fr = FactoryResponse.builder().response(exams).summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ResourceDeniedException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.ACCESS_DENIED).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Application dont feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	@Async
	public void qr(String examName, String subjectName, String type, String fileName, String username,
			Consumer<FileResource> consumer, Runnable progressListener, Runnable successListener) {
		try {
			progressListener.run();
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(subjectName), "Subject name is not provided.",
					ExceptionType.VALIDATION_EXCEPTION);

			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(examName), "Exam name is not provided.",
					ExceptionType.VALIDATION_EXCEPTION);

			ExaminationModel examData;
			examData = examDbService.findByExamName(examName);

			CourseModel courseData = courseDbService.findByCourseName(examData.getCourseName());

			ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(courseData.getSubjects()),
					"Course doesn't have any subjects", ExceptionType.VALIDATION_EXCEPTION);

			// lombok var requires an instance of what is to be loaded, it can
			// never be initialized as a null
			SubjectModel subjectData = SubjectModel.builder().build();
			for (SubjectModel subject : courseData.getSubjects()) {
				if (!subjectName.equalsIgnoreCase(subject.getSubjectName())) {
					continue;
				}
				subjectData = subject;
			}
			List<String> users = new ArrayList<>();
			if (StringUtils.isNotEmpty(username)) {
				users.add(username);
			} else {
				ValidationHandler.throwExceptionIfTrue(
						subjectData == null || CollectionUtils.isEmpty(subjectData.getStudentUsernames()),
						"No students are available for mentioned subject & course ",
						ExceptionType.VALIDATION_EXCEPTION);
				users.addAll(subjectData.getStudentUsernames());
			}
			SubjectType subjectType = SubjectType.findByType(type);
			File file = examQrService.printForSubject(examName, subjectName, subjectType, users);
			consumer.accept(new FileResource(file));
			successListener.run();
		} catch (IllegalArgumentException | ApplicationException | ResourceDeniedException e) {
			LOGGER.error(e.getMessage());
		}

	}

	public void qrForStudentBySubjectNameAndType(HttpServletRequest request, HttpServletResponse response,
			String examName, String subjectName, String type, String fileName, String username) {
		try {
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(subjectName), "Subject name is not provided.",
					ExceptionType.VALIDATION_EXCEPTION);

			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(examName), "Exam name is not provided.",
					ExceptionType.VALIDATION_EXCEPTION);

			ExaminationModel examData = examDbService.findByExamName(examName);
			CourseModel courseData = courseDbService.findByCourseName(examData.getCourseName());

			ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(courseData.getSubjects()),
					"Course doesn't have any subjects", ExceptionType.VALIDATION_EXCEPTION);

			// lombok var requires an instance of what is to be loaded, it can
			// never be initialized as a null
			SubjectModel subjectData = SubjectModel.builder().build();
			for (SubjectModel subject : courseData.getSubjects()) {
				if (!subjectName.equalsIgnoreCase(subject.getSubjectName())) {
					continue;
				}
				subjectData = subject;
				break;
			}
			ValidationHandler.throwExceptionIfNull(subjectData, "No such subject found",
					ExceptionType.VALIDATION_EXCEPTION);
			List<String> students = new ArrayList<String>();
			students.add(username);

			SubjectType subjectType = SubjectType.findByType(type);
			File file = examQrService.printForSubject(examName, subjectName, subjectType, students);
			@Cleanup
			InputStream inputStream = new FileInputStream(file);
			response.setContentType("application/force-download");
			if (StringUtils.isEmpty(fileName)) {
				fileName = file.getName();
			}
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".pdf");
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			try {
				response.sendRedirect(request.getContextPath() + "/error");
			} catch (Exception e) {
				LOGGER.error("Unable to redirect to error page.");
			}
		}
	}

	public void qrByExamNameAndSubjectNameAndType(HttpServletRequest request, HttpServletResponse response,
			String examName, String subjectName, String type, String fileName) {

		try {

			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(subjectName), "Subject name is not provided.",
					ExceptionType.VALIDATION_EXCEPTION);

			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(examName), "Exam name is not provided.",
					ExceptionType.VALIDATION_EXCEPTION);

			ExaminationModel examData = examDbService.findByExamName(examName);
			CourseModel courseData = courseDbService.findByCourseName(examData.getCourseName());

			ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(courseData.getSubjects()),
					"Course doesn't have any subjects", ExceptionType.VALIDATION_EXCEPTION);

			// lombok var requires an instance of what is to be loaded, it can
			// never be initialized as a null
			SubjectModel subjectData = SubjectModel.builder().build();
			for (SubjectModel subject : courseData.getSubjects()) {
				if (!subjectName.equalsIgnoreCase(subject.getSubjectName())) {
					continue;
				}
				subjectData = subject;
			}

			ValidationHandler.throwExceptionIfTrue(
					subjectData == null || CollectionUtils.isEmpty(subjectData.getStudentUsernames()),
					"No students are available for mentioned subject & course ", ExceptionType.VALIDATION_EXCEPTION);

			SubjectType subjectType = SubjectType.findByType(type);
			File file = examQrService.printForSubject(examName, subjectName, subjectType,
					subjectData.getStudentUsernames());
			@Cleanup
			InputStream inputStream = new FileInputStream(file);
			response.setContentType("application/force-download");
			if (StringUtils.isEmpty(fileName)) {
				fileName = file.getName();
			}

			response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".pdf");
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			try {
				response.sendRedirect(request.getContextPath() + "/error");
			} catch (Exception e) {
				LOGGER.error("Unable to redirect to error page.");
			}
		}

	}

	public FactoryResponse createUpdateExam(HttpServletRequest request, ExaminationModel examination) {
		FactoryResponse fr = null;
		try {
			ValidationHandler.throwExceptionIfNull(examination, "No request json is available ",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(examination.getCourseName()),
					"Course name is not provided ", ExceptionType.VALIDATION_EXCEPTION);

			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(examination.getExamName()),
					"Exam name not provided ", ExceptionType.VALIDATION_EXCEPTION);

			/*
			 * ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(
			 * examination.getExamSubjects()), "No subjects provided",
			 * ExceptionType.VALIDATION_EXCEPTION);
			 */

			examDbService.saveUpdateExam(examination);
			fr = FactoryResponse.builder().response("Successfully saved/updated")
					.summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ResourceDeniedException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.ACCESS_DENIED).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Application dont feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse deleteExamByExamName(HttpServletRequest request, String examName) {
		FactoryResponse fr = null;
		try {
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(examName), "Exam name not provided ",
					ExceptionType.VALIDATION_EXCEPTION);
			examDbService.findByExamName(examName);
			examDbService.deleteExam(examName);
			fr = FactoryResponse.builder().response("Successfully deleted.").summaryMessage(SummaryMessageEnum.SUCCESS)
					.build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ResourceDeniedException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.ACCESS_DENIED).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Application dont feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse findAllExams() {
		FactoryResponse fr = null;
		try {
			List<ExaminationModel> exams = examDbService.findAllExams();
			if (CollectionUtils.isEmpty(exams)) {
				exams = new ArrayList<>();
			}
			fr = FactoryResponse.builder().response(exams).summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ResourceDeniedException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.ACCESS_DENIED).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Unable to fetch exams.").summaryMessage(SummaryMessageEnum.FAILURE)
					.build();
		}
		return fr;
	}

	public FactoryResponse updateMarks(ResultModel model) {
		FactoryResponse fr = null;
		try {
			ValidationHandler.throwExceptionIfTrue(Utils.isNull(model), "request is null",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(model.getCourseName()), "course is empty",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(model.getSubjectName()), "subject name is empty",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(model.getSubjectType()), "subject type is empty",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(model.getUsername()), "username is empty",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(model.getExamName()), "exam name is empty",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(model.getMarks()), "marks is empty",
					ExceptionType.VALIDATION_EXCEPTION);
			model.setActionBy(securityService.getPrincipal());
			securityService.authorize(SecurityListener.STAFF_ACCESS);
			resultDbService.save(model);
			fr = FactoryResponse.builder().response("Successfully saved.").summaryMessage(SummaryMessageEnum.SUCCESS)
					.build();
		} catch (ValidationException | AuthenticationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Unable to update marks.")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse findAllTimeTable() {
		FactoryResponse fr = null;
		try {
			List<TimeTableModel> results = this.timeTableDbService.findAll();
			if (CollectionUtils.isEmpty(results)) {
				results = new ArrayList<>();
			}
			fr = FactoryResponse.builder().response(results).summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (ValidationException | AuthenticationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Unable to fetch timetable.")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse findAllResult() {
		FactoryResponse fr = null;
		try {
			List<ResultModel> results = this.resultDbService.findAll();
			if (CollectionUtils.isEmpty(results)) {
				results = new ArrayList<>();
			}
			fr = FactoryResponse.builder().response(results).summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (ValidationException | AuthenticationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Unable to update marks.")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

}