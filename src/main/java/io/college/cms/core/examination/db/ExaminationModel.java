package io.college.cms.core.examination.db;

import java.util.Date;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.college.cms.core.courses.controller.CourseController;
import io.college.cms.core.courses.db.CourseModel.SubjectModel;
import io.college.cms.core.dynamodbloader.constants.TableNames;
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
@DynamoDBTable(tableName = TableNames.EXAMINATION_TABLE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExaminationModel {
	@DynamoDBHashKey(attributeName = ExaminationController.EXAM_NAME)
	private String examName;
	@DynamoDBAttribute(attributeName = CourseController.COURSE_NAME)
	private String courseName;
	@DynamoDBAttribute(attributeName = "exam_subjects")
	@Singular("withSubject")
	private List<ExamSubject> examSubjects;
	@DynamoDBAttribute(attributeName = "exam_start_date_time")
	private Date examStartDate;
	@DynamoDBAttribute(attributeName = "exam_end_date_time")
	private Date examEndDate;
	@DynamoDBAttribute(attributeName = "is_published")
	private boolean published;
	@DynamoDBAttribute(attributeName = "un_published")
	private boolean unPublished;

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
		private List<StudentResult> results;
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
		private String key;
		@JsonFormat(shape = Shape.STRING)
		private Date startTime;
		@JsonFormat(shape = Shape.STRING)
		private Date endTime;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@DynamoDBDocument
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class StudentResult {
		private String username;
		private Double marksScoredTheory;
		private Double marksScoredPractical;
		private Double marksScoredInternal;
		private Double marksScoredOthers;
		private Double totalScored;
		private String actionBy;
		@JsonFormat(shape = Shape.STRING)
		private Date updatedOn;
		private boolean review;
		private boolean disableQrLink;
		private boolean pass;
	}

}
