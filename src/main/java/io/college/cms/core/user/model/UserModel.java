package io.college.cms.core.user.model;

import java.util.Date;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import io.college.cms.core.dynamodbloader.constants.TableNames;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = TableNames.PROFILE_TABLE)
public class UserModel {
	@DynamoDBHashKey(attributeName = "username")
	private String username;
	@DynamoDBAttribute(attributeName = "is_active")
	private boolean isActive;
	@DynamoDBAttribute(attributeName = "created_on")
	@JsonFormat(shape = Shape.STRING)
	private Date createdOn;
	@DynamoDBAttribute(attributeName = "deleted_on")
	@JsonFormat(shape = Shape.STRING)
	private Date deletedOn;
	@DynamoDBAttribute(attributeName = "user_attributes_metadata")
	@Singular(value = "metadata")
	private List<String> userMetaData;
	@DynamoDBAttribute(attributeName = "user_data")
	@Singular(value = "data")
	private List<AttributeType> userData;
	@DynamoDBAttribute(attributeName = "user_type")
	private String userType;	

	@Builder
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@DynamoDBDocument
	public static class AttributeType {
		private String name;
		private String value;
	}
}
