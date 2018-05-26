package io.college.cms.core.subjects.factory;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.exception.NoSuchRecordException;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.subjects.db.SubjectModel;

/**
 * Subject no longer lives as a seperate functionality, it has been merged into
 * {@link com.college.cms.core.courses.service.CourseResponseService}}
 * 
 * @author Ankit
 *
 */

@Deprecated
@Service
public class SubjectResponseFactory {
	private SubjectFactory subjectFactory;

	@Autowired
	public SubjectResponseFactory(SubjectFactory factory) {
		this.subjectFactory = factory;
	}

	@Deprecated
	public FactoryResponse findBySubjectName(HttpServletRequest request, String subjectName) {
		FactoryResponse response = FactoryResponse.builder().build();

		try {
			response.setResponse(subjectFactory.getSubjectEntity(subjectName));
			response.setSummaryMessage(SummaryMessageEnum.SUCCESS);
		} catch (ValidationException ex) {
			response.setSummaryMessage(SummaryMessageEnum.VALIDATION_ERROR);
			response.setResponse(new String("No such subject found."));
		} catch (Exception e) {
			response.setSummaryMessage(SummaryMessageEnum.FAILURE);
			response.setResponse(new String("Application don't feel good!"));
		}
		return response;
	}

	@Deprecated
	public FactoryResponse createUpdateSubjects(HttpServletRequest request, SubjectModel entity) {
		FactoryResponse response = FactoryResponse.builder().build();

		try {
			if (entity == null) {
				throw new ValidationException("No request json.");
			}
			response.setResponse(subjectFactory.createSubject(entity));
			response.setSummaryMessage(SummaryMessageEnum.SUCCESS);
		} catch (ValidationException ex) {
			response.setSummaryMessage(SummaryMessageEnum.VALIDATION_ERROR);
			response.setResponse(new String("Unable to create/save record"));
		} catch (Exception e) {
			response.setSummaryMessage(SummaryMessageEnum.FAILURE);
			response.setResponse(new String("Application don't feel good!"));
		}
		return response;
	}

	@Deprecated
	public FactoryResponse deleteSubject(HttpServletRequest request, String subjectName) {
		FactoryResponse response = FactoryResponse.builder().build();

		try {
			SubjectModel entity = subjectFactory.getSubjectEntity(subjectName);
			subjectFactory.deleteSubject(entity);
			response.setResponse(new StringBuilder().append(subjectName).append(" is deleted."));
			response.setSummaryMessage(SummaryMessageEnum.SUCCESS);
		} catch (ValidationException ex) {
			response.setSummaryMessage(SummaryMessageEnum.VALIDATION_ERROR);
			response.setResponse(new String("No such subject exists."));
		} catch (Exception e) {
			response.setSummaryMessage(SummaryMessageEnum.FAILURE);
			response.setResponse(new String("Unable to delete subject"));
		}
		return response;
	}

}
