package io.college.cms.core.admission.services;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.college.cms.core.admission.model.ApplyAdmissionModel;
import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.dynamodb.service.DynamoGenericService;
import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class AdmissionResponseService {

	private DynamoGenericService<ApplyAdmissionModel, String> admissionDbService;

	@Autowired
	public AdmissionResponseService(DynamoGenericService<ApplyAdmissionModel, String> admissionDbService) {
		super();
		this.admissionDbService = admissionDbService;
		this.admissionDbService.setClass(ApplyAdmissionModel.class);
	}

	public FactoryResponse saveUpdate(ApplyAdmissionModel model) {
		FactoryResponse fr = FactoryResponse.builder().build();
		try {
			ValidationHandler.throwExceptionIfNull(model, "Request is empty", ExceptionType.VALIDATION_EXCEPTION);

			this.admissionDbService.save(model);

		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ex.getLocalizedMessage()).summaryMessage(SummaryMessageEnum.FAILURE)
					.build();

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Unable to publish/update admission details")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		fr = FactoryResponse.builder().summaryMessage(SummaryMessageEnum.SUCCESS).response("Successfully saved")
				.build();
		return fr;
	}

	@Cacheable(cacheNames = { "listAdmission" })
	public FactoryResponse findAllAdmissions() {
		FactoryResponse fr = FactoryResponse.builder().build();
		try {
			LOGGER.info("DB hit -> findAllJobs");
			List<ApplyAdmissionModel> records = this.admissionDbService.findAll();

			ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(records), "No Admissions found",
					ExceptionType.VALIDATION_EXCEPTION);
			fr = FactoryResponse.builder().summaryMessage(SummaryMessageEnum.SUCCESS).response(records).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ex.getLocalizedMessage()).summaryMessage(SummaryMessageEnum.FAILURE)
					.build();

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Unable to find admissions")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}

		return fr;
	}
}
