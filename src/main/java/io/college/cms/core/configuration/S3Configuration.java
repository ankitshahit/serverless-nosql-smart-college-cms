package io.college.cms.core.configuration;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Configuration
public class S3Configuration {
	private AppParams params;

	@Autowired
	public S3Configuration(AppParams params) {
		this.params = params;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public PutObjectRequest putObjectRequest() {
		return new PutObjectRequest(params.getS3BucketName(), params.getS3BucketFolder(), new File(""));
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public GetObjectRequest getObjectRequest() {
		return new GetObjectRequest(params.getS3BucketName(), params.getS3BucketFolder());
	}

}
