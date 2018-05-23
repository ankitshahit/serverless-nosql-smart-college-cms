package io.college.cms.core.subjects.factory;

import io.college.cms.core.FactoryResponse;
import io.college.cms.core.SummaryMessageEnum;
import io.college.cms.core.exception.NoSuchRecord;
import io.college.cms.core.exception.ValidationException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubjectResponseFactory {
	private SubjectFactory subjectFactory;

	@Autowired
	public SubjectResponseFactory(SubjectFactory factory) {
		this.subjectFactory = factory;
	}

	public FactoryResponse findBySubjectName(HttpServletRequest request,
			String subjectName) {
		FactoryResponse response = new FactoryResponse();

		try {
			response.setResponse(subjectFactory.getSubjectEntity(subjectName));
			response.setSummaryMessage(SummaryMessageEnum.SUCCESS);
		} catch (NoSuchRecord | ValidationException ex) {
			response.setSummaryMessage(SummaryMessageEnum.VALIDATION_ERROR);
			response.setResponse(new String("No such subject found."));
		} catch (Exception e) {
			response.setSummaryMessage(SummaryMessageEnum.FAILURE);
			response.setResponse(new String("Application don't feel good!"));
		}
		return response;
	}
}
