package io.college.cms.core.upload.services;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.college.cms.core.dynamodb.service.CrudRepository;
import io.college.cms.core.dynamodb.service.DynamoGenericService;
import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.upload.model.UploadModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class UploadDynamoService implements CrudRepository<UploadModel, String> {
	private DynamoGenericService<UploadModel, String> dbService;

	/**
	 * @param dbService
	 */
	@Autowired
	public UploadDynamoService(DynamoGenericService<UploadModel, String> dbService) {
		super();
		this.dbService = dbService;
		this.dbService.setClass(UploadModel.class);
	}

	@Override
	public UploadModel findBy(String value) throws ApplicationException {
		try {
			return dbService.findBy(value);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex);
		}
	}

	@Override
	public List<UploadModel> findAll() throws ApplicationException {
		List<UploadModel> scanResult = null;
		try {
			scanResult = dbService.findAll();
			if (CollectionUtils.isEmpty(scanResult)) {
				throw new ApplicationException("No records found");
			}
			return scanResult;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex);
		}
	}

	@Override
	public void save(UploadModel model) throws ApplicationException {
		try {
			dbService.save(model);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex);
		}
	}

	@Override
	public void delete(String value) throws ApplicationException {
		throw new ApplicationException("CrudRepository -> Delete is not made available.");
	}

}
