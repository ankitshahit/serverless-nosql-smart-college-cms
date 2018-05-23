package io.college.cms.core.subjects.db;

import org.springframework.stereotype.Repository;

import io.college.cms.core.DynamoRepository;
import io.college.cms.core.exception.ValidationException;

@Repository
public interface ISubjectRepo extends DynamoRepository {
	SubjectEntity findBySubjectName(String subjectName);

	SubjectEntity findOne(String key);

	void save(SubjectEntity subject) throws ValidationException;

	void delete(SubjectEntity entity) throws ValidationException;

	void delete(String subjectName) throws ValidationException;
}
