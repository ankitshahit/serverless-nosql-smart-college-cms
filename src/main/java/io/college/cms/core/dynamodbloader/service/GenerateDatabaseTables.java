package io.college.cms.core.dynamodbloader.service;

import java.lang.reflect.Field;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import io.college.cms.core.configuration.AppParams;
import io.college.cms.core.examination.db.ExaminationModel;
import io.college.cms.core.user.model.UserModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

@Order(1000)
public class GenerateDatabaseTables {
	private AppParams app;
	private DynamoDB dynamoDb;

	@Autowired
	public GenerateDatabaseTables(AppParams app, DynamoDB db) {
		this.app = app;
		this.dynamoDb = db;
	}

	@PostConstruct
	public void bind() {
		if (!app.isGenerateDbTables()) {
			return;
		}

		LOGGER.info("Generating tables.");
		try {
			// create(CourseModel.class);
			create(ExaminationModel.class);
			create(UserModel.class);
			LOGGER.debug("Successfully created table.");
		} catch (InterruptedException e) {
			LOGGER.error("Unable to create table {}", e.getMessage());
		}
	}

	public void create(Class<?> table) throws InterruptedException {
		String attributeNameHashKey = "";
		for (Field field : table.getDeclaredFields()) {
			if (field.isAnnotationPresent(DynamoDBHashKey.class)) {
				attributeNameHashKey = field.getAnnotation(DynamoDBHashKey.class).attributeName();
			}
		}
		try {
			CreateTableRequest request = new CreateTableRequest()
					.withTableName(table.getAnnotation(DynamoDBTable.class).tableName())
					.withKeySchema(
							new KeySchemaElement().withAttributeName(attributeNameHashKey).withKeyType(KeyType.HASH))
					.withProvisionedThroughput(
							new ProvisionedThroughput().withReadCapacityUnits(6L).withWriteCapacityUnits(6L))
					.withAttributeDefinitions(new AttributeDefinition().withAttributeName(attributeNameHashKey)
							.withAttributeType(ScalarAttributeType.S));
			describeTable(table);
			Table dynamoDbTable = dynamoDb.createTable(request);

			dynamoDbTable.waitForActive();
		} catch (InterruptedException ex) {
			throw ex;
		} catch (Exception ex) {
			LOGGER.error(ex.getLocalizedMessage());
		}
	}

	public void describeTable(Class<?> classTable) {
		try {

			TableCollection collection = dynamoDb.listTables(classTable.getName());
			System.out.println(collection);
		} catch (AmazonServiceException ase) {
			if (!ase.getErrorCode().equalsIgnoreCase("ResourceNotFoundException"))
				throw ase;
		}
	}
}
