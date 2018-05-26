package io.college.cms.core.subjects.db;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;

@Data
@ToString

@AllArgsConstructor
@NoArgsConstructor
@Deprecated
@DynamoDBDocument
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubjectModel {

	/**
	 * @param subjectName
	 */
	public SubjectModel(@NonNull String subjectName) {
		super();
		this.subjectName = subjectName;
	}

	@JsonProperty(value = "subject_name")
	@DynamoDBHashKey(attributeName = "subject_name")
	private String subjectName;
	@DynamoDBAttribute(attributeName = "theory")

	private Double theory = 0.0;

	@DynamoDBAttribute(attributeName = "practical")
	private Double practical = 0.0;

	@DynamoDBAttribute(attributeName = "internal")
	private Double internal = 0.0;

	@DynamoDBAttribute(attributeName = "others")
	private Double others = 0.0;

	@DynamoDBAttribute
	private Double total = 0.0;

	@DynamoDBAttribute(attributeName = "theory_marks_required")
	private Double theoryMarksRequired = 0.0;

	@DynamoDBAttribute(attributeName = "practical_marks_required")
	private Double practicalMarksRequired = 0.0;

	@DynamoDBAttribute(attributeName = "internal_marks_required")
	private Double internalMarksRequired = 0.0;

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
	@DynamoDBAttribute(attributeName = "is_optional")
	private boolean isOptional;
	@DynamoDBAttribute(attributeName = "max_allocated_seats")

	private long maxAllowedStudents = (long) 0.0;
	@DynamoDBAttribute(attributeName = "enrolled_students_count")

	private long enrolledStudents = (long) 0.0;
	@DynamoDBAttribute(attributeName = "student_usernames")
	@Singular
	private List<String> studentUsernames;
	@DynamoDBIgnore
	private String courseName;

}
