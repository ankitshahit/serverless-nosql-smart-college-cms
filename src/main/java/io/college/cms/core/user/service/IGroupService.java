package io.college.cms.core.user.service;

import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.user.constants.UserGroups;
import io.college.cms.core.user.model.GroupModel;

public interface IGroupService {
	/**
	 * 
	 * @param username
	 * @param group
	 * @throws IllegalArgumentException
	 * @throws ValidationException
	 * @throws ApplicationException
	 */
	void addUserToGroup(String username, UserGroups group)
			throws IllegalArgumentException, ValidationException, ApplicationException;

	/**
	 * 
	 * @param username
	 * @param group
	 * @throws IllegalArgumentException
	 * @throws ValidationException
	 * @throws ApplicationException
	 */
	void removeUserByGroup(String username, UserGroups group)
			throws IllegalArgumentException, ValidationException, ApplicationException;

	/**
	 * 
	 * @param group
	 * @return
	 * @throws IllegalArgumentException
	 * @throws ValidationException
	 * @throws ApplicationException
	 */
	GroupModel listGroupMembers(GroupModel group)
			throws IllegalArgumentException, ValidationException, ApplicationException;

	/**
	 * 
	 * @param group
	 * @param limit
	 * @param token
	 * @return
	 * @throws IllegalArgumentException
	 * @throws ValidationException
	 * @throws ApplicationException
	 */
	GroupModel listGroupMembers(UserGroups group, Integer limit, String token)
			throws IllegalArgumentException, ValidationException, ApplicationException;

}