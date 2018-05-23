package io.college.cms.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

@Configuration
public class AWSConfiguration {
	@Value("${aws.secret.key}")
	private String awsSecretKey;
	@Value("${aws.access.key}")
	private String awsAccessKey;

	@Bean
	public AWSCredentials awsBasicCredentials() {
		return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
	}

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		return AmazonDynamoDBClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsBasicCredentials())).withEndpointConfiguration(
						new EndpointConfiguration("http://localhost:8000", Regions.AP_SOUTH_1.getName()))
				.build();
	}

	@Bean
	public DynamoDBMapper dbMapper() {
		return new DynamoDBMapper(amazonDynamoDB());
	}

	@Bean
	public DynamoDB dynamoDb() {
		return new DynamoDB(amazonDynamoDB());
	}
}
