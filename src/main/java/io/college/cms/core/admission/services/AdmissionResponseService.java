package io.college.cms.core.admission.services;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.college.cms.core.admission.model.AdmissionMetaModel;
import io.college.cms.core.admission.model.ApplyAdmissionModel;
import io.college.cms.core.admission.model.ApplyFeesModel;
import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.dynamodb.service.DynamoGenericService;
import io.college.cms.core.examination.model.FeesModel;
import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class AdmissionResponseService {

	private DynamoGenericService<ApplyAdmissionModel, String> admissionDbService;
	private DynamoGenericService<AdmissionMetaModel, String> admissionMetaDbService;
	private DynamoGenericService<FeesModel, String> feesMetaDbService;
	private DynamoGenericService<ApplyFeesModel, String> feesDbService;

	@Autowired
	public AdmissionResponseService(DynamoGenericService<ApplyAdmissionModel, String> admissionDbService,
			DynamoGenericService<AdmissionMetaModel, String> admissionMetaDbService,
			DynamoGenericService<FeesModel, String> feesMetaDbService,
			DynamoGenericService<ApplyFeesModel, String> feesDbService) {
		super();
		this.admissionDbService = admissionDbService;
		this.admissionMetaDbService = admissionMetaDbService;
		this.feesMetaDbService = feesMetaDbService;
		this.feesDbService = feesDbService;
		this.admissionDbService.setClass(ApplyAdmissionModel.class);
		this.admissionMetaDbService.setClass(AdmissionMetaModel.class);
		feesMetaDbService.setClass(FeesModel.class);
		feesDbService.setClass(ApplyFeesModel.class);
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

	public FactoryResponse saveUpdate(ApplyFeesModel model) {
		FactoryResponse fr = FactoryResponse.builder().build();
		try {
			ValidationHandler.throwExceptionIfNull(model, "Request is empty", ExceptionType.VALIDATION_EXCEPTION);

			this.feesDbService.save(model);

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

	public FactoryResponse saveUpdate(FeesModel model) {
		FactoryResponse fr = FactoryResponse.builder().build();
		try {
			ValidationHandler.throwExceptionIfNull(model, "Request is empty", ExceptionType.VALIDATION_EXCEPTION);

			this.feesMetaDbService.save(model);

		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ex.getLocalizedMessage()).summaryMessage(SummaryMessageEnum.FAILURE)
					.build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Unable to publish/update fees details")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		fr = FactoryResponse.builder().summaryMessage(SummaryMessageEnum.SUCCESS).response("Successfully saved")
				.build();
		return fr;
	}

	public FactoryResponse saveUpdate(AdmissionMetaModel model) {
		FactoryResponse fr = FactoryResponse.builder().build();
		try {
			ValidationHandler.throwExceptionIfNull(model, "Request is empty", ExceptionType.VALIDATION_EXCEPTION);

			this.admissionMetaDbService.save(model);

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

	public FactoryResponse findAdmissionMetaDetails(String courseName) {
		FactoryResponse fr = FactoryResponse.builder().build();
		try {
			LOGGER.info("DB hit -> findAdmissionMetaDetails");
			AdmissionMetaModel records = this.admissionMetaDbService.findBy(courseName);

			ValidationHandler.throwExceptionIfTrue(records == null, "No Admissions found",
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

	public FactoryResponse findMetaAdmissions() {
		FactoryResponse fr = FactoryResponse.builder().build();
		try {
			LOGGER.info("DB hit -> findMetaAdmissions");
			List<AdmissionMetaModel> records = this.admissionMetaDbService.findAll();

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

	public FactoryResponse findFeesMetaDetails(String courseName) {
		FactoryResponse fr = FactoryResponse.builder().build();
		try {
			LOGGER.info("DB hit -> findFeesMetaDetails");
			FeesModel records = this.feesMetaDbService.findBy(courseName);

			ValidationHandler.throwExceptionIfTrue(records == null, "No Admissions found",
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

	public FactoryResponse findAllFeesMeta() {
		FactoryResponse fr = FactoryResponse.builder().build();
		try {
			LOGGER.info("DB hit -> findAllFeesMeta");
			List<FeesModel> records = this.feesMetaDbService.findAll();

			ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(records), "No fees meta found",
					ExceptionType.VALIDATION_EXCEPTION);
			fr = FactoryResponse.builder().summaryMessage(SummaryMessageEnum.SUCCESS).response(records).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ex.getLocalizedMessage()).summaryMessage(SummaryMessageEnum.FAILURE)
					.build();

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Unable to find fees meta")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}

		return fr;
	}

	public FactoryResponse findAllAdmissions() {
		FactoryResponse fr = FactoryResponse.builder().build();
		try {
			LOGGER.info("DB hit -> findAllAdmissions");
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
