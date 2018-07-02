package io.college.cms.core.admission.model;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.college.cms.core.dynamodb.constants.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@DynamoDBTable(tableName = Table.ADMISSION_TABLE_META)

public class AdmissionMetaModel {
	@DynamoDBHashKey
	private String courseName;
	private String semester;
	private boolean requireFeesVerification;
	private boolean showEnrolledOutOf;
	private String fees;
	private String additionalInformation;
	private boolean admissionClosed;
	@Singular
	private List<String> users;
}
