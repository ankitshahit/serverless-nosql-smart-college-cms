package io.college.cms.core.notification.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import io.college.cms.core.dynamodb.constants.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@DynamoDBTable(tableName = Table.CONFIGURE_NOTIFICATION)
public class ConfigureNotificationModel {

	private String hashKey;
	@DynamoDBHashKey
	private String username;
	private String courseName;
	private boolean email;
	private boolean myNotification;
}
