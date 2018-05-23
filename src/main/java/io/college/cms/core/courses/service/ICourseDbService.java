package io.college.cms.core.courses.service;

import java.util.List;

import io.college.cms.core.Paginate;
import io.college.cms.core.courses.db.CourseEntity;
import io.college.cms.core.exception.NoSuchRecordException;
import io.college.cms.core.exception.ValidationException;

public interface ICourseDbService {
	CourseEntity findByCourseName(String courseName) throws NoSuchRecordException;

	void saveCourse(CourseEntity course) throws ValidationException;

	void deleteCourse(CourseEntity course) throws ValidationException, NoSuchRecordException;

	void deleteCourse(String courseName) throws ValidationException, NoSuchRecordException;

	List<CourseEntity> limitAndPaginateCourses(Paginate paginate) throws ValidationException;
}
