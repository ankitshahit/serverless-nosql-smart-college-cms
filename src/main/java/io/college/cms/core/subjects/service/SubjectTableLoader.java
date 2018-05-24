package io.college.cms.core.subjects.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

import io.college.cms.core.dynamodbloader.service.ICollegeDynamoTableLoader;
import io.college.cms.core.subjects.db.SubjectModel;
import lombok.extern.slf4j.Slf4j;

/**
 * Dynamically manages a table in Dynamodb via the model provided to it.
 * 
 * @author Ankit
 *
 */
@Service
@Slf4j
public class SubjectTableLoader implements ICollegeDynamoTableLoader {
	private DynamoDBMapper dbMapper;
	private AmazonDynamoDB db;

	@Autowired
	public SubjectTableLoader(DynamoDBMapper dbMapper, AmazonDynamoDB db) {
		this.dbMapper = dbMapper;
		this.db = db;
	}

	/**
	 * It will create a table dynamically in dynamodb based upon skeleton of
	 * model.
	 */
	public void createTable() {
		LOGGER.info("{} table being created", SubjectModel.class.getName());
		CreateTableRequest subjectTable = dbMapper.generateCreateTableRequest(SubjectModel.class);
		ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(5L, 5L);
		subjectTable.setProvisionedThroughput(provisionedThroughput);
		db.createTable(subjectTable);
	}

	/**
	 * Deletes a table dynamically in dynamodb, beware this will delete all of
	 * the data along with it.
	 */
	@Override
	public void deleteTable() {
		// TODO: scope not decided to be implemented during at.
	}
}
