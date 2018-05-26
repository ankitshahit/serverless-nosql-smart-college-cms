package io.college.cms.core.examination.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.courses.controller.CourseController;
import io.college.cms.core.examination.db.ExaminationModel;
import io.college.cms.core.examination.service.ExamResponseService;
import io.college.cms.core.user.controller.UserController;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/1.0/exams", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.IMAGE_JPEG_VALUE,
		MediaType.APPLICATION_PDF_VALUE })
@Slf4j
public class ExaminationController {

	public static final String EXAM_NAME = "exam_name";
	public static final String FILE_NAME = "file_name";
	public static final String SUBJECT_TYPE = "subject_type";
	private ExamResponseService examService;

	@Autowired
	public void setExamService(ExamResponseService examService) {
		this.examService = examService;
	}

	@RequestMapping(path = "/qr/download", method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_PDF_VALUE })
	public void download(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = EXAM_NAME, required = false) String examName,
			@RequestParam(value = CourseController.SUBJECT_NAME, required = false) String subjectName,
			@RequestParam(value = UserController.USER_NAME, required = false) String username,
			@RequestParam(value = FILE_NAME, required = false) String fileName,
			@RequestParam(value = SUBJECT_TYPE, required = false) String subjectType) {
		if (StringUtils.isNotEmpty(username)) {
			examService.qrForStudentBySubjectNameAndType(request, response, examName, subjectName, subjectType,
					fileName, username);
		} else {
			examService.qrByExamNameAndSubjectNameAndType(request, response, examName, subjectName, subjectType,
					fileName);
		}
	}

	@RequestMapping(method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public FactoryResponse getExam(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = EXAM_NAME, required = false) String examName) {
		LOGGER.debug("request received.");
		FactoryResponse fr = examService.getExamByExamId(request, examName);
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	@RequestMapping(method = { RequestMethod.POST, RequestMethod.PUT }, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public FactoryResponse saveUpdateExam(HttpServletRequest request, HttpServletResponse response,
			@RequestBody ExaminationModel model) {
		LOGGER.debug("request received.");
		FactoryResponse fr = examService.createUpdateExam(request, model);
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	@RequestMapping(method = { RequestMethod.DELETE }, produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	public FactoryResponse deleteExamExam(HttpServletRequest request, HttpServletResponse response,
			@RequestBody ExaminationModel model) {
		LOGGER.debug("request received.");

		FactoryResponse fr = examService.createUpdateExam(request, model);
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

}
