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

import io.college.cms.core.application.Utils;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.model.ViewConstants;
import io.college.cms.core.ui.util.ListenerUtility;
import io.college.cms.core.user.service.SecurityService;
import lombok.Builder;
import lombok.Data;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LoginUserView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private LoginUserViewService loginViewService;
	private ApplicationContext app;
	private Window mainWindow = new Window();
	private SecurityService securityService;

	@Autowired
	public LoginUserView(ApplicationContext app) {
		super();
		this.loginViewService = new LoginUserViewService();
		this.app = app;
		this.securityService = app.getBean(SecurityService.class);
	}

	@PostConstruct
	public void paint() {
		setWidth("60%");

		addComponent(loginViewService.getDto().getRootPanel());
		this.loginViewService.dto.signupBtn.addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), this.loginViewService.dto.signupBtn)) {
				return;
			}
			Window window = new Window();
			window.center();
			window.setResizable(false);
			window.setContent(app.getBean(FindUsernameView.class));
			getUI().addWindow(window);
		});
		this.loginViewService.dto.confirmAccountBtn.addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(),
					this.loginViewService.dto.confirmAccountBtn)) {
				return;
			}

			Window window = new Window();
			window.center();
			window.setResizable(false);
			window.setContent(app.getBean(ConfirmUserView.class));
			getUI().addWindow(window);

		});
		this.loginViewService.dto.loginBtn.addClickListener(click -> {
			String username = Utils.val(loginViewService.getDto().getUsernameField());
			String password = Utils.val(loginViewService.getDto().getPasswordField());
			try {
				securityService.authenticate(username, password);
				mainWindow.close();

				/*
				 * for(Window window: getUI().getWindows()){ window.close(); }
				 */
				getUI().getNavigator().navigateTo(ViewConstants.USER_PROFILE_VIEW);
			} catch (Exception ex) {
				Utils.showErrorNotification(ex.getLocalizedMessage());
			}

		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		VerticalLayout layout = new VerticalLayout();
		loginViewService.getDto().getRootPanel().setWidth("40%");
		layout.addComponent(loginViewService.getDto().getRootPanel());
		layout.setComponentAlignment(loginViewService.getDto().getRootPanel(), Alignment.MIDDLE_CENTER);

		layout.setSizeFull();
		Panel panel = new Panel();
		// panel.setWidth("60%");
		panel.setContent(layout);
		panel.setCaption("<h2>College CMS</h2>");
		panel.setCaptionAsHtml(true);
		mainWindow.setContent(panel);

		mainWindow.center();
		mainWindow.setSizeFull();
		mainWindow.setResizable(false);
		mainWindow.setClosable(false);

		// mainWindow.setCaption("Protected Resource");
		event.getNavigator().getUI().addWindow(mainWindow);
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
			this.dto.rootPanel.setCaption("Login!");
			this.dto.rootPanel.setCaptionAsHtml(true);
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
