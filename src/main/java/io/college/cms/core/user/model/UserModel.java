package io.college.cms.core.user.model;

import java.util.Date;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import io.college.cms.core.dynamodbloader.constants.TableNames;
import io.college.cms.core.user.constants.UserAttributes;
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
@JsonIgnoreProperties
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
	@DynamoDBAttribute(attributeName = "user_metadata")
	@Singular(value = "withAttribute")
	private List<AttributeType> attributes;
	private Integer limit;
	private String paginationToken;

	@Builder
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@DynamoDBDocument
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class AttributeType {
		private UserAttributes name;
		private String value;
	}
}
