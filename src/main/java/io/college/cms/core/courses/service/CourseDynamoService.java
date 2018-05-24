package io.college.cms.core.courses.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.dynamodbloader.model.Paginate;
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
	public CourseModel findByCourseName(@NonNull() String courseName)
			throws NoSuchRecordException, NullPointerException {
		CourseModel course = null;
		try {
			course = dbMapper.load(CourseModel.class, courseName);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new NoSuchRecordException(e);
		}
		return (CourseModel) course;
	}

	@Override
	public void saveCourse(@NonNull CourseModel course) throws ValidationException {
		try {
			dbMapper.save(course);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ValidationException(e);
		}

	}

	@Override
	public void deleteCourse(@NonNull CourseModel course) throws ValidationException, NoSuchRecordException {

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
			deleteCourse(new CourseModel(courseName));
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ValidationException(e);
		}

	}

	// TODO: limit and pagination of courses is pending.
	@Override
	public List<CourseModel> limitAndPaginateCourses(Paginate paginate) {
		try {

		} catch (Exception e) {

		}
		return null;
	}

}
