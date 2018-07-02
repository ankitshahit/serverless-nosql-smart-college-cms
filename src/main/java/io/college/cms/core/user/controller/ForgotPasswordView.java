package io.college.cms.core.user.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
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
public class ForgotPasswordView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private PasswordViewService view;
	private AWSCognitoIdentityProvider provider;
	private ApplicationContext app;

	@Autowired
	public ForgotPasswordView(AWSCognitoIdentityProvider provider, ApplicationContext app) {
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
			ForgotPasswordRequest request = app.getBean(ForgotPasswordRequest.class);
			request.setUsername(Utils.val(this.view.getUsernameFld()));
			try {
				provider.forgotPassword(request);
				MessagePopupView message = new MessagePopupView("Reset!",
						"We have sent a confirmation code to registered email.<br/>Please provide confirmation code on confirm password page",
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
			this.usernameFld = VaadinWrapper.builder().placeholder("username")
					.description("username provided during signup").icon(VaadinIcons.USER).build().textField();
			// initializing confirmation and setting attributes using a
			// textFieldWrapper builder
			this.confirmCodeFld = VaadinWrapper.builder().placeholder("confirmation code")
					.description("Provide confirmation code received in email").icon(VaadinIcons.CODE).maxLength(6)
					.build().textField();
			this.confirmButton = new Button("Confirm");
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
			this.rootLayout.addComponents(this.usernameFld, this.confirmCodeFld, this.confirmButton);
			this.rootLayout.setComponentAlignment(this.confirmButton, Alignment.MIDDLE_RIGHT);

			EmptyFieldListener<String> usernameListener = (EmptyFieldListener<String>) getEmptyFieldListener();
			usernameListener.setSourceField(this.getUsernameFld());
			this.getUsernameFld().addValueChangeListener(usernameListener);

			EmptyFieldListener<String> confirmListener = (EmptyFieldListener<String>) getEmptyFieldListener();
			confirmListener.setSourceField(this.getConfirmCodeFld());
			this.getConfirmCodeFld().addValueChangeListener(confirmListener);

		}

		protected EmptyFieldListener<?> getEmptyFieldListener() {
			EmptyFieldListener<?> lis = new EmptyFieldListener<>();
			lis.setMandatoryFields(this.usernameFld, this.confirmCodeFld);
			lis.setTargetBtn(this.confirmButton);
			return lis;
		}
	}
}
