package io.college.cms.core.subjects.factory;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.college.cms.core.FactoryResponse;
import io.college.cms.core.SummaryMessageEnum;
import io.college.cms.core.exception.NoSuchRecordException;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.subjects.db.SubjectEntity;

@Service
public class SubjectResponseFactory {
	private SubjectFactory subjectFactory;

	@Autowired
	public SubjectResponseFactory(SubjectFactory factory) {
		this.subjectFactory = factory;
	}

	public FactoryResponse findBySubjectName(HttpServletRequest request, String subjectName) {
		FactoryResponse response = FactoryResponse.builder().build();

		try {
			response.setResponse(subjectFactory.getSubjectEntity(subjectName));
			response.setSummaryMessage(SummaryMessageEnum.SUCCESS);
		} catch (NoSuchRecordException | ValidationException ex) {
			response.setSummaryMessage(SummaryMessageEnum.VALIDATION_ERROR);
			response.setResponse(new String("No such subject found."));
		} catch (Exception e) {
			response.setSummaryMessage(SummaryMessageEnum.FAILURE);
			response.setResponse(new String("Application don't feel good!"));
		}
		return response;
	}

	public FactoryResponse createUpdateSubjects(HttpServletRequest request, SubjectEntity entity) {
		FactoryResponse response = FactoryResponse.builder().build();

		try {
			if (entity == null) {
				throw new ValidationException("No request json.");
			}
			response.setResponse(subjectFactory.createSubject(entity));
			response.setSummaryMessage(SummaryMessageEnum.SUCCESS);
		} catch (NoSuchRecordException | ValidationException ex) {
			response.setSummaryMessage(SummaryMessageEnum.VALIDATION_ERROR);
			response.setResponse(new String("Unable to create/save record"));
		} catch (Exception e) {
			response.setSummaryMessage(SummaryMessageEnum.FAILURE);
			response.setResponse(new String("Application don't feel good!"));
		}
		return response;
	}

	public FactoryResponse deleteSubject(HttpServletRequest request, String subjectName) {
		FactoryResponse response = FactoryResponse.builder().build();

		try {
			SubjectEntity entity = subjectFactory.getSubjectEntity(subjectName);
			subjectFactory.deleteSubject(entity);
			response.setResponse(new StringBuilder().append(subjectName).append(" is deleted."));
			response.setSummaryMessage(SummaryMessageEnum.SUCCESS);
		} catch (NoSuchRecordException | ValidationException ex) {
			response.setSummaryMessage(SummaryMessageEnum.VALIDATION_ERROR);
			response.setResponse(new String("No such subject exists."));
		} catch (Exception e) {
			response.setSummaryMessage(SummaryMessageEnum.FAILURE);
			response.setResponse(new String("Unable to delete subject"));
		}
		return response;
	}

}
