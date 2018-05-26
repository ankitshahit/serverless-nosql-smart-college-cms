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
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupRequest;

@Configuration
public class CognitoConfiguration {
	private AppParams appParams;

	@Autowired
	public CognitoConfiguration(AppParams appParams) {
		this.appParams = appParams;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public AdminGetUserRequest adminGetUser() {
		AdminGetUserRequest adminGetUser = new AdminGetUserRequest();
		adminGetUser.setUserPoolId(appParams.getCognitoUserPoolId());
		return adminGetUser;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public AdminCreateUserRequest adminCreateUser() {
		AdminCreateUserRequest createUserRequest = new AdminCreateUserRequest();
		createUserRequest.setUserPoolId(appParams.getCognitoUserPoolId());
		return createUserRequest;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public AdminDeleteUserRequest adminDeleteUser() {
		AdminDeleteUserRequest deleteUserRequest = new AdminDeleteUserRequest();
		deleteUserRequest.setUserPoolId(appParams.getCognitoUserPoolId());
		return deleteUserRequest;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public AdminUpdateUserAttributesRequest adminUpdateAttributeUser() {
		AdminUpdateUserAttributesRequest request = new AdminUpdateUserAttributesRequest();
		request.setUserPoolId(appParams.getCognitoUserPoolId());
		return request;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public AdminAddUserToGroupRequest adminAddGroup() {
		AdminAddUserToGroupRequest userRequest = new AdminAddUserToGroupRequest();
		userRequest.setUserPoolId(appParams.getCognitoUserPoolId());
		return userRequest;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public AdminRemoveUserFromGroupRequest adminRemoveGroup() {
		AdminRemoveUserFromGroupRequest userRequest = new AdminRemoveUserFromGroupRequest();
		userRequest.setUserPoolId(appParams.getCognitoUserPoolId());
		return userRequest;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public AdminListGroupsForUserRequest adminListUserGroup() {
		AdminListGroupsForUserRequest userRequest = new AdminListGroupsForUserRequest();
		userRequest.setUserPoolId(appParams.getCognitoUserPoolId());
		return userRequest;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public ListUsersInGroupRequest adminListGroup() {
		ListUsersInGroupRequest userRequest = new ListUsersInGroupRequest();
		userRequest.setUserPoolId(appParams.getCognitoUserPoolId());
		return userRequest;
	}
}
