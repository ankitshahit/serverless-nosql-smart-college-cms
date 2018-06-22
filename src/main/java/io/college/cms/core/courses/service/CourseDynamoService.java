package io.college.cms.core.courses.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.dynamodbloader.model.Paginate;
import io.college.cms.core.exception.ApplicationException;
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
	@Cacheable("courseName")
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
		//
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

	@Cacheable(cacheNames = "courseData")
	private List<CourseModel> loadCourses() throws ApplicationException, ValidationException {
		List<CourseModel> scanResult = new ArrayList<>();
		try {
			LOGGER.info("Db hit");
			DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
			scanResult = dbMapper.scan(CourseModel.class, scanExpression);
			if (CollectionUtils.isEmpty(scanResult)) {
				throw new ValidationException("No courses are available.");
			}
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex.getLocalizedMessage());
		}
		return scanResult;
	}

	@Override
	public List<CourseModel> limitAndPaginateCourses(Paginate paginate)
			throws ValidationException, ApplicationException {
		return loadCourses();
	}

}
