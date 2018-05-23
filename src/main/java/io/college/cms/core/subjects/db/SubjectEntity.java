package io.college.cms.core.subjects.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import io.college.cms.core.TableNames;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = TableNames.SUBJECT_TABLE)
public class SubjectEntity {

	/**
	 * @param subjectName
	 */
	public SubjectEntity(@NonNull String subjectName) {
		super();
		this.subjectName = subjectName;
	}

	@DynamoDBHashKey(attributeName = "subject_name")
	private String subjectName;

	@DynamoDBAttribute(attributeName = "theory")
	@Builder.Default
	private Double theory = 0.0;
	@Builder.Default
	@DynamoDBAttribute(attributeName = "practical")
	private Double practical = 0.0;
	@Builder.Default
	@DynamoDBAttribute(attributeName = "internal")
	private Double internal = 0.0;
	@Builder.Default
	@DynamoDBAttribute(attributeName = "others")
	private Double others = 0.0;
	@Builder.Default
	@DynamoDBAttribute
	private Double total = 0.0;
	@Builder.Default
	@DynamoDBAttribute(attributeName = "theory_marks_required")
	private Double theoryMarksRequired = 0.0;
	@Builder.Default
	@DynamoDBAttribute(attributeName = "practical_marks_required")
	private Double practicalMarksRequired = 0.0;
	@Builder.Default
	@DynamoDBAttribute(attributeName = "internal_marks_required")
	private Double internalMarksRequired = 0.0;
	@Builder.Default
	@DynamoDBAttribute(attributeName = "others_marks_required")
	private Double othersMarksRequired = 0.0;
	@DynamoDBAttribute(attributeName = "show_theory")
	private boolean showTheory;
	@DynamoDBAttribute(attributeName = "show_practical")
	private boolean showPractical;
	@DynamoDBAttribute(attributeName = "show_internal")
	private boolean showInternal;
	@DynamoDBAttribute(attributeName = "show_others")
	private boolean showOthers;

}
