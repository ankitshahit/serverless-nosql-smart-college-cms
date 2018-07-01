package io.college.cms.core.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ResourceDeniedException;

@Service
public class UserDetailService implements UserDetailsService {
	private IUserService userService;

	/**
	 * @param userService
	 */
	@Autowired
	public UserDetailService(IUserService userService) {
		super();
		this.userService = userService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = null;
		try {
			user = new User(userService.findByUsername(username));
		} catch (IllegalArgumentException | ApplicationException | ResourceDeniedException e) {
			throw new UsernameNotFoundException(e.getMessage());
		}
		return user;
	}

}
