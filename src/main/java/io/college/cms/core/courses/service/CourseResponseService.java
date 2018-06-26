package io.college.cms.core.courses.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.dynamodb.model.Paginate;
import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CourseResponseService {
	private ICourseDbService dbService;

	@Autowired
	public void setDbService(ICourseDbService dbService) {
		this.dbService = dbService;
	}

	public FactoryResponse saveCourseMetadata(CourseModel course) {
		FactoryResponse fr = null;
		try {
			ValidationHandler.throwExceptionIfNull(course, "No request body provided",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(course.getCourseName()),
					"No course name provided", ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(course.getDescription()),
					"Course description is required", ExceptionType.VALIDATION_EXCEPTION);
			CourseModel data = dbService.findByCourseName(course.getCourseName());
			ValidationHandler.throwExceptionIfTrue(data != null, "Course already exists. Use update option.",
					ExceptionType.VALIDATION_EXCEPTION);
			dbService.saveCourse(course);
			fr = FactoryResponse.builder().response(course).summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (ValidationException | IllegalArgumentException e) {
			LOGGER.error(e.getMessage());
			fr = FactoryResponse.builder().response(e.getMessage()).summaryMessage(SummaryMessageEnum.VALIDATION_ERROR)
					.build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			fr = FactoryResponse.builder().response("Application don't feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse findByCourseName(HttpServletRequest request, String courseName) {
		FactoryResponse fr = null;
		try {
			ValidationHandler.throwExceptionIfNull(courseName, null, ExceptionType.VALIDATION_EXCEPTION);
			CourseModel course = dbService.findByCourseName(courseName);
			fr = FactoryResponse.builder().response(course).summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (ValidationException | IllegalArgumentException e) {
			LOGGER.error(e.getMessage());
			fr = FactoryResponse.builder().response("No such course exists or invalid course name")
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			fr = FactoryResponse.builder().response("Application don't feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse createUpdateCourse(HttpServletRequest request, CourseModel course) {
		FactoryResponse fr = null;
		try {
			ValidationHandler.throwExceptionIfNull(course, null, ExceptionType.VALIDATION_EXCEPTION);
			dbService.saveCourse(course);
			fr = FactoryResponse.builder().response("Saved/updated successfully.")
					.summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (ValidationException | IllegalArgumentException e) {
			LOGGER.error(e.getMessage());
			fr = FactoryResponse.builder().response("Course may already exist or invalid validation exception")
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			fr = FactoryResponse.builder().response("Application don't feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse deleteCourse(String courseName) {
		FactoryResponse fr = null;
		try {
			ValidationHandler.throwExceptionIfNull(courseName, null, ExceptionType.VALIDATION_EXCEPTION);
			dbService.deleteCourse(courseName);
			fr = FactoryResponse.builder().response("deleted successfully.").summaryMessage(SummaryMessageEnum.SUCCESS)
					.build();
		} catch (ValidationException | IllegalArgumentException e) {
			LOGGER.error(e.getMessage());
			fr = FactoryResponse.builder().response("Invalid course or invalid permissions")
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			fr = FactoryResponse.builder().response("Application don't feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse deleteCourse(HttpServletRequest request, CourseModel course) {
		return deleteCourse(course.getCourseName());
	}

	public FactoryResponse findAllCourses(HttpServletRequest request, Long startRecord, Long endRecord) {
		FactoryResponse fr = null;
		try {
			ValidationHandler.throwExceptionIfNull(startRecord, "No start page number",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfNull(startRecord, "No end page number",
					ExceptionType.VALIDATION_EXCEPTION);
			Paginate paginate = new Paginate();
			paginate.setStartNumber(startRecord);
			paginate.setEndNumber(endRecord);
			List<CourseModel> course = dbService.loadCourses();
			fr = FactoryResponse.builder().response(course).summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (ValidationException | IllegalArgumentException e) {
			LOGGER.error(e.getMessage());
			fr = FactoryResponse.builder().response("No course exists.")
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			fr = FactoryResponse.builder().response("Application don't feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

}
