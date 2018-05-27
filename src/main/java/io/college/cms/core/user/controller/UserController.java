package io.college.cms.core.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.user.model.GroupModel;
import io.college.cms.core.user.model.UserModel;
import io.college.cms.core.user.service.GroupResponseService;
import io.college.cms.core.user.service.UserResponseService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/1.0/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UserController {
	public static final String USER_NAME = "username";
	public static final String GROUP_NAME = "group_name";
	public static final String NEXT_TOKEN = "next_token";
	public static final String LIST_TYPE = "list_type";
	public static final String USERS = "users";
	public static final String ATTRIBUTES = "attributes";
	public static final String LIMIT = "limit";
	private UserResponseService userService;
	private GroupResponseService groupService;

	public UserController(UserResponseService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setGroupService(GroupResponseService groupService) {
		this.groupService = groupService;
	}

	@RequestMapping(path = "attributes", method = { RequestMethod.GET })
	public FactoryResponse getAttributes(HttpServletRequest request, HttpServletResponse response) {
		LOGGER.debug("Request received");
		FactoryResponse fr = userService.getAllUserAttributes();
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	@RequestMapping(method = { RequestMethod.GET })
	public FactoryResponse findByUsername(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = USER_NAME, required = false) String username,
			@RequestParam(value = LIMIT, required = false) Integer limit,
			@RequestParam(value = NEXT_TOKEN, required = false) String token) {
		LOGGER.debug(new StringBuilder().append("Request").append(" {0}").toString(), username);

		FactoryResponse fr = null;
		if (StringUtils.isNotEmpty(username)) {
			fr = userService.getUserByUser(request, username);
		} else {
			fr = userService.getUsers(request, UserModel.builder().limit(limit).paginationToken(token).build());
		}
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	@RequestMapping(method = { RequestMethod.PUT, RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE)
	public FactoryResponse createUpdateUser(HttpServletRequest request, HttpServletResponse response,
			@RequestBody UserModel model) {
		LOGGER.debug("Request received");

		FactoryResponse fr = userService.createUpdateUser(request, model);

		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	@RequestMapping(method = { RequestMethod.DELETE }, consumes = MediaType.APPLICATION_JSON_VALUE)
	public FactoryResponse deleteUser(HttpServletRequest request, HttpServletResponse response,
			@RequestBody UserModel model) {
		LOGGER.debug("Request received");

		FactoryResponse fr = userService.deleteUserByUsername(request, model);
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	@RequestMapping(path = "groups", method = { RequestMethod.GET })
	public FactoryResponse getGroups(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = GROUP_NAME, required = false) String groupName,
			@RequestParam(value = LIMIT, required = false) Integer limit,
			@RequestParam(value = NEXT_TOKEN, required = false) String token,
			@RequestParam(value = LIST_TYPE, required = false) String listType) {
		LOGGER.debug("Request received");
		FactoryResponse fr = null;
		if (StringUtils.isNotEmpty(listType) && USERS.equalsIgnoreCase(listType)) {
			fr = groupService.listGroup(request,
					GroupModel.builder().nextToken(token).groupName(groupName).limit(limit).build());
		} else {
			fr = groupService.getAllGroupNames();
		}
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	@RequestMapping(path = "groups", method = { RequestMethod.PUT }, consumes = MediaType.APPLICATION_JSON_VALUE)
	public FactoryResponse addToGroup(HttpServletRequest request, HttpServletResponse response,
			@RequestBody GroupModel group) {
		LOGGER.debug("Request received");

		FactoryResponse fr = groupService.addUserToGroup(request, group);
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

	@RequestMapping(path = "groups", method = { RequestMethod.DELETE }, consumes = MediaType.APPLICATION_JSON_VALUE)
	public FactoryResponse removeFromGroup(HttpServletRequest request, HttpServletResponse response,
			@RequestBody GroupModel group) {
		LOGGER.debug("Request received");
		FactoryResponse fr = groupService.removeUserFromGroup(request, group);
		response.setStatus(fr.getSummaryMessage().code().value());
		return fr;
	}

}
