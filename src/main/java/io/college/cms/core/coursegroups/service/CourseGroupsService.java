package io.college.cms.core.coursegroups.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.college.cms.core.courses.service.ICourseDbService;
import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;
import lombok.NonNull;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CourseGroupsService implements ICourseGroupsService {
	private ICourseDbService courseDbService;

	@Autowired
	public CourseGroupsService(ICourseDbService courseDbService) {
		this.courseDbService = courseDbService;
	}

	@Override
	public void addStudentUsernameByCourseAndSubject(@NonNull String courseName, @NonNull String subjectName,
			@NonNull List<String> username) throws ValidationException, IllegalArgumentException, ApplicationException {
		LOGGER.debug("{} is being added", username);
		try {
			var courseModel = courseDbService.findByCourseName(courseName);
			ValidationHandler.throwExceptionIfNull(courseModel, "No such course exists",
					ExceptionType.VALIDATION_EXCEPTION);

			var subjectGroupModel = courseModel.getCourseSubjectGroups();

			LOGGER.debug(new StringBuilder().append(subjectGroupModel == null).append(" for coursename ")
					.append(courseName).append(" and subject name ").append(subjectName).toString());

			ValidationHandler.throwExceptionIfNull(subjectGroupModel,
					String.valueOf(new StringBuilder().append("Subject group is empty for course").append(courseName)),
					ExceptionType.VALIDATION_EXCEPTION);

			var usernames = new ArrayList<String>();

			if (!CollectionUtils.isEmpty(subjectGroupModel.getStudentUsernames())) {
				usernames.addAll(subjectGroupModel.getStudentUsernames());
			}
			usernames.addAll(username);
			subjectGroupModel.setStudentUsernames(usernames);
			subjectGroupModel.setEnrolledStudents(CollectionUtils.size(usernames));
			courseModel.setCourseSubjectGroups(subjectGroupModel);
			courseDbService.saveCourse(courseModel);
		} catch (ValidationException | IllegalArgumentException ex) {
			throw ex;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex);
		}
	}

	@Override
	public void removeStudentUsernameByCourseAndSubject(@NonNull String courseName, @NonNull String subjectName,
			@NonNull List<String> username) throws ValidationException, IllegalArgumentException, ApplicationException {
		LOGGER.debug("{} is being removed", username);
		try {
			var courseModel = courseDbService.findByCourseName(courseName);

			ValidationHandler.throwExceptionIfNull(courseModel, "No such course exists",
					ExceptionType.VALIDATION_EXCEPTION);

			var subjectGroupModel = courseModel.getCourseSubjectGroups();

			LOGGER.debug(new StringBuilder().append(subjectGroupModel == null).append(" for coursename ")
					.append(courseName).append(" and subject name ").append(subjectName).toString());

			ValidationHandler.throwExceptionIfNull(subjectGroupModel,
					String.valueOf(new StringBuilder().append("Subject group is empty for course").append(courseName)),
					ExceptionType.VALIDATION_EXCEPTION);

			if (!CollectionUtils.isEmpty(subjectGroupModel.getStudentUsernames())) {
				subjectGroupModel.getStudentUsernames().removeAll(username);
			}

			subjectGroupModel.setEnrolledStudents(CollectionUtils.size(subjectGroupModel.getStudentUsernames()));
			courseModel.setCourseSubjectGroups(subjectGroupModel);
			courseDbService.saveCourse(courseModel);
		} catch (ValidationException | IllegalArgumentException ex) {
			throw ex;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex);
		}
	}

	@Override
	public void updateStudentCount(@NonNull String courseName, @NonNull String subjectName, long totalCount)
			throws ValidationException, IllegalArgumentException, ApplicationException {
		LOGGER.debug("{} count is being updated.", subjectName);
		try {
			var courseModel = courseDbService.findByCourseName(courseName);

			ValidationHandler.throwExceptionIfNull(courseModel, "No such course exists",
					ExceptionType.VALIDATION_EXCEPTION);
			var subjectGroupModel = courseModel.getCourseSubjectGroups();

			LOGGER.debug(new StringBuilder().append(subjectGroupModel == null).append(" for coursename ")
					.append(courseName).append(" and subject name ").append(subjectName).toString());

			ValidationHandler.throwExceptionIfNull(subjectGroupModel,
					String.valueOf(new StringBuilder().append("Subject group is empty for course").append(courseName)),
					ExceptionType.VALIDATION_EXCEPTION);

			subjectGroupModel.setEnrolledStudents(CollectionUtils.size(totalCount));
			courseDbService.saveCourse(courseModel);

		} catch (ValidationException | IllegalArgumentException ex) {
			throw ex;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex);
		}
	}

}
