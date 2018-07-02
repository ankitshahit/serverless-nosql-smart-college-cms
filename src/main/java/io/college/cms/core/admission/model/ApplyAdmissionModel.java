package io.college.cms.core.admission.model;

import java.time.LocalDate;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;

import io.college.cms.core.application.LocalDateConverter;
import io.college.cms.core.dynamodb.constants.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@DynamoDBTable(tableName = Table.APPLY_ADMISSION_TABLE)
public class ApplyAdmissionModel {
	@DynamoDBHashKey
	private String username;
	@DynamoDBAttribute(attributeName = "course_name")
	private String courseName;
	private String semester;
	@DynamoDBTypeConverted(converter = LocalDateConverter.class)
	private LocalDate appliedOn;
	@DynamoDBAttribute()
	private boolean approved;
	@DynamoDBAttribute()
	private boolean rejected;
	@DynamoDBAttribute(attributeName = "fees_verification_done")
	private boolean feesVerficationDone;
	@DynamoDBAttribute(attributeName = "fees_verification_receipt_required")
	private boolean feesVerificationReceiptRequired;
	@DynamoDBAttribute(attributeName = "pending_requests")
	@Singular(value = "withRoleMember")
	private List<String> roleMembers;
	@DynamoDBAttribute(attributeName = "additional_comments")
	private String comments;
	private String actionBy;
	@DynamoDBTypeConverted(converter = LocalDateConverter.class)
	private LocalDate lastUpdatedOn;
	@Singular
	private List<String> subjects;
	
}
