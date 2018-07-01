package io.college.cms.core.notification.services;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.dynamodb.service.DynamoGenericService;
import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;
import io.college.cms.core.notification.model.ConfigureNotificationModel;
import io.college.cms.core.notification.model.NotificationModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class NotificationResponseService {
	private ApplicationContext app;
	private DynamoGenericService<ConfigureNotificationModel, String> configNotificationDb;
	private DynamoGenericService<NotificationModel, String> notificationDb;

	/**
	 * @param app
	 * @param configNotificationDb
	 * @param notificationDb
	 */
	public NotificationResponseService(ApplicationContext app,
			DynamoGenericService<ConfigureNotificationModel, String> configNotificationDb,
			DynamoGenericService<NotificationModel, String> notificationDb) {
		super();
		this.app = app;
		this.configNotificationDb = configNotificationDb;
		this.notificationDb = notificationDb;
		this.configNotificationDb.setClass(ConfigureNotificationModel.class);
		this.notificationDb.setClass(NotificationModel.class);
	}

	public FactoryResponse saveNotificationConfiguration(ConfigureNotificationModel configureModel) {
		FactoryResponse fr = FactoryResponse.builder().build();
		try {
			ValidationHandler.throwExceptionIfNull(configureModel, null, ExceptionType.VALIDATION_EXCEPTION);
			this.configNotificationDb.save(configureModel);
			fr = FactoryResponse.builder().response("Saved/updated successfully.")
					.summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (ValidationException e) {
			LOGGER.error(e.getMessage());
			fr = FactoryResponse.builder().response(e.getMessage()).summaryMessage(SummaryMessageEnum.VALIDATION_ERROR)
					.build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			fr = FactoryResponse.builder().response("Unable to save configuration")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse findNotificationConfiguration(String username) {
		FactoryResponse.FactoryResponseBuilder fr = FactoryResponse.builder();
		try {

			ValidationHandler.throwExceptionStringEmpty(username, "username", ExceptionType.VALIDATION_EXCEPTION);
			DynamoDBScanExpression scan = new DynamoDBScanExpression();
			Condition condition = new Condition();
			condition.setComparisonOperator(ComparisonOperator.EQ);
			condition.withAttributeValueList(new AttributeValue().withS(username));
			scan.addFilterCondition("username", condition);
			List<ConfigureNotificationModel> config = configNotificationDb.findBy(scan);
			if (CollectionUtils.isEmpty(config) || config.get(0) == null) {
				fr.response(ConfigureNotificationModel.builder().build());
			} else {
				fr.response(config.get(0));
			}
			fr.summaryMessage(SummaryMessageEnum.SUCCESS);
		} catch (ValidationException e) {
			LOGGER.error(e.getMessage());
			fr.response(e.getMessage()).summaryMessage(SummaryMessageEnum.VALIDATION_ERROR);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			fr.response("Unable to fetch configuration").summaryMessage(SummaryMessageEnum.FAILURE);
		}
		return fr.build();
	}

	public FactoryResponse findAllNotifications() {
		FactoryResponse.FactoryResponseBuilder fr = FactoryResponse.builder();
		try {

			List<NotificationModel> config = notificationDb.findAll();
			if (CollectionUtils.isEmpty(config) || config.get(0) == null) {
				fr.response("No notifications!!!");
			} else {
				fr.response(config);
			}
			fr.summaryMessage(SummaryMessageEnum.SUCCESS);
		} catch (ValidationException e) {
			LOGGER.error(e.getMessage());
			fr.response(e.getMessage()).summaryMessage(SummaryMessageEnum.VALIDATION_ERROR);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			fr.response("Unable to fetch notifications").summaryMessage(SummaryMessageEnum.FAILURE);
		}
		return fr.build();
	}

	public FactoryResponse pushNotification(NotificationModel model) {
		FactoryResponse.FactoryResponseBuilder fr = FactoryResponse.builder();
		try {
			notificationDb.save(model);
			fr.response("successfully pushed").summaryMessage(SummaryMessageEnum.SUCCESS);
		} catch (ValidationException e) {
			LOGGER.error(e.getMessage());
			fr.response(e.getMessage()).summaryMessage(SummaryMessageEnum.VALIDATION_ERROR);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			fr.response("Unable to push notifications").summaryMessage(SummaryMessageEnum.FAILURE);
		}
		return fr.build();
	}
}
