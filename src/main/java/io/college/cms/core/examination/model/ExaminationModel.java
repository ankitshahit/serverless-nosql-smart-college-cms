package io.college.cms.core.examination.model;

import java.time.LocalDate;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.college.cms.core.application.LocalDateConverter;
import io.college.cms.core.courses.controller.CourseController;
import io.college.cms.core.dynamodb.constants.Table;
import io.college.cms.core.examination.controller.ExaminationController;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	@DynamoDBTypeConverted(converter = LocalDateConverter.class)
	private LocalDate examStartDate;
	@DynamoDBTypeConverted(converter = LocalDateConverter.class)
	private LocalDate examEndDate;
	@DynamoDBAttribute(attributeName = "is_published")
	private boolean published;
	@DynamoDBAttribute(attributeName = "un_published")
	private boolean unPublished;
	@DynamoDBAttribute(attributeName = "semester")
	private String semester;

}
