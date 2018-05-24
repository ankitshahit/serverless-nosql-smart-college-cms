package io.college.cms.core.courses.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.service.CourseResponseService;
import lombok.extern.slf4j.Slf4j;

/**
 * A gateway between view and model layer, it will manage concepts of courses
 * functionality to deal.
 * 
 * @author Ankit
 * 
 */
@Slf4j
@RestController
@RequestMapping(path = "/1.0/courses", produces = MediaType.APPLICATION_JSON_VALUE)
public class CourseController {
	public static final String COURSE_NAME = "course_name";
	private static final String START_NUMBER = "start_number";
	private static final String MAX_RECORDS = "max_records";

	private CourseResponseService courseResService;

	@Autowired
	public void setCourseResponseService(CourseResponseService courseResponseService) {
		this.courseResService = courseResponseService;
	}

	@RequestMapping(method = { RequestMethod.GET })
	public FactoryResponse findByCourseName(@RequestParam(value = COURSE_NAME, required = false) String courseName,
			@RequestParam(value = START_NUMBER, required = false) long startNumber,
			@RequestParam(value = MAX_RECORDS, required = false) long maxRecords, HttpServletRequest request,
			HttpServletResponse response) {
		LOGGER.debug("Course name {0}");
		FactoryResponse factoryResponse = null;
		if (courseName != null && !courseName.isEmpty()) {
			factoryResponse = courseResService.findByCourseName(request, courseName);
		} else {
			// TODO: This is considering a limit rather than the end records.
			// we may probably require a count api just to count how many
			// courses we got.
			factoryResponse = courseResService.findAllCourses(request, startNumber, maxRecords);
		}
		response.setStatus(factoryResponse.getSummaryMessage().code().value());
		return factoryResponse;
	}

	@RequestMapping(method = { RequestMethod.POST, RequestMethod.PUT }, consumes = MediaType.APPLICATION_JSON_VALUE)
	public FactoryResponse createUpdateCourse(CourseModel courseEntity, HttpServletRequest request,
			HttpServletResponse response) {
		LOGGER.debug("Course name {0}");
		FactoryResponse factoryResponse = courseResService.createUpdateCourse(request, courseEntity);
		response.setStatus(factoryResponse.getSummaryMessage().code().value());
		return factoryResponse;
	}

	@RequestMapping(method = { RequestMethod.DELETE }, consumes = MediaType.APPLICATION_JSON_VALUE)
	public FactoryResponse deleteCourse(CourseModel courseEntity, HttpServletRequest request,
			HttpServletResponse response) {
		LOGGER.debug("Course name {0}");
		FactoryResponse factoryResponse = courseResService.deleteCourse(request, courseEntity);
		response.setStatus(factoryResponse.getSummaryMessage().code().value());
		return factoryResponse;
	}

	@RequestMapping(method = { RequestMethod.PUT,
			RequestMethod.POST }, path = "/groups", consumes = MediaType.APPLICATION_JSON_VALUE)
	public FactoryResponse saveUpdateGroup(HttpServletRequest request, HttpServletResponse response,
			@RequestBody CourseModel.CourseSubjectGroupsModel courseSubjectGroupModel) {
		FactoryResponse factoryResponse = courseResService.createUpdateGroups(request, courseSubjectGroupModel);
		response.setStatus(factoryResponse.getSummaryMessage().code().value());
		return factoryResponse;
	}

}
