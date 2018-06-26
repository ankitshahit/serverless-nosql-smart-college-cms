package io.college.cms.core.upload.services;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.configuration.AppParams;
import io.college.cms.core.upload.model.UploadModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class UploadResponseService {
	private UploadDynamoService uploadDbService;
	private UploadService uploadService;
	private AppParams params;

	/**
	 * @param dbMapper
	 * @param uploadService
	 */
	@Autowired
	public UploadResponseService(UploadDynamoService uploadDbService, UploadService uploadService) {
		super();
		this.uploadService = uploadService;
		this.uploadDbService = uploadDbService;
	}

	@Autowired
	public void setParams(AppParams params) {
		this.params = params;
	}

	public FactoryResponse uploadFile(File file, String username, String tag) {
		FactoryResponse fr = FactoryResponse.builder().build();
		try {
			String filename = uploadService.upload(file, tag, username);

			this.uploadDbService.save(UploadModel.builder().filename(file.getName()).tag(tag).username(username)
					.s3BucketLink(params.getHost().concat(filename)).key("key_".concat(filename)).build());
			fr = FactoryResponse.builder()
					.response(new StringBuilder().append(params.getS3Host()).append(filename).toString())
					.summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().summaryMessage(SummaryMessageEnum.FAILURE)
					.response("Unable to upload to server!").build();
		}
		return fr;
	}
}
