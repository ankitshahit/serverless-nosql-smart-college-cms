package io.college.cms.core.subjects.factory;

import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.NoSuchRecordException;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;
import io.college.cms.core.subjects.db.ISubjectRepo;
import io.college.cms.core.subjects.db.SubjectEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Ankit
 *
 */
@Service
public class SubjectFactory {
	private ISubjectRepo subjectRepo;

	@Autowired
	public void setSubjectRepo(ISubjectRepo subjectRepo) {
		this.subjectRepo = subjectRepo;
	}

	public SubjectEntity getSubjectEntity(String key) throws NoSuchRecordException, ValidationException {
		SubjectEntity val = subjectRepo.findOne(key);
		ValidationHandler.throwExceptionIfNull(val, ValidationHandler.EMPTY_STRING, ExceptionType.NO_RECORD_AVAILABLE);
		return val;
	}

	public SubjectEntity createSubject(SubjectEntity entity) throws NoSuchRecordException, ValidationException {
		subjectRepo.save(entity);
		return entity;
	}

	public void deleteSubject(SubjectEntity key) throws ValidationException {
		subjectRepo.delete(key);
	}
}
