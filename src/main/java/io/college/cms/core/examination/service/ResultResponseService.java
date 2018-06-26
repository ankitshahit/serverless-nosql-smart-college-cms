package io.college.cms.core.examination.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.examination.model.ExaminationModel.ExaminationModelBuilder;
import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ExceptionHandler;
import io.college.cms.core.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ResultResponseService {
	/**
	 * 
	 * @param request
	 * @param examModel
	 * @return
	 */
	public FactoryResponse updateQrMarks(HttpServletRequest request, ExaminationModelBuilder examModel) {
		FactoryResponse fr = null;
		try {
			if (false) {
				throw new ValidationException();
			}
			if (false) {
				throw new ApplicationException();
			}

			fr = FactoryResponse.builder().response("").summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (IllegalArgumentException ex) {
			LOGGER.error("One of required fields is empty.");
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Application dont feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}
}
