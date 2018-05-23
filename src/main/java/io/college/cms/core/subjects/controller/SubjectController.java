package io.college.cms.core.subjects.controller;

import io.college.cms.core.FactoryResponse;
import io.college.cms.core.subjects.factory.SubjectResponseFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	public FactoryResponse findBySubjectName(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(SUBJECT_NAME) String subjectName) {
		FactoryResponse fr = subjectResponseFactory.findBySubjectName(request,
				subjectName);
		response.setStatus(fr.getSummaryMessage().code().value());

		return fr;
	}
}
