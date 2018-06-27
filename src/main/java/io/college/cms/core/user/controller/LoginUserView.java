package io.college.cms.core.user.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.util.ListenerUtility;
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
	private ApplicationContext app;

	@Autowired
	public LoginUserView(ApplicationContext app) {
		super();
		this.loginViewService = new LoginUserViewService();
		this.app = app;
	}

	@PostConstruct
	public void paint() {
		setWidth("60%");
		addComponent(loginViewService.getDto().getRootPanel());
		this.loginViewService.dto.confirmAccountBtn.addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(),
					this.loginViewService.dto.confirmAccountBtn)) {
				return;
			}
			ConfirmUserView confirmUser = app.getBean(ConfirmUserView.class);
			Window window = new Window();
			window.center();
			window.setResizable(false);
			window.setContent(confirmUser);
			getUI().addWindow(window);
			confirmUser.enter(null);
		});
	}

	@Override
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
			initComponents();
			initListener();
		}

		/**
		 * Abstracting setting caption, placeholders and icons
		 */
		protected void initUI() {

			this.dto.usernameField = VaadinWrapper.builder().caption("Username").icon(VaadinIcons.USERS)
					.placeholder("username").build().textField();

			this.dto.passwordField = VaadinWrapper.builder().caption("Password").icon(VaadinIcons.PASSWORD).build()
					.passwordField();
			this.dto.signupBtn = VaadinWrapper.builder().build().button();
			this.dto.signupBtn.setCaption("New user?");
			this.dto.signupBtn.setIcon(VaadinIcons.SIGN_IN);
			this.dto.signupBtn.setStyleName(ValoTheme.BUTTON_QUIET);

			this.dto.forgotPasswordBtn = VaadinWrapper.builder().build().button();
			this.dto.forgotPasswordBtn.setCaption("Forgot password?");
			this.dto.forgotPasswordBtn.setStyleName(ValoTheme.BUTTON_QUIET);
			this.dto.forgotPasswordBtn.setIcon(VaadinIcons.FORM);
			this.dto.confirmAccountBtn = VaadinWrapper.builder().build().button();
			this.dto.confirmAccountBtn.setCaption("Confirm user");
			this.dto.confirmAccountBtn.setIcon(VaadinIcons.ADD_DOCK);
			this.dto.confirmAccountBtn.setStyleName(ValoTheme.BUTTON_QUIET);
			
			this.dto.loginBtn = VaadinWrapper.builder().build().button();
			this.dto.loginBtn.setCaption("Login");
			this.dto.loginBtn.setEnabled(false);
			this.dto.loginBtn.setIcon(VaadinIcons.SIGN_IN);
			this.dto.loginBtn.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
			this.dto.loginBtn.addStyleNames(ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_LARGE);

		}

		protected void initComponents() {
			this.dto.rootPanel.setContent(this.dto.rootLayout);

			this.dto.rootLayout.addComponent(this.dto.usernameField);

			this.dto.rootLayout.addComponent(this.dto.passwordField);
			HorizontalLayout buttonLayout = new HorizontalLayout(this.dto.signupBtn, this.dto.forgotPasswordBtn,
					this.dto.confirmAccountBtn);

			this.dto.rootLayout.addComponents(buttonLayout, this.dto.loginBtn);
			this.dto.rootLayout.setComponentAlignment(this.dto.loginBtn, Alignment.BOTTOM_RIGHT);
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
