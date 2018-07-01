package io.college.cms.core.user.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import io.college.cms.core.application.Application.CustomAuthenticationProvider;
import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ResourceDeniedException;
import io.college.cms.core.user.constants.UserGroups;
import lombok.extern.slf4j.Slf4j;

@Service()
@Scope()
@Slf4j
public class SecurityService {

	public static final String ANONYMOUS_USER = "anonymoususer";
	private ApplicationContext app;
	private IUserService userService;

	/**
	 * @param app
	 */
	@Autowired
	public SecurityService(ApplicationContext app) {
		super();
		this.app = app;
		this.userService = app.getBean(UserCognitoService.class);
	}

	public String getPrincipal() {
		return SecurityContextHolder.getContext().getAuthentication() != null
				? SecurityContextHolder.getContext().getAuthentication().getName() : "";
	}

	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public CustomAuthenticationProvider getAuthenticationManager() {
		return app.getBean(CustomAuthenticationProvider.class);
	}

	public UsernamePasswordAuthenticationToken getUsernamePasswordToken(String username, String password) {
		return new UsernamePasswordAuthenticationToken(username, password);
	}

	public Authentication authenticate(String username, String hash) throws AuthenticationException {
		Authentication authenticated = null;
		try {

			String principal = SecurityContextHolder.getContext().getAuthentication().getName();
			if (ANONYMOUS_USER.equalsIgnoreCase(principal) || StringUtils.isEmpty(principal)) {
				authenticated = getAuthenticationManager().authenticate(getUsernamePasswordToken(username, hash));

				// SecurityContextHolder.getContext().setAuthentication(authenticated);
				// SecurityContextHolder.getContext().getAuthentication().setAuthenticated(true);
			} else {
				authenticated = SecurityContextHolder.getContext().getAuthentication();
			}

		} catch (Exception e) {
			throw e;
		}
		return authenticated;
	}

	public boolean isValidUsername(String val) {
		boolean isValid = true;
		try {
			UserCognitoService service = app.getBean(UserCognitoService.class);
			service.findByUsername(val);
		} catch (Exception ex) {
			isValid = false;
			LOGGER.error(ex.getMessage());
		}
		return isValid;
	}

	public boolean isInValidUsername(String val) {
		return !isValidUsername(val);
	}

	public void authorize(UserGroups... roles) throws BadCredentialsException {
		if (!isValidUser(roles)) {
			throw new BadCredentialsException("You do not have required access.");
		}

	}

	public boolean isValidUser(UserGroups... roles) {
		Authentication auth = getAuthentication();
		User user = null;
		if (auth == null || auth.getPrincipal() == null
				|| (ANONYMOUS_USER.equalsIgnoreCase(auth.getName()) || (this.getAuthentication() == null))) {
			return false;
		}

		try {
			user = new User(this.userService.findByUsername(String.valueOf(this.getAuthentication().getPrincipal())));
		} catch (IllegalArgumentException | ApplicationException | ResourceDeniedException e) {
			LOGGER.error(e.getMessage());
		}
		for (UserGroups role : roles) {
			for (GrantedAuthority authority : user.getAuthorities()) {
				if (role.toString().equalsIgnoreCase(authority.getAuthority())) {
					return true;
				}
			}
		}
		return false;
	}
}
