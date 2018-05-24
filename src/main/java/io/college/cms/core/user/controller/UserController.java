package io.college.cms.core.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.user.model.UserModel;
import io.college.cms.core.user.service.UserResponseService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/1.0/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UserController {
	public static final String USER_NAME = "username";

	private UserResponseService userResService;

	public UserController(UserResponseService userService) {
		this.userResService = userService;
	}

	@RequestMapping(method = { RequestMethod.GET })
	public FactoryResponse findByUsername(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = USER_NAME, required = false) String username) {
		LOGGER.debug("Request received");

		FactoryResponse fr = null;
		if (StringUtils.isNotEmpty(username)) {
			fr = userResService.getUserByUser(request, username);
		} else {
			fr = userResService.getUsers(request);
		}
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	@RequestMapping(method = { RequestMethod.PUT, RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE)
	public FactoryResponse createUpdateUser(HttpServletRequest request, HttpServletResponse response,
			@RequestBody UserModel model) {
		LOGGER.debug("Request received");

		FactoryResponse fr = userResService.createUpdateUser(request, model);

		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	@RequestMapping(method = { RequestMethod.DELETE }, consumes = MediaType.APPLICATION_JSON_VALUE)
	public FactoryResponse deleteUser(HttpServletRequest request, HttpServletResponse response,
			@RequestBody UserModel model) {
		LOGGER.debug("Request received");

		FactoryResponse fr = userResService.deleteUserByUsername(request, model);
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}
}
