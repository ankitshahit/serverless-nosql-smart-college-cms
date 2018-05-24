package io.college.cms.core.user.service;

import java.util.List;

import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ResourceDeniedException;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.user.model.UserModel;

public interface IUserService {
	UserModel findByUsername(String username)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException;

	List<UserModel> findAllUsers()
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException;

	void createUpdateUser(UserModel user)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException;

	void deleteUser(UserModel user)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException;

	void deleteUser(String username)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException;
}
