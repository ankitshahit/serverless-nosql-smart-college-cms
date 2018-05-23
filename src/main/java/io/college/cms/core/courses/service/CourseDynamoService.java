package io.college.cms.core.courses.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import io.college.cms.core.Paginate;
import io.college.cms.core.courses.db.CourseEntity;
import io.college.cms.core.exception.NoSuchRecordException;
import io.college.cms.core.exception.ValidationException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CourseDynamoService implements ICourseDbService {
	private DynamoDBMapper dbMapper;

	@Autowired
	public CourseDynamoService(DynamoDBMapper dbMapper) {
		this.dbMapper = dbMapper;
	}

	@Override
	public CourseEntity findByCourseName(@NonNull() String courseName)
			throws NoSuchRecordException, NullPointerException {
		CourseEntity course = null;
		try {
			course = dbMapper.load(CourseEntity.class, courseName);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new NoSuchRecordException(e);
		}
		return (CourseEntity) course;
	}

	@Override
	public void saveCourse(@NonNull CourseEntity course) throws ValidationException {
		try {
			dbMapper.save(course);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ValidationException(e);
		}

	}

	@Override
	public void deleteCourse(@NonNull CourseEntity course) throws ValidationException, NoSuchRecordException {

		try {
			dbMapper.delete(course);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ValidationException(e);
		}
	}

	@Override
	public void deleteCourse(@NonNull String courseName) throws ValidationException, NoSuchRecordException {
		try {
			deleteCourse(new CourseEntity(courseName));
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ValidationException(e);
		}

	}

	// TODO: limit and pagination of courses is pending.
	@Override
	public List<CourseEntity> limitAndPaginateCourses(Paginate paginate) {
		try {

		} catch (Exception e) {

		}
		return null;
	}

}
