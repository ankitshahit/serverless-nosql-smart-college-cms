package io.college.cms.core.examination.model;

import java.time.LocalDate;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.college.cms.core.application.LocalDateConverter;
import io.college.cms.core.courses.controller.CourseController;
import io.college.cms.core.courses.db.CourseModel.SubjectModel;
import io.college.cms.core.dynamodb.constants.Table;
import io.college.cms.core.examination.controller.ExaminationController;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@DynamoDBTable(tableName = Table.EXAMINATION_TABLE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExaminationModel {
	@DynamoDBHashKey(attributeName = ExaminationController.EXAM_NAME)
	private String examName;
	@DynamoDBAttribute(attributeName = CourseController.COURSE_NAME)
	private String courseName;
	@DynamoDBAttribute(attributeName = "exam_subjects")
	@Singular("withSubject")
	private List<ExamSubject> examSubjects;
	@DynamoDBTypeConverted(converter = LocalDateConverter.class)
	@DynamoDBAttribute(attributeName = "exam_start_Local_date_time")
	private LocalDate examStartDate;
	@DynamoDBTypeConverted(converter = LocalDateConverter.class)
	@DynamoDBAttribute(attributeName = "exam_end_Local_date_time")
	private LocalDate examEndDate;
	@DynamoDBAttribute(attributeName = "is_published")
	private boolean published;
	@DynamoDBAttribute(attributeName = "un_published")
	private boolean unPublished;
	@DynamoDBAttribute(attributeName = "semester")
	private String semester;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@DynamoDBDocument
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ExamSubject {
		@DynamoDBAttribute(attributeName = CourseController.SUBJECT_NAME)
		private String subjectName;
		@DynamoDBAttribute(attributeName = "subject_data")
		private SubjectModel subjectData;
		@DynamoDBAttribute(attributeName = "subject_results")
		@Singular("withResult")
		private List<StudentResultModel> results;
		@DynamoDBAttribute(attributeName = "subject_exam_metadata")
		@Singular("withTimeTable")
		private List<ExamSubjectTimeTable> subjectTimeTable;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@DynamoDBDocument
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ExamSubjectTimeTable {
		@DynamoDBAttribute(attributeName = "subject")
		private String subject;
		@DynamoDBAttribute(attributeName = "start_time")
		@DynamoDBTypeConverted(converter = LocalDateConverter.class)
		@JsonFormat(shape = Shape.STRING)
		private LocalDate startTime;

		@DynamoDBTypeConverted(converter = LocalDateConverter.class)
		@JsonFormat(shape = Shape.STRING)
		private LocalDate endTime;
		@DynamoDBAttribute
		private String subjectType;
	}

}
