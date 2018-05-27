package io.college.cms.core.user.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.UserType;

import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.ResourceDeniedException;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;
import io.college.cms.core.user.constants.UserAttributes;
import io.college.cms.core.user.model.UserModel;
import lombok.NonNull;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Primary
public class UserCognitoService implements IUserService {
	private ApplicationContext app;
	private AWSCognitoIdentityProvider identityProvider;

	@Autowired
	public UserCognitoService(AWSCognitoIdentityProvider provider) {
		this.identityProvider = provider;
	}

	@Autowired
	public void setApp(ApplicationContext app) {
		this.app = app;
	}

	/**
	 * Maps out cognito data to UserType to UserModel instance
	 * 
	 * @param users
	 * @return
	 */
	public static UserModel valueOf(UserType user) {
		var userBuilder = UserModel.builder();
		userBuilder.username(user.getUsername()).isActive(user.getEnabled());
		userBuilder.attributes(valueOf(user.getAttributes()).getAttributes());
		return userBuilder.build();
	}

	/**
	 * Maps out cognito attributes to UserModel.AttributeType instance
	 * 
	 * @param attributes
	 * @return
	 */
	public static UserModel valueOf(List<AttributeType> attributes) {
		var userBuilder = UserModel.builder();
		for (AttributeType attributeType : attributes) {
			if (attributeType == null || StringUtils.isEmpty(attributeType.getName())) {
				continue;
			}
			for (UserAttributes attribute : UserAttributes.values()) {
				if (!attribute.val().equalsIgnoreCase(attributeType.getName())) {
					continue;
				}
				userBuilder.withAttribute(
						UserModel.AttributeType.builder().name(attribute).value(attributeType.getValue()).build());
				break;
			}

		}
		return userBuilder.build();
	}

	@Override
	@Cacheable
	public UserModel findByUsername(@NonNull String username)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		var userBuilder = UserModel.builder();
		try {
			AdminGetUserRequest request = app.getBean(AdminGetUserRequest.class);
			request.setUsername(username);
			AdminGetUserResult result = identityProvider.adminGetUser(request);

			userBuilder.username(result.getUsername()).isActive(result.getEnabled());

			for (AttributeType attributeType : result.getUserAttributes()) {
				if (attributeType == null || StringUtils.isEmpty(attributeType.getName())) {
					continue;
				}
				for (UserAttributes attribute : UserAttributes.values()) {
					if (!attribute.val().equalsIgnoreCase(attributeType.getName())) {
						continue;
					}
					userBuilder.withAttribute(
							UserModel.AttributeType.builder().name(attribute).value(attributeType.getValue()).build());
					break;
				}

			}
		} catch (Exception e) {
			LOGGER.error("{} " + e.getMessage(), username);
			throw new ApplicationException(e);
		}
		return userBuilder.build();
	}

	@Override
	public List<UserModel> findAllUsers(UserModel model)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		var users = new ArrayList<UserModel>();
		try {
			ListUsersRequest request = app.getBean(ListUsersRequest.class);

			int limit = 10;
			LOGGER.debug("limit : {}", limit);
			if ((model.getLimit() != null && model.getLimit() <= 0)) {
				limit = model.getLimit();
			}
			request.setLimit(limit);
			if (StringUtils.isNotEmpty(model.getPaginationToken())) {
				request.setPaginationToken(model.getPaginationToken());
			}
			ListUsersResult result = identityProvider.listUsers(request);

			result.getUsers().forEach(user -> {
				var data = valueOf(user);
				data.setPaginationToken(result.getPaginationToken());
				users.add(data);
			});
			ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(users), "No users available",
					ExceptionType.VALIDATION_EXCEPTION);
		} catch (ValidationException e) {
			LOGGER.error(e.getMessage());
			throw new ValidationException(e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ApplicationException(e);
		}
		return users;
	}

	@Override
	public void createUpdateUser(@NonNull UserModel user)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		try {
			ValidationHandler.throwExceptionIfNull(user.getUsername(), "username not provided",
					ExceptionType.VALIDATION_EXCEPTION);

			ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(user.getAttributes()),
					"User attributes not provided.", ExceptionType.VALIDATION_EXCEPTION);

			var attributes = new ArrayList<AttributeType>();
			for (io.college.cms.core.user.model.UserModel.AttributeType attr : user.getAttributes()) {
				AttributeType attribute = new AttributeType();
				attribute.setName(StringUtils.lowerCase(attr.getName().toString()));
				attribute.setValue(attr.getValue());
				attributes.add(attribute);
			}

			try {
				findByUsername(user.getUsername());
				createRequest(user, attributes);
			} catch (Exception e) {
				// Note: It was really required to perform in a catch block as
				// the validation has been set to be thrown if there's no such
				// username with cognito.
				updateRequest(user, attributes);
				LOGGER.info("updating user request.");

			}

		} catch (ValidationException e) {
			LOGGER.error(e.getMessage());
			throw new ValidationException(e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ApplicationException(e);
		}
	}

	private void createRequest(UserModel model, Collection<AttributeType> attributes) throws ApplicationException {
		try {
			AdminCreateUserRequest request = app.getBean(AdminCreateUserRequest.class);
			request.setUsername(model.getUsername());
			request.setUserAttributes(attributes);
			identityProvider.adminCreateUser(request);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	private void updateRequest(UserModel model, Collection<AttributeType> attributes) throws ApplicationException {
		try {
			AdminUpdateUserAttributesRequest request = app.getBean(AdminUpdateUserAttributesRequest.class);
			request.setUserAttributes(attributes);
			request.setUsername(model.getUsername());
			identityProvider.adminUpdateUserAttributes(request);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	@Override
	public void deleteUser(@NonNull UserModel user)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		try {
			ValidationHandler.throwExceptionIfNull(user.getUsername(), "username not provided",
					ExceptionType.VALIDATION_EXCEPTION);
			AdminDeleteUserRequest request = app.getBean(AdminDeleteUserRequest.class);
			request.setUsername(user.getUsername());
			identityProvider.adminDeleteUser(request);
		} catch (ValidationException e) {
			LOGGER.error(e.getMessage());
			throw new ValidationException(e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ApplicationException(e);
		}
	}

	@Override
	public void deleteUser(String username)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		deleteUser(UserModel.builder().username(username).build());
	}

}
