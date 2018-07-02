package io.college.cms.core.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminRemoveUserFromGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;

@Configuration
public class CognitoConfiguration {
	private AppParams params;

	@Autowired
	public CognitoConfiguration(AppParams params) {
		this.params = params;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public AdminGetUserRequest adminGetUser() {
		AdminGetUserRequest adminGetUser = new AdminGetUserRequest();
		adminGetUser.setUserPoolId(params.getCognitoUserPoolId());
		return adminGetUser;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public AdminCreateUserRequest adminCreateUser() {
		AdminCreateUserRequest createUserRequest = new AdminCreateUserRequest();
		createUserRequest.setUserPoolId(params.getCognitoUserPoolId());
		return createUserRequest;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public AdminDeleteUserRequest adminDeleteUser() {
		AdminDeleteUserRequest deleteUserRequest = new AdminDeleteUserRequest();
		deleteUserRequest.setUserPoolId(params.getCognitoUserPoolId());
		return deleteUserRequest;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public AdminUpdateUserAttributesRequest adminUpdateAttributeUser() {
		AdminUpdateUserAttributesRequest request = new AdminUpdateUserAttributesRequest();
		request.setUserPoolId(params.getCognitoUserPoolId());
		return request;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public AdminAddUserToGroupRequest adminAddGroup() {
		AdminAddUserToGroupRequest userRequest = new AdminAddUserToGroupRequest();
		userRequest.setUserPoolId(params.getCognitoUserPoolId());
		return userRequest;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public AdminRemoveUserFromGroupRequest adminRemoveGroup() {
		AdminRemoveUserFromGroupRequest userRequest = new AdminRemoveUserFromGroupRequest();
		userRequest.setUserPoolId(params.getCognitoUserPoolId());
		return userRequest;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public AdminListGroupsForUserRequest adminListUserGroup() {
		AdminListGroupsForUserRequest userRequest = new AdminListGroupsForUserRequest();
		userRequest.setUserPoolId(params.getCognitoUserPoolId());
		return userRequest;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public ListUsersInGroupRequest adminListGroup() {
		ListUsersInGroupRequest userRequest = new ListUsersInGroupRequest();
		userRequest.setUserPoolId(params.getCognitoUserPoolId());
		return userRequest;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public ListUsersRequest listUsersGroup() {
		ListUsersRequest userRequest = new ListUsersRequest();
		userRequest.setUserPoolId(params.getCognitoUserPoolId());
		return userRequest;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public SignUpRequest signupRequest() {
		SignUpRequest request = new SignUpRequest();
		request.setClientId(params.getCognitoClientId());
		return request;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public ConfirmSignUpRequest confirmSignupRequest() {
		ConfirmSignUpRequest request = new ConfirmSignUpRequest();
		request.setClientId(params.getCognitoClientId());
		return request;
	}

	// ForgotPasswordRequest
	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public InitiateAuthRequest authRequest() {
		InitiateAuthRequest authRequest = new InitiateAuthRequest();
		authRequest.setClientId(params.getCognitoClientId());
		authRequest.setAuthFlow(AuthFlowType.USER_PASSWORD_AUTH);
		return authRequest;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public ForgotPasswordRequest forgotPasswordRequest() {
		ForgotPasswordRequest authRequest = new ForgotPasswordRequest();
		authRequest.setClientId(params.getCognitoClientId());
		return authRequest;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public ConfirmForgotPasswordRequest confirmPasswordRequest() {
		ConfirmForgotPasswordRequest authRequest = new ConfirmForgotPasswordRequest();
		authRequest.setClientId(params.getCognitoClientId());
		return authRequest;
	}

}
