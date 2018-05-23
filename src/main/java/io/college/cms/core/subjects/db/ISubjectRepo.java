package io.college.cms.core.subjects.db;

import io.college.cms.core.DynamoRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface ISubjectRepo extends DynamoRepository {
	SubjectEntity findBySubjectName(String subjectName);

	SubjectEntity findOne(String key);

	void save(SubjectEntity subject);

	void delete(SubjectEntity entity);

	void delete(String subjectName);
}
