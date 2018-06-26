package io.college.cms.core.user.model;

import java.time.LocalDate;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.college.cms.core.dynamodb.constants.Table;
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
@DynamoDBTyped()
@DynamoDBTable(tableName = Table.PROFILE_TABLE)
@JsonIgnoreProperties
public class UserModel {
	@DynamoDBAttribute(attributeName = "first_name")
	private String firstName;
	@DynamoDBAttribute(attributeName = "middle_name")
	private String middleName;
	@DynamoDBAttribute(attributeName = "last_name")
	private String lastName;
	@DynamoDBAttribute(attributeName = "token")
	private String token;
	@DynamoDBHashKey(attributeName = "username")
	private String username;
	@DynamoDBHashKey(attributeName = "email")
	private String email;
	@DynamoDBAttribute(attributeName = "is_active")
	private boolean isActive;
	@DynamoDBAttribute(attributeName = "date_of_birth")
	private LocalDate dateOfBirth;
	@DynamoDBAttribute(attributeName = "gender")
	// TODO: convert this to enum
	private String gender;
	@DynamoDBAttribute(attributeName = "created_on")
	@JsonFormat(shape = Shape.STRING)
	private LocalDate createdOn;
	@DynamoDBAttribute(attributeName = "deleted_on")
	@JsonFormat(shape = Shape.STRING)
	private LocalDate deletedOn;

	@DynamoDBAttribute(attributeName = "user_metadata")
	@Singular(value = "withAttribute")
	private List<AttributeType> attributes;
	private Integer limit;
	private String paginationToken;
	@DynamoDBIgnore
	private String userStatus;

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
