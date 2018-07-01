package io.college.cms.core.dynamodb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;

import io.college.cms.core.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class DynamoGenericService<K, V extends Object> implements CrudRepository<K, V> {
	private DynamoDBMapper dbMapper;
	private Class<K> k;

	/**
	 * @param dbMapper
	 * @param k
	 */
	@Autowired
	public void setDbMapper(DynamoDBMapper dbMapper) {
		this.dbMapper = dbMapper;
	}

	@Override
	public void setClass(Class<K> key) {
		this.k = key;
	}

	@Deprecated
	public void setK(Class<K> k) {
		setClass(k);
	}

	@Override
	public K findBy(V value) throws ApplicationException {
		K data = null;
		try {
			data = dbMapper.load(k, value);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex);
		}
		return data;
	}

	@Override
	public List<K> findAll() throws ApplicationException {
		List<K> records = null;
		try {
			DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

			records = dbMapper.scan(k, scanExpression);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex);
		}
		return records;
	}

	@Override
	public void save(K model) throws ApplicationException {
		try {
			dbMapper.save(model);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex);
		}

	}

	@Override
	public void delete(V value) throws ApplicationException {
		try {
			dbMapper.delete(value);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex);
		}
	}

	@Override
	public List<K> findBy(DynamoDBScanExpression scan) throws ApplicationException {
		try {
			return 	dbMapper.scan(k, scan);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex);
		}
	}

}
