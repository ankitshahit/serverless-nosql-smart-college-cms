package io.college.cms.core.upload.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.configuration.AppParams;
import io.college.cms.core.upload.model.UploadModel;
import io.college.cms.core.user.constants.UserGroups;
import io.college.cms.core.user.model.UserModel;
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
					.s3BucketLink(new StringBuilder().append(params.getS3Host()).append(username).append("_")
							.append(tag).append("_").append(file.getName()).toString())
					.key("key_".concat(filename)).build());
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

	public FactoryResponse findAll(String username, UserGroups groups) {
		FactoryResponse.FactoryResponseBuilder fr = FactoryResponse.builder();
		try {
			List<UploadModel> records = new ArrayList<>();
			if (UserGroups.STAFF == groups || UserGroups.ADMIN == groups) {
				records = this.uploadDbService.findAll();
			} else {

				DynamoDBScanExpression scan = new DynamoDBScanExpression();
				Condition condition = new Condition();
				condition.setComparisonOperator(ComparisonOperator.EQ);
				condition.withAttributeValueList(new AttributeValue().withS(username));
				scan.addFilterCondition("username", condition);
				records = this.uploadDbService.findBy(scan);
			}
			fr.response(records).summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr.summaryMessage(SummaryMessageEnum.FAILURE).response("Unable to fetch from server!").build();
		}
		return fr.build();
	}
}
