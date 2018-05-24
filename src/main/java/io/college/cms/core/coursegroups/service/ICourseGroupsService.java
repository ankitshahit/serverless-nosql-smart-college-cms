package io.college.cms.core.coursegroups.service;

import io.college.cms.core.exception.ApplicationException;
import java.util.List;
import io.college.cms.core.exception.ValidationException;

public interface ICourseGroupsService {
	void addStudentUsernameByCourseAndSubject(String courseName, String subjectName, List<String> username)
			throws ValidationException, IllegalArgumentException, ApplicationException;

	void removeStudentUsernameByCourseAndSubject(String courseName, String subjectName, List<String> username)
			throws ValidationException, IllegalArgumentException, ApplicationException;

	void updateStudentCount(String courseName, String subjectName, long totalCount)
			throws ValidationException, IllegalArgumentException, ApplicationException;
}
