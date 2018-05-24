package io.college.cms.core.subjects.controller;

import java.util.Map;

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
import io.college.cms.core.subjects.db.SubjectModel;
import io.college.cms.core.subjects.factory.SubjectResponseFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController()
@RequestMapping(path = "/1.0/subjects", produces = MediaType.APPLICATION_JSON_VALUE)
public class SubjectController {
	public static final String SUBJECT_NAME = "subject_name";
	private SubjectResponseFactory subjectResponseFactory;

	@Autowired
	public void setSubjectFactory(SubjectResponseFactory subjectFactory) {
		this.subjectResponseFactory = subjectFactory;
	}

	@RequestMapping(method = { RequestMethod.GET })
	public FactoryResponse findBySubjectName(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = SUBJECT_NAME, required = false) String subjectName) {
		LOGGER.debug("Request");
		FactoryResponse fr = subjectResponseFactory.findBySubjectName(request, subjectName);
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	@RequestMapping(method = { RequestMethod.PUT, RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE)
	public FactoryResponse findBySubjectName(HttpServletRequest request, HttpServletResponse response,
			@RequestBody SubjectModel entity) {
		LOGGER.debug("{} Subject is passed", entity);
		FactoryResponse fr = subjectResponseFactory.createUpdateSubjects(request, entity);
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	@RequestMapping(method = { RequestMethod.DELETE })
	public FactoryResponse findBySubjectName(HttpServletRequest request, HttpServletResponse response,
			Map<String, String> json) {
		FactoryResponse fr = subjectResponseFactory.createUpdateSubjects(request,
				new SubjectModel(json.get(SUBJECT_NAME)));
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}
}
