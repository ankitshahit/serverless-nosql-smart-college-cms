package io.college.cms.core.courses.db;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.college.cms.core.dynamodbloader.constants.TableNames;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;

@Builder
@Data
@DynamoDBTable(tableName = TableNames.COURSES_TABLE)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.USE_DEFAULTS)
public class CourseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param courseName
	 */
	public CourseModel(String courseName) {
		super();
		this.courseName = courseName;
	}

	@DynamoDBHashKey(attributeName = "course_name")
	private String courseName;
	@DynamoDBAttribute(attributeName = "enrolled_students")
	@Builder.Default
	private Long enrolledStudents = (long) 0.0;
	@DynamoDBAttribute(attributeName = "max_students_allowed")
	@Builder.Default
	private Long maxStudentsAllowed = (long) 0.0;

	@DynamoDBAttribute(attributeName = "is_archive")
	private boolean isArchive;
	@DynamoDBAttribute(attributeName = "subjects")
	@Singular("withSubject")
	private List<SubjectModel> subjects;
	@DynamoDBAttribute(attributeName = "course_students")
	@Singular("withUsername")
	private List<String> users;
	@DynamoDBAttribute(attributeName = "description")
	private String description;
	@JsonProperty("current_enrolled_out_of")
	@DynamoDBAttribute(attributeName = "outOfCount")
	private String outOf;

	public String getOutOf() {
		if (StringUtils.isEmpty(outOf)) {
			outOf = new StringBuilder().append(getEnrolledStudents()).append("/").append(getMaxStudentsAllowed())
					.toString();
		}
		return outOf;
	}

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	@DynamoDBDocument
	public static class SubjectModel {

		/**
		 * @param subjectName
		 */
		public SubjectModel(@NonNull String subjectName) {
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
		@DynamoDBAttribute(attributeName = "is_optional")
		private boolean optional;
		@DynamoDBAttribute(attributeName = "max_allocated_seats")
		@Builder.Default
		private long maxAllowedStudents = (long) 0.0;
		@DynamoDBAttribute(attributeName = "enrolled_students_count")

		@Builder.Default
		private long enrolledStudents = (long) 0.0;
		@DynamoDBAttribute(attributeName = "student_usernames")
		@Singular("withUsername")
		private List<String> studentUsernames;

		@Builder.Default
		@DynamoDBAttribute(attributeName = "semester")
		private String semester = "Sem 1";
	}

}
