package io.college.cms.core.admission.model;

import java.time.LocalDate;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import io.college.cms.core.dynamodb.constants.Table;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
@DynamoDBTable(tableName = Table.APPLY_ADMISSION_TABLE)
public class ApplyAdmissionModel {
	@DynamoDBHashKey
	private String username;
	@DynamoDBAttribute(attributeName = "course_name")
	private String courseName;
	@DynamoDBAttribute(attributeName = "applied_on ")
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
}
