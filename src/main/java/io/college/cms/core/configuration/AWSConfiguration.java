package io.college.cms.core.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AWSConfiguration {
	private AppParams params;

	@Autowired
	public AWSConfiguration(AppParams params) {
		this.params = params;
	}

	@Bean
	public AWSCredentials awsBasicCredentials() {
		return new BasicAWSCredentials(params.getAwsAccessKey(), params.getAwsSecretKey());
	}

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {

		AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsBasicCredentials()));
		if (params.isUseLocalDynamoDb()) {
			builder = builder.withEndpointConfiguration(
					new EndpointConfiguration(params.getDynamoDbEndPoint(), Regions.AP_SOUTH_1.getName()));
		} else {
			builder.setRegion(Regions.AP_SOUTH_1.getName());
		}
		return builder.build();
	}

	@Bean
	public DynamoDBMapper dbMapper() {
		return new DynamoDBMapper(amazonDynamoDB());
	}

	@Bean
	public DynamoDB dynamoDb() {
		return new DynamoDB(amazonDynamoDB());
	}

	@Bean
	public AWSCognitoIdentityProvider awsCognito() {
		return AWSCognitoIdentityProviderClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsBasicCredentials())).withRegion(Regions.AP_SOUTH_1)
				.build();
	}

	@Bean
	public AmazonS3 awsS3() {
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTH_1)
				.withCredentials(new AWSStaticCredentialsProvider(awsBasicCredentials())).build();
		return s3Client;
	}
}
