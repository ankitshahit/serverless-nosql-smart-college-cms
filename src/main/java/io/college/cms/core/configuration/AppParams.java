package io.college.cms.core.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Data;

@Service
@Data
public class AppParams {
	@Value("${aws.secret.key}")
	private String awsSecretKey;
	@Value("${aws.access.key}")
	private String awsAccessKey;
	@Value("${use-local-dynamo-db:false}")
	private boolean useLocalDynamoDb;
	@Value("${cognito-user-pool-id}")
	private String cognitoUserPoolId;
	@Value("${dynamodb-endpoint:http://localhost:8000}")
	private String dynamoDbEndPoint;
}
