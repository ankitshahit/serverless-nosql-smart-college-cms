package io.college.cms.core.subjects.service;

import io.college.cms.core.subjects.db.ISubjectRepo;
import io.college.cms.core.subjects.db.SubjectEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

@Service
public class SubjectDynamoService implements ISubjectRepo {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SubjectDynamoService.class);
	private DynamoDBMapper dynamoMapper;

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

	public void save(SubjectEntity subject) {
		try {
			dynamoMapper.save(subject);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
	}

	@Override
	public void delete(SubjectEntity entity) {
		try {
			dynamoMapper.save(entity);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
	}

	@Override
	public void delete(String subjectName) {
		try {

			dynamoMapper.save(new SubjectEntity(subjectName));
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
	}

}
