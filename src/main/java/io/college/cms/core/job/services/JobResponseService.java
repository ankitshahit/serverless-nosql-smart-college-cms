package io.college.cms.core.job.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.dynamodb.service.DynamoGenericService;
import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;
import io.college.cms.core.job.model.JobModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class JobResponseService {

	private DynamoGenericService<JobModel, String> jobDbService;

	@Autowired
	public JobResponseService(DynamoGenericService<JobModel, String> jobDbService) {
		super();
		this.jobDbService = jobDbService;
		this.jobDbService.setClass(JobModel.class);
	}

	public FactoryResponse saveUpdate(JobModel model) {
		FactoryResponse fr = FactoryResponse.builder().build();
		try {
			ValidationHandler.throwExceptionIfNull(model, "Request is empty", ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionStringEmpty(model.getContactInformation(), "contact information is empty",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionStringEmpty(model.getDescription(), "description is empty",
					ExceptionType.VALIDATION_EXCEPTION);
			this.jobDbService.save(model);
			List<JobModel> dataList = new ArrayList<>();
			dataList.add(model);
			FactoryResponse dataCache = FactoryResponse.builder().response(dataList).build();
			putSaveUpdateCache(dataCache);
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ex.getLocalizedMessage()).summaryMessage(SummaryMessageEnum.FAILURE)
					.build();

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Unable to publish/update job")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		fr = FactoryResponse.builder().summaryMessage(SummaryMessageEnum.SUCCESS).response("Successfully saved")
				.build();
		return fr;
	}

	// @CachePut(cacheNames = "listJobs")
	private void putSaveUpdateCache(FactoryResponse fr) {
		// TODO: empty method just to invoke cache update
		LOGGER.info("saved.");

	}

	// @Cacheable(cacheNames = { "listJobs" })
	public FactoryResponse findAllJobs() {
		FactoryResponse fr = FactoryResponse.builder().build();
		try {
			LOGGER.info("DB hit -> findAllJobs");
			List<JobModel> records = this.jobDbService.findAll();

			ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(records), "No jobs found",
					ExceptionType.VALIDATION_EXCEPTION);
			fr = FactoryResponse.builder().summaryMessage(SummaryMessageEnum.SUCCESS).response(records).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ex.getLocalizedMessage()).summaryMessage(SummaryMessageEnum.FAILURE)
					.build();

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Unable to find jobs").summaryMessage(SummaryMessageEnum.FAILURE)
					.build();
		}

		return fr;
	}
}
