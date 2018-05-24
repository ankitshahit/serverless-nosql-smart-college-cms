package io.college.cms.core.subjects.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.subjects.db.ISubjectRepo;
import io.college.cms.core.subjects.db.SubjectModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SubjectDynamoService implements ISubjectRepo {
	private DynamoDBMapper dynamoMapper;

	@Autowired
	public SubjectDynamoService(DynamoDBMapper dbMapper) {
		this.dynamoMapper = dbMapper;
	}

	@Override
	public SubjectModel findBySubjectName(String subjectName) {
		SubjectModel subject = null;
		try {
			subject = dynamoMapper.load(SubjectModel.class, subjectName);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
		return subject;
	}

	@Override
	public SubjectModel findOne(String key) {
		return findBySubjectName(key);
	}

	public void save(SubjectModel subject) throws ValidationException {
		try {
			dynamoMapper.save(subject);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ValidationException("Unable to save/update record");
		}
	}

	@Override
	public void delete(SubjectModel entity) throws ValidationException {
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

			dynamoMapper.delete(new SubjectModel(subjectName));
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ValidationException("Unable to delete record");
		}
	}

}
