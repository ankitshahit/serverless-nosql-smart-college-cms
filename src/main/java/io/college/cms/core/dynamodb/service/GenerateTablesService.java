package io.college.cms.core.dynamodb.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

import io.college.cms.core.admission.model.AdmissionMetaModel;
import io.college.cms.core.admission.model.ApplyAdmissionModel;
import io.college.cms.core.announcement.model.AnnouncementModel;
import io.college.cms.core.configuration.AppParams;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.examination.model.ExaminationModel;
import io.college.cms.core.examination.model.StudentResultModel;
import io.college.cms.core.job.model.JobModel;
import io.college.cms.core.notification.model.ConfigureNotificationModel;
import io.college.cms.core.notification.model.NotificationModel;
import io.college.cms.core.upload.model.UploadModel;
import io.college.cms.core.user.model.UserModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j()
@Order(1000)
public class GenerateTablesService {
	private AppParams app;
	private DynamoDB dynamoDb;
	private DynamoDBMapper dbMapper;

	@Autowired
	public GenerateTablesService(AppParams app, DynamoDB db, DynamoDBMapper dbMapper) {
		this.app = app;
		this.dynamoDb = db;
		this.dbMapper = dbMapper;
	}

	@PostConstruct
	public void bind() {
		if (!app.isGenerateDbTables()) {
			return;
		}

		LOGGER.info("Generating tables.");
		try {
			create(CourseModel.class);
			create(ExaminationModel.class);
			create(UserModel.class);
			create(UploadModel.class);
			create(StudentResultModel.class);
			create(ApplyAdmissionModel.class);
			create(JobModel.class);
			create(AnnouncementModel.class);
			create(ConfigureNotificationModel.class);
			create(NotificationModel.class);
			create(AdmissionMetaModel.class);
			LOGGER.debug("Successfully created table.");
		} catch (InterruptedException e) {
			LOGGER.error("Unable to create table {}", e.getMessage());
		}
	}

	public void create(Class<?> table) throws InterruptedException {

		try {
			CreateTableRequest tableRequest = dbMapper.generateCreateTableRequest(table);
			tableRequest.withProvisionedThroughput(
					new ProvisionedThroughput().withReadCapacityUnits(5L).withWriteCapacityUnits(5L));

			Table dynamoDbTable = dynamoDb.createTable(tableRequest);

			LOGGER.info("waiting for db to be active");
			dynamoDbTable.waitForActive();
			LOGGER.debug("Table {} created successfully.", table.getName());
		} catch (InterruptedException ex) {
			throw ex;
		} catch (SdkClientException clientException) {
			LOGGER.error(clientException.getLocalizedMessage());
		} catch (Exception ex) {
			LOGGER.error(ex.getLocalizedMessage());
		}
	}
}
