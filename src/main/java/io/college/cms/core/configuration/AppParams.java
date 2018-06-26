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
	@Value("${dynamodb-endpoint:http://localhost:3000}")
	private String dynamoDbEndPoint;
	@Value("${host:http://localhost:8080}")
	private String host;
	@Value("${generate-db-tables:false}")
	private boolean generateDbTables;
	@Value("${qna-maker-key:no-key}")
	private String qnaMakerKey;
	@Value("${cognito-client-id:no-key}")
	private String cognitoClientId;
	@Value("${s3.bucket.name:value}")
	private String s3BucketName;
	@Value("${s3.bucket.folder:folder}")
	private String s3BucketFolder;
	@Value("${s3.bucket.host:http://bucket}")
	private String s3Host;
}
