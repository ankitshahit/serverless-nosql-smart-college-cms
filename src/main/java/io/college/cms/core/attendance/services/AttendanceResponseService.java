package io.college.cms.core.attendance.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ConditionalOperator;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.Face;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.IndexFacesResult;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.amazonaws.util.IOUtils;
import com.vaadin.server.FileResource;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.application.excel.services.ExcelService;
import io.college.cms.core.attendance.model.AttendanceModel;
import io.college.cms.core.configuration.AppParams;
import io.college.cms.core.dynamodb.service.DynamoGenericService;
import io.college.cms.core.user.model.FaceModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class AttendanceResponseService {
	private AppParams params;
	private ApplicationContext app;
	private DynamoGenericService<AttendanceModel, String> attendanceDbService;
	private DynamoGenericService<FaceModel, String> faceDbService;
	private AmazonRekognition provider;

	/**
	 * @param params
	 * @param app
	 * @param attendanceDbService
	 * @param faceDbService
	 */
	@Autowired
	public AttendanceResponseService(AppParams params, ApplicationContext app,
			DynamoGenericService<AttendanceModel, String> attendanceDbService,
			DynamoGenericService<FaceModel, String> faceDbService) {
		super();
		this.params = params;
		this.app = app;
		this.attendanceDbService = attendanceDbService;
		this.faceDbService = faceDbService;
		provider = app.getBean(AmazonRekognition.class);
		attendanceDbService.setClass(AttendanceModel.class);
		faceDbService.setClass(FaceModel.class);
	}

	@Async
	public void tag(FileResource resource, Consumer<List<String>> taggedStudents, Runnable successListener) {
		try {
			SearchFacesByImageRequest imageRequest = app.getBean(SearchFacesByImageRequest.class);
			imageRequest.withImage(getImage(resource.getSourceFile()));
			imageRequest.setFaceMatchThreshold(60.0f);
			SearchFacesByImageResult result = provider.searchFacesByImage(imageRequest);
			Set<String> usernames = new HashSet<>();
			// Display results
			List<FaceMatch> faceDetails = result.getFaceMatches();
			for (FaceMatch match : faceDetails) {
				Face face = match.getFace();
				if (face.getConfidence() < 60)
					continue;
				FaceModel faceModel = faceDbService.findBy(face.getFaceId());
				if (faceModel != null) {
					usernames.add(faceModel.getUsername());
				}

			}
			List<String> users = new ArrayList<>();
			users.addAll(usernames);
			taggedStudents.accept(users);
			successListener.run();
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage());
		}
	}

	@Async
	public void tag(File file, String username, Consumer<String> errorMsg, Runnable successListener) {
		try {
			IndexFacesRequest faces = app.getBean(IndexFacesRequest.class);
			faces.withImage(getImage(file));
			IndexFacesResult response = provider.indexFaces(faces);
			if (CollectionUtils.size(response.getFaceRecords()) < 1) {
				errorMsg.accept("No face records found, please upload an individual photo");
				return;
			} else if (CollectionUtils.size(response.getFaceRecords()) > 1) {
				errorMsg.accept("More than one faces found, please upload individual photo.");
				return;
			} else {
				try {
					FaceModel model = this.faceDbService.findBy(response.getFaceRecords().get(0).getFace().getFaceId());
					if (model != null) {
						errorMsg.accept("Face is already registered with another user.");
						return;
					} else {
						SearchFacesByImageRequest imageRequest = app.getBean(SearchFacesByImageRequest.class);
						imageRequest.withImage(getImage(file));
						imageRequest.setFaceMatchThreshold(60.0f);
						SearchFacesByImageResult result = provider.searchFacesByImage(imageRequest);
						if (CollectionUtils.size(result.getFaceMatches()) > 0) {
							errorMsg.accept(
									"Face is features matches  with another user. Confidence of matching result is: "
											+ result.getSearchedFaceConfidence());
							return;
						}

					}

				} catch (Exception ex) {
					// meaning this is a new face
				}
			}
			FaceRecord faceRecord = response.getFaceRecords().get(0);
			String faceId = faceRecord.getFace().getFaceId();
			this.faceDbService.save(FaceModel.builder().faceId(faceId).username(username).build());

			successListener.run();
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage());
		}
	}

	public FactoryResponse saveAttendance(AttendanceModel model) {
		FactoryResponse.FactoryResponseBuilder fr = FactoryResponse.builder();
		try {
			if (CollectionUtils.isEmpty(model.getUsers()))
				fr.response("No students provided for tagging").summaryMessage(SummaryMessageEnum.FAILURE);

			for (String student : model.getUsers()) {
				model.setUsername(student);
				attendanceDbService.save(model);
			}

			fr.response("Saved successfully!").summaryMessage(SummaryMessageEnum.SUCCESS);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage());
			fr.response("Unable to save attendance").summaryMessage(SummaryMessageEnum.FAILURE);
		}
		return fr.build();
	}

	public Image getImage(File file) {
		ByteBuffer sourceImageBytes = null;

		try (InputStream inputStream = new FileInputStream(file)) {
			sourceImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Image().withBytes(sourceImageBytes);
	}

	@Async
	public void download(Consumer<File> downloadFileListener, Consumer<Float> progressListener,
			Consumer<String> errorListener, Runnable successListener, AttendanceModel attendance) {
		try {
			ExcelService excelService = app.getBean(ExcelService.class);
			excelService.setHeaderTitle("Sr.no", "Course", "Semester", "Subject", "Date", "Student", "Attendance by",
					"Tagged on");
			List<AttendanceModel> records = new ArrayList<>();
			progressListener.accept(5.0f);
			if (StringUtils.isNotEmpty(attendance.getUsername())) {
				DynamoDBScanExpression scan = new DynamoDBScanExpression();
				Condition condition = new Condition();
				condition.setComparisonOperator(ComparisonOperator.EQ);
				condition.withAttributeValueList(new AttributeValue().withS(attendance.getUsername()));
				scan.addFilterCondition("username", condition);
				progressListener.accept(15.0f);
				records = attendanceDbService.findBy(scan);
			} else {
				DynamoDBScanExpression scan = new DynamoDBScanExpression();
				scan.withFilterConditionEntry("courseName",
						new Condition().withComparisonOperator(ComparisonOperator.EQ)
								.withAttributeValueList(new AttributeValue().withS(attendance.getCourseName())))
						.withConditionalOperator(ConditionalOperator.AND)
						.withFilterConditionEntry("subjectName",
								new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(
										new AttributeValue().withS(attendance.getSubjectName())))
						.withConditionalOperator(ConditionalOperator.AND).withFilterConditionEntry("semester",
								new Condition().withComparisonOperator(ComparisonOperator.EQ)
										.withAttributeValueList(new AttributeValue().withS(attendance.getSemester())));
				progressListener.accept(15.0f);
				records = attendanceDbService.findBy(scan);

			}
			progressListener.accept(25.0f);
			if (CollectionUtils.isEmpty(records)) {
				errorListener.accept("<p>No records found</p>");
				return;
			}
			progressListener.accept(29.0f);
			float currentProgress = 29.0f;
			List<String> data = new ArrayList<>();
			int count = 0;
			for (AttendanceModel model : records) {
				data.add(String.valueOf(++count));
				data.add(model.getCourseName());
				data.add(model.getSemester());
				data.add(model.getSubjectName());
				data.add(String.valueOf(model.getAttendanceDate()));
				data.add(model.getUsername());
				data.add(model.getActionBy());
				data.add(String.valueOf(model.getAddedOn()));				
				progressListener.accept(currentProgress + 0.1f);
			}
			progressListener.accept(currentProgress + 5.0f);
			excelService.write(data, downloadFileListener, errorListener, progressListener, successListener);

		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage());
			errorListener.accept("Unable to process file.");
		}
	}
}
