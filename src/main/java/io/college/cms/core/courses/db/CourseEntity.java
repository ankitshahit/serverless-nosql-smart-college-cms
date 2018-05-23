package io.college.cms.core.courses.db;

import io.college.cms.core.TableNames;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = TableNames.COURSES_TABLE)
public class CourseEntity {

	/**
	 * @param courseName
	 */
	public CourseEntity(String courseName) {
		super();
		this.courseName = courseName;
	}

	/**
	 * 
	 */
	public CourseEntity() {
		super();
	}

	@DynamoDBHashKey(attributeName = "course_name")
	private String courseName;
	@DynamoDBAttribute(attributeName = "subjects")
	private List<String> subjects;
	@DynamoDBAttribute(attributeName = "enrolled_students")
	private Long enrolledStudents = (long) 0.0;
	@DynamoDBAttribute(attributeName = "max_students_allowed")
	private Long maxStudentsAllowed = (long) 0.0;
	@DynamoDBAttribute(attributeName = "total_duration")
	private Double totalDuration = 0.0;
	private boolean isArchive;

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public List<String> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}

	public Long getEnrolledStudents() {
		return enrolledStudents;
	}

	public void setEnrolledStudents(Long enrolledStudents) {
		this.enrolledStudents = enrolledStudents;
	}

	public Long getMaxStudentsAllowed() {
		return maxStudentsAllowed;
	}

	public void setMaxStudentsAllowed(Long maxStudentsAllowed) {
		this.maxStudentsAllowed = maxStudentsAllowed;
	}

	public Double getTotalDuration() {
		return totalDuration;
	}

	public void setTotalDuration(Double totalDuration) {
		this.totalDuration = totalDuration;
	}

	public boolean isArchive() {
		return isArchive;
	}

	public void setArchive(boolean isArchive) {
		this.isArchive = isArchive;
	}

}
