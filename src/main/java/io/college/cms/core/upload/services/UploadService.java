package io.college.cms.core.upload.services;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

import io.college.cms.core.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class UploadService {
	private ApplicationContext app;
	private AmazonS3 s3Client;

	@Autowired
	public UploadService(ApplicationContext app ) {
		this.app = app;
	}

	@Autowired
	public void setS3Client(AmazonS3 s3Client) {
		this.s3Client = s3Client;
	}

	public boolean hasFile(String tag, String filename) {
		try {

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
		return false;
	}

	public String upload(File file, String tag, String username) throws ApplicationException {
		// building an unique file upload location
		String filename = "";
		try {
			filename = new StringBuilder().append(username).append("_").append(tag).append("_").append(file.getName())
					.toString();
			PutObjectRequest request = this.app.getBean(PutObjectRequest.class);
			// need to get the root folder in s3 bucket.
			String key = new StringBuilder().append(request.getKey()).append(filename).toString();
			request.setKey(key);
			request.setFile(file);
			s3Client.putObject(request);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex.getLocalizedMessage());
		}
		return filename;
	}
}
