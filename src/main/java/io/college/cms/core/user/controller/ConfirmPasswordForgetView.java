package io.college.cms.core.user.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.Utils;
import io.college.cms.core.ui.builder.MessagePopupView;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.util.ListenerUtility;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ConfirmPasswordForgetView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private PasswordViewService view;
	private AWSCognitoIdentityProvider provider;
	private ApplicationContext app;

	@Autowired
	public ConfirmPasswordForgetView(AWSCognitoIdentityProvider provider, ApplicationContext app) {
		super();
		this.provider = provider;
		this.app = app;
		this.view = new PasswordViewService();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

	@PostConstruct
	protected void paint() {
		this.view.getConfirmButton().addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), this.view.getConfirmButton())) {
				return;
			}
			ConfirmForgotPasswordRequest request = app.getBean(ConfirmForgotPasswordRequest.class);
			request.setUsername(Utils.val(this.view.getUsernameFld()));
			request.setPassword(Utils.val(view.getPasswordField()));
			request.setConfirmationCode(Utils.val(this.view.getConfirmCodeFld()));
			try {
				provider.confirmForgotPassword(request);
				MessagePopupView message = new MessagePopupView("Reset!", "We have reset your password successfully.",
						30.0f);
				getUI().addWindow(message);
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage());
				Utils.showErrorNotification("We couldn't reset your password.");
			}
		});
		addComponent(this.view.getPanel());
	}

	/**
	 * presentation layer for confirmSignup
	 * 
	 * @author Ankit
	 *
	 */
	@Data
	private static class PasswordViewService {
		private Panel panel;
		private VerticalLayout rootLayout;
		private TextField usernameFld;
		private TextField confirmCodeFld;
		private Button confirmButton;
		private PasswordField passwordField;

		PasswordViewService() {
			initUI();
			paint();

		}

		protected void initUI() {
			this.panel = new Panel();
			// initializing a vertical layout
			this.rootLayout = new VerticalLayout();
			// initializing and setting attributes using a TextFieldWrapper
			// builder
			this.usernameFld = VaadinWrapper.builder().placeholder("username").caption("username")
					.description("username provided during signup").icon(VaadinIcons.USER).build().textField();
			// initializing confirmation and setting attributes using a
			// textFieldWrapper builder
			this.confirmCodeFld = VaadinWrapper.builder().caption("Confirmation code").placeholder("confirmation code")
					.description("Provide confirmation code received in email").icon(VaadinIcons.CODE).maxLength(6)
					.build().textField();
			this.confirmButton = new Button("Confirm");
			this.passwordField = VaadinWrapper.builder().caption("New password").placeholder("New password")
					.icon(VaadinIcons.PASSWORD).build().passwordField();
			this.confirmButton.setEnabled(false);
			this.panel.setContent(this.rootLayout);
			this.panel.setSizeFull();
			this.rootLayout.setSizeFull();
			this.usernameFld.setSizeFull();
			this.confirmCodeFld.setSizeFull();
		}

		@SuppressWarnings("unchecked")
		protected void paint() {
			this.usernameFld.setIcon(VaadinIcons.USER);
			this.confirmCodeFld.setIcon(VaadinIcons.CODE);
			this.confirmButton.addStyleNames(ValoTheme.BUTTON_PRIMARY);
			// by adding it to root layout, it allows us to show elements on UI.
			Label passwordGuideLinesLbl = VaadinWrapper.builder().caption("Password Guidelines").build().label();
			passwordGuideLinesLbl.setValue(new StringBuilder().append("<b>Should be greater than 8 characters</b>")
					.append("<br/><b>Should have one capital character<br/>Should have one lowercase character<br/>Should have one digit<br/>Should have special character</b>")
					.toString());
			this.rootLayout.addComponents(this.usernameFld, this.confirmCodeFld, this.passwordField,
					passwordGuideLinesLbl, this.confirmButton);
			this.rootLayout.setComponentAlignment(this.confirmButton, Alignment.MIDDLE_RIGHT);

			EmptyFieldListener<String> usernameListener = (EmptyFieldListener<String>) getEmptyFieldListener();
			usernameListener.setSourceField(this.getUsernameFld());
			this.getUsernameFld().addValueChangeListener(usernameListener);

			EmptyFieldListener<String> confirmListener = (EmptyFieldListener<String>) getEmptyFieldListener();
			confirmListener.setSourceField(this.getConfirmCodeFld());
			this.getConfirmCodeFld().addValueChangeListener(confirmListener);

			EmptyFieldListener<String> passwordFieldListener = (EmptyFieldListener<String>) getEmptyFieldListener();
			passwordFieldListener.setSourceField(this.getConfirmCodeFld());
			this.getPasswordField().addValueChangeListener(passwordFieldListener);

		}

		protected EmptyFieldListener<?> getEmptyFieldListener() {
			EmptyFieldListener<?> lis = new EmptyFieldListener<>();
			lis.setMandatoryFields(this.usernameFld, this.confirmCodeFld, this.passwordField);
			lis.setTargetBtn(this.confirmButton);
			return lis;
		}
	}
}
