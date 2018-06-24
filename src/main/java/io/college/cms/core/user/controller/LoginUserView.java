package io.college.cms.core.user.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.ui.listener.EmptyFieldListener;
import lombok.Builder;
import lombok.Data;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LoginUserView extends VerticalLayout implements View {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LoginUserViewService loginViewService;

	public LoginUserView() {
		super();
		this.loginViewService = new LoginUserViewService();
	}

	@PostConstruct
	public void paint() {
		addComponent(loginViewService.getDto().getRootPanel());
	}	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

	@Data
	private static class LoginUserViewService {
		private UserViewDTO dto;

		public LoginUserViewService() {
			super();
			this.dto = UserViewDTO.builder().build();
			ui();

		}

		protected void ui() {
			initUI();
			initUI();
			initStyle();
			initSizeAndAlignment();
			initComponents();
			initListener();
		}

		/**
		 * Abstracting setting caption, placeholders and icons
		 */
		protected void initUI() {

			this.dto.usernameField.setPlaceholder("username or email id");
			this.dto.usernameField.setRequiredIndicatorVisible(true);
			this.dto.usernameField.setVisible(true);
			this.dto.usernameField.setEnabled(true);

			this.dto.passwordField.setPlaceholder("password");
			this.dto.passwordField.setRequiredIndicatorVisible(true);
			this.dto.passwordField.setVisible(true);
			this.dto.passwordField.setEnabled(true);

			this.dto.signupBtn.setCaption("New user?");
			this.dto.signupBtn.setVisible(true);
			this.dto.signupBtn.setEnabled(true);

			this.dto.forgotPasswordBtn.setCaption("Forgot password?");
			this.dto.forgotPasswordBtn.setVisible(true);
			this.dto.forgotPasswordBtn.setEnabled(true);

			this.dto.confirmAccountBtn.setCaption("Confirm user");
			this.dto.confirmAccountBtn.setVisible(true);
			this.dto.confirmAccountBtn.setEnabled(true);

			this.dto.loginBtn.setCaption("Login");
			this.dto.loginBtn.setVisible(true);
			this.dto.loginBtn.setEnabled(true);

		}

		protected void initStyle() {
			this.dto.usernameField.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_INLINE_ICON);
			this.dto.passwordField.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_INLINE_ICON);
			this.dto.signupBtn.addStyleNames(ValoTheme.BUTTON_QUIET, ValoTheme.BUTTON_LINK);
			this.dto.forgotPasswordBtn.addStyleNames(ValoTheme.BUTTON_QUIET, ValoTheme.BUTTON_LINK);
			this.dto.confirmAccountBtn.addStyleNames(ValoTheme.BUTTON_QUIET, ValoTheme.BUTTON_LINK);
		}

		protected void initSizeAndAlignment() {
		}

		protected void initComponents() {
			this.dto.rootPanel.setContent(this.dto.rootLayout);

			this.dto.rootLayout.addComponent(this.dto.usernameField);

			this.dto.rootLayout.addComponent(this.dto.passwordField);
			HorizontalLayout buttonLayout = new HorizontalLayout(this.dto.signupBtn, this.dto.forgotPasswordBtn,
					this.dto.confirmAccountBtn);

			this.dto.rootLayout.addComponents(buttonLayout, this.dto.loginBtn);
		}

		protected void initListener() {
			EmptyFieldListener<String> usernameFieldListener = new EmptyFieldListener<String>();
			usernameFieldListener.setSourceField(this.dto.usernameField);
			usernameFieldListener.setTargetBtn(this.dto.loginBtn);
			usernameFieldListener.setMandatoryFields(this.dto.usernameField, this.dto.passwordField);
			this.dto.usernameField.addValueChangeListener(usernameFieldListener);
			EmptyFieldListener<String> passwordFieldListener = new EmptyFieldListener<String>();
			passwordFieldListener.setSourceField(this.dto.passwordField);
			passwordFieldListener.setTargetBtn(this.dto.loginBtn);
			passwordFieldListener.setMandatoryFields(this.dto.usernameField, this.dto.passwordField);
			this.dto.passwordField.addValueChangeListener(passwordFieldListener);
		}
	}

	@Builder
	@Data
	static class UserViewDTO {
		@Builder.Default
		private Panel rootPanel = new Panel();
		@Builder.Default
		private VerticalLayout rootLayout = new VerticalLayout();
		@Builder.Default
		private TextField usernameField = new TextField();
		@Builder.Default
		private PasswordField passwordField = new PasswordField();
		@Builder.Default
		private Button signupBtn = new Button();
		@Builder.Default
		private Button forgotPasswordBtn = new Button();
		@Builder.Default
		private Button confirmAccountBtn = new Button();
		@Builder.Default
		private Button loginBtn = new Button();
	}
}
