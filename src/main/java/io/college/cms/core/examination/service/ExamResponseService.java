package io.college.cms.core.examination.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.courses.controller.constants.SubjectType;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.db.CourseModel.SubjectModel;
import io.college.cms.core.courses.service.ICourseDbService;
import io.college.cms.core.examination.model.ExaminationModel;
import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ExceptionHandler;
import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.ResourceDeniedException;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExamResponseService {
	private IExamDbService examDbService;
	private ExamQrService examQrService;
	private ICourseDbService courseDbService;

	@Autowired
	public void setExamDbService(IExamDbService examDbService) {
		this.examDbService = examDbService;
	}

	@Autowired
	public void setExamQrService(ExamQrService examQrService) {
		this.examQrService = examQrService;
	}

	@Autowired
	public void setCourseDbService(ICourseDbService courseDbService) {
		this.courseDbService = courseDbService;
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
			fr = FactoryResponse.builder().response(model).summaryMessage(SummaryMessageEnum.SUCCESS).build();
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

	@Cacheable("qrForExam")
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

	@Cacheable()
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

			ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(examination.getExamSubjects()),
					"No subjects provided", ExceptionType.VALIDATION_EXCEPTION);

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

}