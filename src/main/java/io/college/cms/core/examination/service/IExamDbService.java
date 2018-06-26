package io.college.cms.core.examination.service;

import java.util.List;

import io.college.cms.core.examination.model.ExaminationModel;
import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ResourceDeniedException;
import io.college.cms.core.exception.ValidationException;

public interface IExamDbService {

	ExaminationModel findByExamName(String examName)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException;

	void saveUpdateExam(ExaminationModel model)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException;

	void deleteExam(String examName)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException;

	List<ExaminationModel> findAllExams()
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException;
}