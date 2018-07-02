package io.college.cms.core.notification.model;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import io.college.cms.core.dynamodb.constants.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = Table.CONFIGURE_NOTIFICATION)
public class ConfigureNotificationModel {

	private String hashKey;
	@DynamoDBHashKey
	private String username;
	private boolean email;
	private boolean myNotification;
	private List<String> checkBoxGroup;
}
