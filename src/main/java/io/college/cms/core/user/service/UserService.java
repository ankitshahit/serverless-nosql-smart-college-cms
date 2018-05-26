package io.college.cms.core.user.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.ResourceDeniedException;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;
import io.college.cms.core.user.model.UserModel;
import lombok.NonNull;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;

/**
 * Deals with user management.
 * 
 * @author Ankit
 *
 */
@Slf4j
@Service
public class UserService implements IUserService {

	private DynamoDBMapper dbMapper;

	public UserService(DynamoDBMapper mapper) {
		this.dbMapper = mapper;
	}

	@Override
	public List<UserModel> findAllUsers()
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {

		var users = new ArrayList<UserModel>();
		try {
			ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(users), "No users found.",
					ExceptionType.VALIDATION_EXCEPTION);

		} catch (ValidationException ex) {
			throw ex;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex);
		}
		return users;
	}

	@Override
	public void createUpdateUser(@NonNull UserModel user)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		try {
			ValidationHandler.throwExceptionIfNull(user.getUsername(), "user type is empty",
					ExceptionType.VALIDATION_EXCEPTION);

			dbMapper.save(user);
		} catch (ValidationException ex) {
			throw ex;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex);
		}
	}

	@Override
	public void deleteUser(@NonNull UserModel user)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		try {
			ValidationHandler.throwExceptionIfNull(user.getUsername(), "user name is empty",
					ExceptionType.VALIDATION_EXCEPTION);
			var dbVersionUser = findByUsername(user.getUsername());
			dbVersionUser.setDeletedOn(Date.from(Instant.now()));
			dbMapper.save(user);
		} catch (ValidationException ex) {
			throw ex;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex);
		}
	}

	@Override
	public UserModel findByUsername(@NonNull String username)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		var user = UserModel.builder().build();
		try {
			user = dbMapper.load(UserModel.class, username);

			ValidationHandler.throwExceptionIfNull(user, "No such user exists.", ExceptionType.VALIDATION_EXCEPTION);
		} catch (ValidationException ex) {
			throw ex;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex);
		}
		return user;
	}

	@Override
	public void deleteUser(String username)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		deleteUser(UserModel.builder().username(username).build());
	}

}
