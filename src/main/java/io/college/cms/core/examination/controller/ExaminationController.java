package io.college.cms.core.examination.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.courses.controller.CourseController;
import io.college.cms.core.subjects.controller.SubjectController;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/1.0/exams", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.IMAGE_JPEG_VALUE })
@Slf4j
public class ExaminationController {

	public static final String EXAM_NAME = "exam_name";

	@RequestMapping(method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public FactoryResponse getExams(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = CourseController.COURSE_NAME, required = false) String courseName) {
		FactoryResponse fr = null;
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	/**
	 * Schedules an exam, doesn't publish it
	 * 
	 * @param request
	 * @param response
	 * @param body
	 * @return
	 */
	@RequestMapping(path = "schedule", method = { RequestMethod.PUT, RequestMethod.POST }, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })

	public FactoryResponse scheduleExams(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> body) {
		FactoryResponse fr = null;
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	/**
	 * publishes date(s) to website.
	 * 
	 * @param request
	 * @param response
	 * @param body
	 * @return
	 */
	@RequestMapping(path = "{" + EXAM_NAME + "}/publish", method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public FactoryResponse publishExam(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(EXAM_NAME) String examName) {
		FactoryResponse fr = null;
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	/**
	 * provides qr code of users in exam for each subject.
	 * 
	 * @param request
	 * @param response
	 * @param body
	 * @return
	 */
	@RequestMapping(path = "{" + EXAM_NAME + "}/print/qr", method = { RequestMethod.GET }, produces = {
			MediaType.IMAGE_JPEG_VALUE })
	public FactoryResponse printQrCode(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(EXAM_NAME) String examName,
			@RequestParam(SubjectController.SUBJECT_NAME) String subjectName) {
		FactoryResponse fr = null;
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	/**
	 * publishes result(s) to website.
	 * 
	 * @param request
	 * @param response
	 * @param body
	 * @return
	 */
	@RequestMapping(path = "{" + EXAM_NAME + "}/publish/result", method = { RequestMethod.GET })
	public FactoryResponse publishResults(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(EXAM_NAME) String examName) {
		FactoryResponse fr = null;
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	/**
	 * unpublishes result(s) to website.
	 * 
	 * @param request
	 * @param response
	 * @param body
	 * @return
	 */
	@RequestMapping(path = "{" + EXAM_NAME + "}/un-publish/result", method = { RequestMethod.GET })
	public FactoryResponse unPublishResults(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(EXAM_NAME) String examName) {
		FactoryResponse fr = null;
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	/**
	 * prints result(s)
	 * 
	 * @param request
	 * @param response
	 * @param body
	 * @return
	 */
	@RequestMapping(path = "{" + EXAM_NAME + "}/result/print", method = { RequestMethod.GET }, produces = {
			MediaType.IMAGE_JPEG_VALUE })
	public FactoryResponse printResults(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(EXAM_NAME) String examName) {
		FactoryResponse fr = null;
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	/**
	 * Updates a score of a subject by a student.
	 * 
	 * @param request
	 * @param response
	 * @param examName
	 * @return
	 */
	@RequestMapping(path = "{" + EXAM_NAME + "}/update-subject-score", method = { RequestMethod.PUT }, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public FactoryResponse updateExamScore(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(EXAM_NAME) String examName) {
		FactoryResponse fr = null;
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}
}
