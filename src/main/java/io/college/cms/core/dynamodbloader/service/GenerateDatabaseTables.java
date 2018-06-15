package io.college.cms.core.dynamodbloader.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

import io.college.cms.core.configuration.AppParams;
import io.college.cms.core.courses.db.CourseModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j

public class GenerateDatabaseTables {
	private AppParams app;
	private DynamoDB dynamoDb;

	@Autowired
	public GenerateDatabaseTables(AppParams app, DynamoDB db) {
		this.app = app;
	}

	@PostConstruct
	public void bind() {
		if (!app.isGenerateDbTables()) {
			return;
		}
		LOGGER.info("Generating tables.");
		try {
			create(CourseModel.class);
			LOGGER.debug("Successfully created table.");
		} catch (InterruptedException e) {
			LOGGER.error("Unable to create table {}", e.getMessage());
		}
	}

	public void create(Class<?> table) throws InterruptedException {

		CreateTableRequest request = new CreateTableRequest()
				.withTableName(table.getAnnotation(DynamoDBTable.class).tableName())
				.withKeySchema(new KeySchemaElement()
						.withAttributeName(table.getAnnotation(DynamoDBHashKey.class).attributeName())
						.withKeyType(KeyType.HASH))
				.withProvisionedThroughput(
						new ProvisionedThroughput().withReadCapacityUnits(5L).withWriteCapacityUnits(6L));

		Table dynamoDbTable = dynamoDb.createTable(request);

		dynamoDbTable.waitForActive();
	}
}
