package io.college.cms.core.examination.db;

import java.util.Date;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import io.college.cms.core.courses.controller.CourseController;
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

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@DynamoDBDocument
	public static class ExamSubject {
		@DynamoDBAttribute(attributeName = CourseController.SUBJECT_NAME)
		private String subjectName;
		@DynamoDBAttribute(attributeName = "exam_start_date_time")
		private Date startDate;
		@DynamoDBAttribute(attributeName = "exam_end_date_time")
		private Date endDate;
	}
}
