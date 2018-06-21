package io.college.cms.core.courses.service;

import java.util.List;

import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.dynamodbloader.model.Paginate;
import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.NoSuchRecordException;
import io.college.cms.core.exception.ValidationException;

public interface ICourseDbService {
	CourseModel findByCourseName(String courseName) throws NoSuchRecordException;

	void saveCourse(CourseModel course) throws ValidationException;

	void deleteCourse(CourseModel course) throws ValidationException, NoSuchRecordException;

	void deleteCourse(String courseName) throws ValidationException, NoSuchRecordException;

	List<CourseModel> limitAndPaginateCourses(Paginate paginate) throws ValidationException, ApplicationException;
}
