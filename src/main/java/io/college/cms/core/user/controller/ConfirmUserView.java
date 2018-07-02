package io.college.cms.core.user.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

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

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.Utils;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.util.ListenerUtility;
import io.college.cms.core.user.service.UserResponseService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ConfirmUserView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private ConfirmUserViewService confirmUserService;
	private UserResponseService userResponseService;

	public ConfirmUserView() {
		this.confirmUserService = new ConfirmUserViewService();
	}

	@Autowired
	public void setUserResponseService(UserResponseService userResponseService) {
		this.userResponseService = userResponseService;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		LOGGER.debug("event triggered");
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

	@PostConstruct
	protected void paint() {
		this.confirmUserService.getConfirmButton().addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), this.confirmUserService.getConfirmButton())) {
				return;
			}
			// TODO: to allow confirmation of phone, email and other stuff
			// seperately in future.
			FactoryResponse fr = userResponseService.confirmUserSignup(
					Utils.val(this.confirmUserService.getUsernameFld().getOptionalValue()),
					Utils.val(this.confirmUserService.getConfirmCodeFld().getOptionalValue()));
			Utils.showFactoryResponseMsg(fr);
		});
		addComponent(this.confirmUserService.getPanel());
	}

	/**
	 * presentation layer for confirmSignup
	 * 
	 * @author Ankit
	 *
	 */
	@Data
	private static class ConfirmUserViewService {
		private Panel panel;
		private VerticalLayout rootLayout;
		private TextField usernameFld;
		private TextField confirmCodeFld;
		private Button confirmButton;

		ConfirmUserViewService() {
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
