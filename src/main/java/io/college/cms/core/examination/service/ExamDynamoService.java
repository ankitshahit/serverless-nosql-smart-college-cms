package io.college.cms.core.examination.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import io.college.cms.core.courses.service.ICourseDbService;
import io.college.cms.core.examination.db.ExaminationModel;
import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ExceptionHandler;
import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.ResourceDeniedException;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;
import lombok.NonNull;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExamDynamoService implements IExamDbService {
	private ICourseDbService courseDbService;
	private DynamoDBMapper dbMapper;

	@Autowired
	public ExamDynamoService( DynamoDBMapper dbMapper) {
		this.dbMapper = dbMapper;
	}

	/* (non-Javadoc)
	 * @see io.college.cms.core.examination.service.IExamDbService#findByExamName(java.lang.String)
	 */
	@Override@Cacheable
	public ExaminationModel findByExamName(@NonNull String examName)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		var examData = ExaminationModel.builder().build();
		try {
			examData = dbMapper.load(ExaminationModel.class, examName);
			ValidationHandler.throwExceptionIfNull(examData, "", ExceptionType.VALIDATION_EXCEPTION);
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			throw new ValidationException(ExceptionHandler.beautifyStackTrace(ex));
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ExceptionHandler.beautifyStackTrace(ex));
		}
		return examData;
	}

	/* (non-Javadoc)
	 * @see io.college.cms.core.examination.service.IExamDbService#saveUpdateExam(io.college.cms.core.examination.db.ExaminationModel)
	 */
	@Override
	public void saveUpdateExam(@NonNull ExaminationModel model)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {

		try {
			// we won't be checking exam subjects for now 
			//TODO: have to verify
			// each subject whether they working or not
			ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(model.getExamSubjects()),
					"No exam subjects provided.", ExceptionType.VALIDATION_EXCEPTION);

			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(model.getCourseName()), "NO course provided.",
					ExceptionType.VALIDATION_EXCEPTION);

			// loading only to verify whether such course exists. In-case course
			// doesn't exists an error will be thrown by the target.
			courseDbService.findByCourseName(model.getCourseName());

			dbMapper.save(model);

		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			throw new ValidationException(ExceptionHandler.beautifyStackTrace(ex));
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ExceptionHandler.beautifyStackTrace(ex));
		}
	}

	/* (non-Javadoc)
	 * @see io.college.cms.core.examination.service.IExamDbService#deleteExam(java.lang.String)
	 */
	@Override
	public void deleteExam(@NonNull String examName)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		try {

			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(examName), "NO course provided.",
					ExceptionType.VALIDATION_EXCEPTION);
			// we won't be calling to verify whether such exam exists as in-case
			// it don't exist an exception will be thrown by dynamodb.
			// Reducing db calls to dynamodb
			dbMapper.delete(ExaminationModel.builder().examName(examName).build());

		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			throw new ValidationException(ExceptionHandler.beautifyStackTrace(ex));
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ExceptionHandler.beautifyStackTrace(ex));
		}
	}
}