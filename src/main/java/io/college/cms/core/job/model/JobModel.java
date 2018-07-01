package io.college.cms.core.job.model;

import java.time.LocalDate;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;

import io.college.cms.core.application.LocalDateConverter;
import io.college.cms.core.dynamodb.constants.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
	@DynamoDBTypeConverted(converter = LocalDateConverter.class)
	private LocalDate interViewDate;
	@DynamoDBAttribute
	private String contactInformation;
	@DynamoDBAttribute
	private boolean jobDisabled;

}