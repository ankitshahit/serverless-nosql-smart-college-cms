package io.college.cms.core.subjects.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.subjects.db.ISubjectRepo;
import io.college.cms.core.subjects.db.SubjectEntity;

@Service
public class SubjectDynamoService implements ISubjectRepo {
	private static final Logger LOGGER = LoggerFactory.getLogger(SubjectDynamoService.class);
	private DynamoDBMapper dynamoMapper;

	@Autowired
	public SubjectDynamoService(DynamoDBMapper dbMapper) {
		this.dynamoMapper = dbMapper;
	}

	@Override
	public SubjectEntity findBySubjectName(String subjectName) {
		SubjectEntity subject = null;
		try {
			subject = dynamoMapper.load(SubjectEntity.class, subjectName);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
		return subject;
	}

	@Override
	public SubjectEntity findOne(String key) {
		return findBySubjectName(key);
	}

	public void save(SubjectEntity subject) throws ValidationException {
		try {
			dynamoMapper.save(subject);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ValidationException("Unable to save/update record");
		}
	}

	@Override
	public void delete(SubjectEntity entity) throws ValidationException {
		try {
			dynamoMapper.delete(entity);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ValidationException("Unable to delete record");
		}
	}

	@Override
	public void delete(String subjectName) throws ValidationException {
		try {

			dynamoMapper.delete(new SubjectEntity(subjectName));
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ValidationException("Unable to delete record");
		}
	}

}
