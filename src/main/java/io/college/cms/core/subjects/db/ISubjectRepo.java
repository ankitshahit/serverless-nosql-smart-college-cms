package io.college.cms.core.subjects.db;

import org.springframework.stereotype.Repository;

import io.college.cms.core.dynamodbloader.service.DynamoRepository;
import io.college.cms.core.exception.ValidationException;

@Repository
public interface ISubjectRepo extends DynamoRepository {
	SubjectModel findBySubjectName(String subjectName);

	SubjectModel findOne(String key);

	void save(SubjectModel subject) throws ValidationException;

	void delete(SubjectModel entity) throws ValidationException;

	void delete(String subjectName) throws ValidationException;
}
