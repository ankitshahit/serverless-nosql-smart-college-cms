package io.college.cms.core.courses.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.dynamodb.model.Paginate;
import io.college.cms.core.dynamodb.service.DynamoGenericService;
import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.NoSuchRecordException;
import io.college.cms.core.exception.ValidationException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CourseDynamoService implements ICourseDbService {

	private DynamoGenericService<CourseModel, String> dbService;

	/**
	 * @param dbMapper
	 * @param dbService
	 */
	@Autowired
	public CourseDynamoService(DynamoGenericService<CourseModel, String> dbService) {
		super();
		this.dbService = dbService;
		this.dbService.setClass(CourseModel.class);
	}

	@Override
	public CourseModel findByCourseName(@NonNull() String courseName)
			throws NoSuchRecordException, NullPointerException {
		CourseModel course = null;
		try {
			course = dbService.findBy(courseName);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new NoSuchRecordException(e);
		}
		return  course;
	}

	@Override
	public void saveCourse(@NonNull CourseModel course) throws ValidationException {
		try {
			dbService.save(course);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ValidationException(e);
		}
	}

	@Override
	public void deleteCourse(@NonNull CourseModel course) throws ValidationException, NoSuchRecordException {

		try {
			dbService.delete(course.getCourseName());
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

	public List<CourseModel> loadCourses() throws ApplicationException, ValidationException {
		List<CourseModel> scanResult = new ArrayList<>();
		try {
			LOGGER.info("Db hit");
			scanResult = dbService.findAll();
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
