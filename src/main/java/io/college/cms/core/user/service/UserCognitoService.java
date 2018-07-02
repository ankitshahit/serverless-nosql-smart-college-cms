package io.college.cms.core.user.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.GroupType;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.UserType;

import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.ResourceDeniedException;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;
import io.college.cms.core.user.constants.UserAttributes;
import io.college.cms.core.user.constants.UserGroups;
import io.college.cms.core.user.model.UserModel;
import lombok.NonNull;
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
		UserModel.UserModelBuilder userBuilder = UserModel.builder();
		// userBuilder.createdOn(LocalDate.from(user.getUserCreateDate().toInstant()));
		userBuilder.username(user.getUsername()).isActive(user.getEnabled());
		userBuilder.attributes(valueOf(user.getAttributes()).getAttributes());
		return copyAttributes(userBuilder.build());
	}

	public static UserModel copyAttributes(UserModel user) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		for (io.college.cms.core.user.model.UserModel.AttributeType attribute : user.getAttributes()) {
			if (attribute == null) {
				continue;
			}
			if (UserAttributes.FAMILY_NAME == attribute.getName()) {
				user.setLastName(attribute.getValue());
			} else if (UserAttributes.GENDER == attribute.getName()) {
				user.setGender(attribute.getValue());
			} else if (UserAttributes.EMAIL == attribute.getName()) {
				user.setEmail(attribute.getValue());
			} else if (UserAttributes.PHONE_NUMBER == attribute.getName()) {
				user.setPhone(attribute.getValue());
			} else if (UserAttributes.GIVEN_NAME == attribute.getName()) {
				user.setFirstName(attribute.getValue());
			} else if (UserAttributes.NAME == attribute.getName()) {
				user.setName(attribute.getValue());
			} else if (UserAttributes.BIRTH_DATE == attribute.getName()) {
				user.setDateOfBirth(LocalDate.parse(attribute.getValue(), formatter));
			} else if (UserAttributes.PROFILE == attribute.getName()) {
				user.setProfileLink(attribute.getValue());
			} else if (UserAttributes.MIDDLE_NAME == attribute.getName()) {
				user.setMiddleName(attribute.getValue());
			}
		}

		return user;
	}

	/**
	 * Maps out cognito attributes to UserModel.AttributeType instance
	 * 
	 * @param attributes
	 * @return
	 */
	public static UserModel valueOf(List<AttributeType> attributes) {
		UserModel.UserModelBuilder userBuilder = UserModel.builder();
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

	private String getGroupName(String user) {
		AdminListGroupsForUserRequest adminListRequest = app.getBean(AdminListGroupsForUserRequest.class);
		adminListRequest.setUsername(user);

		List<GroupType> groups = identityProvider.adminListGroupsForUser(adminListRequest).getGroups();
		if (CollectionUtils.isEmpty(groups)) {
			// TODO: REMOVE THIS IMPLEMENTATION AND ADD USER TO STUDENT GROUP BY
			// DEFAULT.
			return "STUDENT";
		}
		for (GroupType groupType : groups) {
			return groupType.getGroupName();
		}
		return "";
	}

	@Override
	// @Cacheable
	public UserModel findByUsername(@NonNull String username)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		UserModel model = null;
		try {
			AdminGetUserRequest request = app.getBean(AdminGetUserRequest.class);
			request.setUsername(username);
			AdminGetUserResult result = identityProvider.adminGetUser(request);
			model = copyAttributes(valueOf(result.getUserAttributes()));
			model.setUsername(username);
			model.setActive(result.getEnabled());
			model.setUserStatus(result.getUserStatus());
			model.setGroup(UserGroups.valueOf(getGroupName(username)));

		} catch (com.amazonaws.services.cognitoidp.model.UserNotFoundException ex) {
			LOGGER.error("User not found{}", username);
			throw new ValidationException(ex.getMessage());
		} catch (Exception e) {
			LOGGER.error("{} " + e.getMessage(), username);
			throw new ApplicationException(e);
		}
		return model;
	}

	@Override
	public void confirmSignup(String username, String confirmation)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		try {
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(username), "No such user available",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(confirmation), "No confirmation value provided.",
					ExceptionType.VALIDATION_EXCEPTION);
			UserModel user = findByUsername(username);
			// if there's no such user, an exception will be raised by
			// findByUsername() to let us know resource doesn't exist.
			if (!UserStatus.UNCONFIRMED.toString().equalsIgnoreCase(user.getUserStatus())) {
				throw new ValidationException("User is already verified.");
			}
			ConfirmSignUpRequest request = app.getBean(ConfirmSignUpRequest.class);
			request.setUsername(username);
			request.setConfirmationCode(confirmation);
			identityProvider.confirmSignUp(request);
			LOGGER.debug("Confirmed user {} on {}", username, LocalDate.now());
		} catch (ValidationException e) {
			LOGGER.error("validation code provided : {}", confirmation);
			LOGGER.error("user {} couldn't be verified, validation exception", username);
			LOGGER.error(e.getMessage());
			throw new ValidationException(e.getLocalizedMessage());
		} catch (com.amazonaws.services.cognitoidp.model.UserNotFoundException ex) {
			LOGGER.error(ex.getMessage());
			throw new ValidationException("No such user exists");
		} catch (Exception e) {
			LOGGER.error("user {} couldn't be verified, other exception", username);
			LOGGER.error(e.getMessage());
			throw new ApplicationException("Not a valid confirmation key, unable to verify!");
		}
	}

	@Override
	public List<UserModel> findAllUsers(UserModel model)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		List<UserModel> users = new ArrayList<UserModel>();
		try {
			ListUsersRequest request = app.getBean(ListUsersRequest.class);

			int limit = 60;
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
				UserModel data = valueOf(user);
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
	public void createUpdateUser(@NonNull UserModel user, boolean isAdmin)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		try {
			ValidationHandler.throwExceptionIfNull(user.getUsername(), "username not provided",
					ExceptionType.VALIDATION_EXCEPTION);

			UserModel.UserModelBuilder builder = UserModel.builder();

			builder.withAttribute(io.college.cms.core.user.model.UserModel.AttributeType.builder()
					.name(UserAttributes.EMAIL).value(user.getEmail()).build())
					.withAttribute(io.college.cms.core.user.model.UserModel.AttributeType.builder()
							.name(UserAttributes.MIDDLE_NAME).value(user.getMiddleName()).build())
					.withAttribute(io.college.cms.core.user.model.UserModel.AttributeType.builder()
							.name(UserAttributes.FAMILY_NAME).value(user.getLastName()).build())
					.withAttribute(io.college.cms.core.user.model.UserModel.AttributeType.builder()
							.name(UserAttributes.GIVEN_NAME).value(user.getFirstName()).build())
					.withAttribute(io.college.cms.core.user.model.UserModel.AttributeType.builder()
							.name(UserAttributes.BIRTH_DATE).value(String.valueOf(user.getDateOfBirth())).build())
					.withAttribute(io.college.cms.core.user.model.UserModel.AttributeType.builder()
							.name(UserAttributes.PHONE_NUMBER).value(user.getPhone()).build())
					.withAttribute(io.college.cms.core.user.model.UserModel.AttributeType.builder()
							.name(UserAttributes.GENDER).value(user.getGender()).build())
					.withAttribute(
							io.college.cms.core.user.model.UserModel.AttributeType.builder().name(UserAttributes.NAME)
									.value(new StringBuilder().append(user.getFirstName()).append(" ")
											.append(user.getLastName()).toString())
									.build())
					.withAttribute(io.college.cms.core.user.model.UserModel.AttributeType.builder()
							.name(UserAttributes.PROFILE).value(user.getProfileLink()).build())
					.username(user.getUsername()).token(user.getToken());

			List<AttributeType> attributes = new ArrayList<AttributeType>();
			for (io.college.cms.core.user.model.UserModel.AttributeType attr : builder.build().getAttributes()) {
				AttributeType attribute = new AttributeType();
				attribute.setName(StringUtils.lowerCase(attr.getName().val().toString()));
				attribute.setValue(attr.getValue());
				attributes.add(attribute);
			}

			try {
				// TODO: we need to fetch the existing details from cognito and
				// update only those that are really required to.
				findByUsername(builder.build().getUsername());// Note:
				// It
				// was
				// really

				// required to perform in a

				// catch block as
				// the validation has been set to be thrown if there's no such
				// username with cognito.
				if (isAdmin) {
					updateRequestAdmin(user, attributes);
					LOGGER.info("updating user request.");
				} else {
					//
				}

			} catch (Exception e) {
				if (isAdmin) {
					createRequestAdmin(builder.build(), attributes);
				} else {
					ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(user.getToken()), "No password provided",
							ExceptionType.VALIDATION_EXCEPTION);
					createRequest(user, attributes);
				}
			}

		} catch (ValidationException e) {
			LOGGER.error(e.getMessage());
			throw new ValidationException(e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ApplicationException(e);
		}
	}

	private void createRequest(UserModel model, Collection<AttributeType> attributes)
			throws ApplicationException, ValidationException {
		try {
			List<AttributeType> newAttributes = new ArrayList<>();
			Iterator<AttributeType> iterator = attributes.iterator();
			while (iterator.hasNext()) {
				AttributeType attr = iterator.next();
				newAttributes.add(attr);
			}
			SignUpRequest request = app.getBean(SignUpRequest.class);
			request.setUsername(model.getUsername());
			request.setUserAttributes(newAttributes);
			request.setPassword(model.getToken());
			identityProvider.signUp(request);
			app.getBean(GroupService.class).addUserToGroup(model.getUsername(), UserGroups.STUDENT);
			
		} catch (com.amazonaws.services.cognitoidp.model.InvalidParameterException ex) {
			LOGGER.error(ex.getMessage());
			throw new ValidationException(ex.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ApplicationException(e);
		}
	}

	private void createRequestAdmin(UserModel model, Collection<AttributeType> attributes)
			throws ApplicationException, ValidationException {
		try {
			SignUpRequest request = app.getBean(SignUpRequest.class);
			request.setUsername(model.getUsername());
			request.setPassword(model.getToken());
			request.setUserAttributes(attributes);
			identityProvider.signUp(request);
			GroupService groupService = app.getBean(GroupService.class);
			groupService.addUserToGroup(model.getUsername(), UserGroups.STAFF);
		} catch (com.amazonaws.services.cognitoidp.model.InvalidParameterException ex) {
			LOGGER.error(ex.getMessage());
			throw new ValidationException(ex.getMessage());
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	private void updateRequestAdmin(UserModel model, Collection<AttributeType> attributes) throws ApplicationException {
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
		deleteUser(UserModel.builder().email(username).build());
	}

	public enum UserStatus {
		UNCONFIRMED, CONFIRMED, NEW_PASSWORD_REQUIRED;
	}

}
