package io.college.cms.core.user.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;

import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ResourceDeniedException;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.user.constants.UserAttributes;
import io.college.cms.core.user.model.UserModel;
import lombok.NonNull;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
			LOGGER.error(e.getMessage());
			throw new ApplicationException(e);
		}
		return userBuilder.build();
	}

	@Override
	public List<UserModel> findAllUsers()
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {

		return null;
	}

	@Override
	public void createUpdateUser(UserModel user)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {

	}

	@Override
	public void deleteUser(UserModel user)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {

	}

	@Override
	public void deleteUser(String username)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {

	}

}
