package io.college.cms.core.user.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ExceptionHandler;
import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;
import io.college.cms.core.user.constants.UserGroups;
import io.college.cms.core.user.model.GroupModel;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Ankit
 *
 */
@Service
@Slf4j
public class GroupResponseService {
	private IGroupService groupService;

	/**
	 * 
	 * @param group
	 */
	public GroupResponseService(IGroupService group) {
		this.groupService = group;
	}

	/**
	 * 
	 * @param request
	 * @param group
	 * @return
	 */
	public FactoryResponse addUserToGroup(HttpServletRequest request, GroupModel group) {
		FactoryResponse fr = null;
		try {
			ValidationHandler.throwExceptionIfNull(group, "No request json", ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(group.getGroupName()), "No group provided ",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(group.getUsernames()),
					"No usernames provided ", ExceptionType.VALIDATION_EXCEPTION);
			for (String string : group.getUsernames()) {
				try {

					groupService.addUserToGroup(string,
							UserGroups.valueOf(StringUtils.upperCase(group.getGroupName())));
				} catch (Exception e) {
					LOGGER.error("partially processed." + e.getMessage());
					fr = FactoryResponse.builder()
							.response("partially processed requests, couldn't complete your request.")
							.summaryMessage(SummaryMessageEnum.FAILURE).build();
					return fr;
				}
			}

			fr = FactoryResponse.builder().response("added successfully.").summaryMessage(SummaryMessageEnum.SUCCESS)
					.build();
		} catch (IllegalArgumentException ex) {
			LOGGER.error("One of required fields is empty.");
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Application dont feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	/**
	 * 
	 * @param request
	 * @param group
	 * @return
	 */

	public FactoryResponse removeUserFromGroup(HttpServletRequest request, GroupModel group) {
		FactoryResponse fr = null;
		try {

			ValidationHandler.throwExceptionIfNull(group, "No request json", ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(group.getGroupName()), "No groupname provided ",
					ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(CollectionUtils.isEmpty(group.getUsernames()),
					"No usernames provided ", ExceptionType.VALIDATION_EXCEPTION);
			for (String string : group.getUsernames()) {
				try {

					groupService.removeUserByGroup(string,
							UserGroups.valueOf(StringUtils.upperCase(group.getGroupName())));
				} catch (Exception e) {
					LOGGER.error("partially processed." + e.getMessage());
					fr = FactoryResponse.builder()
							.response("partially processed requests, couldn't complete your request.")
							.summaryMessage(SummaryMessageEnum.FAILURE).build();
					return fr;
				}
			}

			fr = FactoryResponse.builder().response("removed successfully.").summaryMessage(SummaryMessageEnum.SUCCESS)
					.build();
		} catch (IllegalArgumentException ex) {
			LOGGER.error("One of required fields is empty.");
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response("Application dont feel so good!")
					.summaryMessage(SummaryMessageEnum.FAILURE).build();
		}
		return fr;
	}

	/**
	 * 
	 * @param request
	 * @param group
	 * @return
	 */
	public FactoryResponse listGroup(HttpServletRequest request, GroupModel group) {
		FactoryResponse fr = null;
		try {
			ValidationHandler.throwExceptionIfNull(group, "No request json", ExceptionType.VALIDATION_EXCEPTION);
			ValidationHandler.throwExceptionIfTrue(StringUtils.isEmpty(group.getGroupName()), "No group name provided ",
					ExceptionType.VALIDATION_EXCEPTION);

			GroupModel users = groupService.listGroupMembers(group);
			ValidationHandler.throwExceptionIfNull(users, "No users are found", ExceptionType.VALIDATION_EXCEPTION);
			fr = FactoryResponse.builder().response(users).summaryMessage(SummaryMessageEnum.SUCCESS).build();
		} catch (IllegalArgumentException ex) {
			LOGGER.error("One of required fields is empty.");
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
		} catch (ValidationException ex) {
			LOGGER.error(ex.getMessage());
			fr = FactoryResponse.builder().response(ExceptionHandler.beautifyStackTrace(ex))
					.summaryMessage(SummaryMessageEnum.VALIDATION_ERROR).build();
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

	/**
	 * 
	 * @return
	 */
	@Cacheable
	public FactoryResponse getAllGroupNames() {
		List<String> constants = new ArrayList<String>();
		Arrays.asList(UserGroups.values()).forEach(attribute -> {
			constants.add(attribute.toString());
		});

		return FactoryResponse.builder().response(constants).summaryMessage(SummaryMessageEnum.SUCCESS).build();
	}
}
