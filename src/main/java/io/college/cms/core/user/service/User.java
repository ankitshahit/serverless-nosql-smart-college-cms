package io.college.cms.core.user.service;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.college.cms.core.user.model.UserModel;

public class User implements UserDetails {
	public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
	private static final long serialVersionUID = 1L;
	private UserModel user;

	/**
	 * @param user
	 */
	public User(UserModel user) {
		super();
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList((GrantedAuthority) () -> {
			return String.valueOf(user.getGroup());
		});
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.isActive();
	}

}
