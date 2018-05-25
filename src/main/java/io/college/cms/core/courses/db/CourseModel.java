package io.college.cms.core.courses.db;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import io.college.cms.core.dynamodbloader.constants.TableNames;
import io.college.cms.core.subjects.db.SubjectModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Builder
@Data
@DynamoDBTable(tableName = TableNames.COURSES_TABLE)
@AllArgsConstructor
@NoArgsConstructor
public class CourseModel {

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
	@DynamoDBAttribute(attributeName = "total_duration")
	@Builder.Default
	private Double totalDuration = 0.0;
	@DynamoDBAttribute(attributeName = "is_archive")
	private boolean isArchive;
	@DynamoDBAttribute(attributeName = "subjects")
	@Singular("subject")
	private List<SubjectModel> subjects;
	@DynamoDBAttribute(attributeName = "subject_groups")
	@Singular("group")
	private List<CourseSubjectGroupsModel> courseSubjectGroups;

	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class CourseSubjectGroupsModel {
		@DynamoDBAttribute(attributeName = "subject_name")
		private String subjectName;
		@DynamoDBAttribute(attributeName = "is_optional")
		private boolean isOptional;

		@DynamoDBAttribute(attributeName = "max_allocated_seats")
		@Builder.Default
		private long maxAllowedStudents = (long) 0.0;
		@DynamoDBAttribute(attributeName = "enrolled_students_count")
		@Builder.Default
		private long enrolledStudents = (long) 0.0;
		@DynamoDBAttribute(attributeName = "student_usernames")
		@Singular
		private List<String> studentUsernames;
		@DynamoDBIgnore
		private String courseName;
	}
}
