package io.college.cms.core.subjects.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

import io.college.cms.core.subjects.db.SubjectEntity;

@Service
public class SubjectDynamoTables {

	@Autowired
	public SubjectDynamoTables(DynamoDBMapper dbMapper, AmazonDynamoDB db) {
		CreateTableRequest subjectTable = dbMapper.generateCreateTableRequest(SubjectEntity.class);
		ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(5L, 5L);
		subjectTable.setProvisionedThroughput(provisionedThroughput);
		db.createTable(subjectTable);
	}
}
