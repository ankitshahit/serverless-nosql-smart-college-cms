package io.college.cms.core.announcement.services;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.college.cms.core.announcement.model.AnnouncementModel;
import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.dynamodb.service.DynamoGenericService;
import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;
import io.college.cms.core.notification.model.NotificationModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class AnnouncementResponseService {
	private DynamoGenericService<AnnouncementModel, String> announcementDbService;
	private DynamoGenericService<NotificationModel, String> notificationDbService;

	@Autowired
	public AnnouncementResponseService(DynamoGenericService<AnnouncementModel, String> announcementDbService) {
		super();
		this.announcementDbService = announcementDbService;
		this.announcementDbService.setClass(AnnouncementModel.class);
		this.notificationDbService.setClass(NotificationModel.class);
	}

	// @CachePut(cacheNames = "listAnnouncements", key =
	// "#model.announcementKey")
	public FactoryResponse saveUpdate(AnnouncementModel model) {
		FactoryResponse fr = FactoryResponse.builder().build();
		try {
			ValidationHandler.throwExceptionIfNull(model, "Request is empty", ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionStringEmpty(model.getSubject(), "contact information is empty",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionStringEmpty(model.getDescription(), "description is empty",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfNull(model.getScheduleDate(), "date is empty",
					ExceptionType.VALIDATION_EXCEPTION);

			this.announcementDbService.save(model);
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ex.getLocalizedMessage()).summaryMessage(SummaryMessageEnum.FAILURE)
					.build();

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Unable to publish/update announcement")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		fr = FactoryResponse.builder().summaryMessage(SummaryMessageEnum.SUCCESS).response("Successfully published")
				.build();
		return fr;
	}

	// @Cacheable(cacheNames = "listAnnouncements")
	public FactoryResponse findAllJobs() {
		FactoryResponse fr = FactoryResponse.builder().build();
		try {

			List<AnnouncementModel> records = this.announcementDbService.findAll();

			ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(records), "No announcements found",
					ExceptionType.VALIDATION_EXCEPTION);
			fr = FactoryResponse.builder().summaryMessage(SummaryMessageEnum.SUCCESS).response(records).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ex.getLocalizedMessage()).summaryMessage(SummaryMessageEnum.FAILURE)
					.build();

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Unable to fetch announcements")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}

		return fr;
	}
}
