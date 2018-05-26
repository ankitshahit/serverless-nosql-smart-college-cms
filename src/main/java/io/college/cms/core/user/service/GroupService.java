package io.college.cms.core.user.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminRemoveUserFromGroupRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupResult;

import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ExceptionHandler;
import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;
import io.college.cms.core.user.constants.UserGroups;
import io.college.cms.core.user.model.GroupModel;
import lombok.NonNull;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroupService implements IGroupService {
	private ApplicationContext app;
	private AWSCognitoIdentityProvider identity;

	@Autowired
	public GroupService(@NonNull AWSCognitoIdentityProvider identity) {
		this.identity = identity;
	}

	@Autowired
	public void setApp(ApplicationContext app) {
		this.app = app;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.college.cms.core.user.service.IGroupService#addUserToGroup(java.lang.
	 * String, io.college.cms.core.user.constants.UserGroups)
	 */
	@Override
	public void addUserToGroup(@NonNull String username, @NonNull UserGroups group)
			throws IllegalArgumentException, ValidationException, ApplicationException {
		try {

			AdminAddUserToGroupRequest request = app.getBean(AdminAddUserToGroupRequest.class);
			request.setUsername(username);
			request.setGroupName(group.toString());
			// we won't be storing/validating result as it will throw an error
			// in case of failure.
			identity.adminAddUserToGroup(request);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ExceptionHandler.beautifyStackTrace(ex));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.college.cms.core.user.service.IGroupService#removeUserByGroup(java.
	 * lang.String, io.college.cms.core.user.constants.UserGroups)
	 */
	@Override
	public void removeUserByGroup(@NonNull String username, @NonNull UserGroups group)
			throws IllegalArgumentException, ValidationException, ApplicationException {
		try {

			AdminRemoveUserFromGroupRequest request = app.getBean(AdminRemoveUserFromGroupRequest.class);
			request.setUsername(username);
			request.setGroupName(group.toString());
			// we won't be storing/validating result as it will throw an error
			// in case of failure.
			identity.adminRemoveUserFromGroup(request);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ExceptionHandler.beautifyStackTrace(ex));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.college.cms.core.user.service.IGroupService#listGroupMembers(io.
	 * college.cms.core.user.model.GroupModel)
	 */
	@Override
	public GroupModel listGroupMembers(GroupModel group)
			throws IllegalArgumentException, ValidationException, ApplicationException {

		return listGroupMembers(UserGroups.valueOf(StringUtils.upperCase(group.getGroupName())), group.getLimit(),
				group.getNextToken());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.college.cms.core.user.service.IGroupService#listGroupMembers(io.
	 * college.cms.core.user.constants.UserGroups, java.lang.Integer,
	 * java.lang.String)
	 */
	@Override
	public GroupModel listGroupMembers(@NonNull UserGroups group, Integer limit, String token)
			throws IllegalArgumentException, ValidationException, ApplicationException {
		var builder = GroupModel.builder().groupName(group.toString());
		try {

			ListUsersInGroupRequest request = app.getBean(ListUsersInGroupRequest.class);
			if (limit == null || limit <= 0) {
				limit = 10;
			}
			request.setLimit(limit);
			if (StringUtils.isNotEmpty(token)) {
				request.setNextToken(token);
			}
			ListUsersInGroupResult result = identity.listUsersInGroup(request);
			builder.nextToken(result.getNextToken());
			result.getUsers().forEach(user -> {
				builder.withUsername(user.getUsername());
			});
			builder.limit(limit.intValue());
			ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(builder.build().getUsernames()),
					"No users found", ExceptionType.VALIDATION_EXCEPTION);
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			throw new ValidationException(ExceptionHandler.beautifyStackTrace(ex));
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ExceptionHandler.beautifyStackTrace(ex));
		}
		return builder.build();
	}
}
