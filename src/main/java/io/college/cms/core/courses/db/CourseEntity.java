package io.college.cms.core.courses.db;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.college.cms.core.TableNames;
import io.college.cms.core.subjects.db.SubjectEntity;
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
public class CourseEntity {

	/**
	 * @param courseName
	 */
	public CourseEntity(String courseName) {
		super();
		this.courseName = courseName;
	}

	@DynamoDBHashKey(attributeName = "course_name")
	private String courseName;
	@DynamoDBAttribute(attributeName = "subjects")
	private List<String> subjects;
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
	@DynamoDBIgnore
	@JsonIgnore
	@Singular("subjectResolvedMap")
	private List<SubjectEntity> subjectsResolved;

}
