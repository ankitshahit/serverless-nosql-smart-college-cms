package io.college.cms.core.dynamodb.service;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

import io.college.cms.core.exception.ApplicationException;

public interface CrudRepository<K, V> {
	default void setClass(Class<K> key) {
		return;
	};

	public K findBy(V value) throws ApplicationException;

	public List<K> findAll() throws ApplicationException;

	public void save(K model) throws ApplicationException;

	public void delete(V value) throws ApplicationException;

	default List<K> findBy(DynamoDBScanExpression scan) throws ApplicationException {
		return null;
	};
}
