package io.college.cms.core.user.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ExceptionHandler;
import io.college.cms.core.exception.ResourceDeniedException;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.user.constants.UserAttributes;
import io.college.cms.core.user.model.UserModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserResponseService {

	private IUserService userService;

	@Autowired()
	public UserResponseService(IUserService userService) {
		this.userService = userService;
	}

	/**
	 * Returns all of user attributes made available through cms, caching is
	 * used because we do not expect for them to change
	 * 
	 * @return
	 */
	@Cacheable
	public FactoryResponse getAllUserAttributes() {
		List<String> constants = new ArrayList<String>();
		Arrays.asList(UserAttributes.values()).forEach(attribute -> {
			constants.add(attribute.toString());
		});

		return FactoryResponse.builder().response(constants).summaryMessage(SummaryMessageEnum.SUCCESS).build();
	}

	public FactoryResponse getUserByUser(HttpServletRequest request, String username) {
		FactoryResponse fr = null;
		try {
			UserModel user = userService.findByUsername(username);
			fr = FactoryResponse.builder().response(user).summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (IllegalArgumentException ex) {
			LOGGER.error("One of required fields is empty.");
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ResourceDeniedException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.ACCESS_DENIED).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Application dont feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse getUsers(HttpServletRequest request, UserModel userModel) {
		FactoryResponse fr = null;
		try {
			fr = FactoryResponse.builder().response(userService.findAllUsers(userModel))
					.summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (IllegalArgumentException ex) {
			LOGGER.error("One of required fields is empty.");
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ResourceDeniedException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.ACCESS_DENIED).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Application dont feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse createUpdateUser(HttpServletRequest request, UserModel user) {
		FactoryResponse fr = null;
		try {
			userService.createUpdateUser(user);
			fr = FactoryResponse.builder().response("Successfully created/updated.")
					.summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (IllegalArgumentException ex) {
			LOGGER.error("One of required fields is empty.");
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ResourceDeniedException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.ACCESS_DENIED).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Application dont feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	public FactoryResponse deleteUserByUsername(HttpServletRequest request, UserModel username) {
		FactoryResponse fr = null;
		try {

			userService.deleteUser(username);
			fr = FactoryResponse.builder().response("deleted successfully.").summaryMessage(SummaryMessageEnum.SUCCESS)
					.build();
		} catch (IllegalArgumentException ex) {
			LOGGER.error("One of required fields is empty.");
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ResourceDeniedException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.ACCESS_DENIED).build();
		} catch (ApplicationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Application dont feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

}