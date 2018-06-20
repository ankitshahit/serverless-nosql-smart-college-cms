package io.college.cms.core.user.service;

import java.util.List;

import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ResourceDeniedException;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.user.model.UserModel;

public interface IUserService {
	/**
	 * 
	 * @param username
	 * @return
	 * @throws IllegalArgumentException
	 * @throws ValidationException
	 * @throws ApplicationException
	 * @throws ResourceDeniedException
	 */
	UserModel findByUsername(String username)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException;

	/**
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 * @throws ValidationException
	 * @throws ApplicationException
	 * @throws ResourceDeniedException
	 */
	default List<UserModel> findAllUsers()
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		return null;
	};

	default List<UserModel> findAllUsers(UserModel userModel)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		return null;
	};

	default void createUpdateUser(UserModel user, boolean isAdmin)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
	};

	default void confirmSignup(String username, String confirmation)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
		throw new ApplicationException("Service doesn't implement confirmSignup()");
	}

	/**
	 * 
	 * @param user
	 * @throws IllegalArgumentException
	 * @throws ValidationException
	 * @throws ApplicationException
	 * @throws ResourceDeniedException
	 */
	default void createUpdateUser(UserModel user)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException {
	};

	/**
	 * 
	 * @param user
	 * @throws IllegalArgumentException
	 * @throws ValidationException
	 * @throws ApplicationException
	 * @throws ResourceDeniedException
	 */
	void deleteUser(UserModel user)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException;

	/**
	 * 
	 * @param username
	 * @throws IllegalArgumentException
	 * @throws ValidationException
	 * @throws ApplicationException
	 * @throws ResourceDeniedException
	 */
	void deleteUser(String username)
			throws IllegalArgumentException, ValidationException, ApplicationException, ResourceDeniedException;
}
