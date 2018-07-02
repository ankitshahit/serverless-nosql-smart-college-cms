package io.college.cms.core.examination.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import io.college.cms.core.dynamodb.constants.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = Table.RESULTS)
public class ResultModel {
	private String username;
	private String courseName;
	private String semester;
	private String examName;
	private String subjectName;
	private String subjectType;
	private String actionBy;
	private String marks;
	// true == pass
	private boolean result;

}
