package io.college.cms.core.examination.model;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import io.college.cms.core.dynamodb.constants.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = Table.FEES_META_TABLE)
public class FeesModel {

	private String hashKey;
	@DynamoDBHashKey
	private String courseName;
	private String semester;
	private String fees;
	private String contactInformation;
	private boolean cancelAdmission;
	private String link;
	private String offlinePaymentDetails;
	@Singular
	private List<String> users;
}
