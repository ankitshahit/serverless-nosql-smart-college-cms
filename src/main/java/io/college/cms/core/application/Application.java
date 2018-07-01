package io.college.cms.core.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.PasswordResetRequiredException;
import com.amazonaws.services.cognitoidp.model.UserNotConfirmedException;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

import io.college.cms.core.ui.controller.HomePageUI;
import io.college.cms.core.ui.model.ViewConstants;
import io.college.cms.core.user.service.UserDetailService;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ComponentScan(basePackages = { "io.college.cms.core.*", "io.college.cms.core.configuration.*",
		"io.college.cms.core.examination.*", "io.college.cms.core.courses.*", "io.college.cms.core.application.*",
		"io.college.cms.core.application.automation.*", "io.college.cms.core.ui.*",
		"io.college.cms.core.dynamodbloader.*", "io.college.cms.core.dynamodbloader.service.*",
		"io.college.cms.core.faq.*", "io.college.cms.core.admission.*", "io.college.cms.core.ui.services.*",
		"io.college.cms.core.upload.*", "io.college.cms.core.job.*", "io.college.cms.core.attendance.*" })
@Slf4j
@EnableCaching
@EnableWebSecurity(debug = false)
@EnableAsync
public class Application {
	public static void main(String[] args) {
		LOGGER.debug("isDebugEnabled = {}", LOGGER.isDebugEnabled());
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
		SpringApplication.run(Application.class, args);
	}

	@WebServlet(urlPatterns = "/*", name = "frontEndApp", asyncSupported = true)
	@VaadinServletConfiguration(ui = HomePageUI.class, productionMode = false)
	public static class VaadinApplication extends VaadinServlet {
		private static final long serialVersionUID = 1L;
	}

	@Configuration
	public class SpringConfiguration extends WebSecurityConfigurerAdapter {

		@Bean
		public CacheManager cacheManager() {
			return new ConcurrentMapCacheManager();
		}

		@Bean
		public CustomAuthenticationProvider createDaoAuthenticationProvider() {
			return new CustomAuthenticationProvider();
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.authenticationProvider(createDaoAuthenticationProvider());
			super.configure(auth);
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable().exceptionHandling()
					.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(
							new StringBuilder().append("/homepage/").append(ViewConstants.LOGIN).toString()))
					.and().authorizeRequests()
					.antMatchers("/homepage/VAADIN/**", "/homepage/PUSH/**", "/homepage/UIDL/**", "/homepage/login/",
							"/homepage/login/**", "/homepage/error/**", "/homepage/accessDenied/**",
							"/homepage/vaadinServlet/**", "/homepage/VAADIN/widgetsets/**", "/homepage/**",
							"/VAADIN/**", "/vaadinServlet/**", "/HEARTBEAT/**")

					.permitAll().anyRequest().authenticated().and().formLogin()
					.loginProcessingUrl(new StringBuilder().append("/login/")
							.toString()/*
										 * .append(ViewConstants.LOGIN).
										 * toString( )
										 */)
					.loginPage(new StringBuilder().append("/login")
							/* .append(ViewConstants.LOGIN) */.toString())
					.permitAll();
			;
		}
	}

	public static class CustomAuthenticationProvider implements AuthenticationProvider {
		@Autowired
		private UserDetailService userSecurityService;
		@Autowired
		private InitiateAuthRequest authRequest;

		@Autowired(required = true)
		private AWSCognitoIdentityProvider provider;

		@Override
		public Authentication authenticate(Authentication authentication) throws AuthenticationException {
			LOGGER.debug("authentication: {}", authentication);
			String username = authentication.getName();
			String password = String.valueOf(authentication.getCredentials());

			UserDetails user = userSecurityService.loadUserByUsername(username);
			if (!user.isEnabled()) {
				throw new DisabledException("Account is disabled");
			}
			Map<String, String> authParameters = new HashMap<>();
			authParameters.put("USERNAME", username);
			// TODO: do we wanna handle with bcrypt first at login page then
			// implement in overall filter check?
			authParameters.put("PASSWORD", password);
			authRequest.setAuthParameters(authParameters);

			try {
				provider.initiateAuth(authRequest);
			} catch (PasswordResetRequiredException ex) {
				throw new BadCredentialsException("Password reset is required");
			} catch (UserNotConfirmedException ex) {
				throw new BadCredentialsException("User account not confirmed");
			} catch (AWSCognitoIdentityProviderException ex) {
				throw new BadCredentialsException("Bad credentials");
			}
			Authentication response = new UsernamePasswordAuthenticationToken(username, password,
					user.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(response);
			return response;
		}

		@Override
		public boolean supports(Class<?> authentication) {
			return authentication.equals(UsernamePasswordAuthenticationToken.class);
		}

	}
}
