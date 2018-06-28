package io.college.cms.core.job.model;

import java.time.LocalDate;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel.DynamoDBAttributeType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped;

import io.college.cms.core.dynamodb.constants.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@DynamoDBTable(tableName = Table.JOB_TABLE)
public class JobModel {
	@DynamoDBHashKey
	@DynamoDBAutoGeneratedKey
	private String jobKey;
	@DynamoDBAttribute
	private String location;
	@DynamoDBAttribute(attributeName = "posted_by")
	private String postedBy;
	@DynamoDBAttribute
	private String salary;
	@DynamoDBAttribute
	private String title;
	@DynamoDBAttribute
	private String description;
	@DynamoDBAttribute
	private String additionalInformation;
	@DynamoDBTyped(DynamoDBAttributeType.S)
	private LocalDate interViewDate;
	@DynamoDBAttribute
	private String contactInformation;
	@DynamoDBAttribute
	private boolean jobDisabled;

}
