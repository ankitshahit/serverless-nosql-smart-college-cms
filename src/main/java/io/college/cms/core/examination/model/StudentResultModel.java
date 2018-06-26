package io.college.cms.core.examination.model;

import java.time.LocalDate;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel.DynamoDBAttributeType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.college.cms.core.dynamodb.constants.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBDocument
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@DynamoDBTable(tableName = Table.RESULTS)
public class StudentResultModel {

	@DynamoDBHashKey
	private String hashKey;
	@DynamoDBAttribute
	private String username;
	@DynamoDBAttribute
	private Double marksScoredTheory;
	@DynamoDBAttribute
	private Double marksScoredPractical;
	@DynamoDBAttribute
	private Double marksScoredInternal;
	@DynamoDBAttribute
	private Double marksScoredOthers;
	@DynamoDBAttribute
	private Double totalScored;
	@DynamoDBAttribute
	private String actionBy;
	@DynamoDBAttribute
	@DynamoDBTyped(DynamoDBAttributeType.S)
	@JsonFormat(shape = Shape.STRING)
	private LocalDate updatedOn;
	@DynamoDBAttribute
	private boolean review;
	@DynamoDBAttribute
	private boolean disableQrLink;
	@DynamoDBAttribute
	private boolean pass;

}
