package io.college.cms.core.user.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.application.Utils;
import io.college.cms.core.ui.builder.MessagePopupView;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.model.ViewConstants;
import io.college.cms.core.ui.util.ListenerUtility;
import io.college.cms.core.user.service.UserResponseService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class FindUsernameView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private FindUsernameViewService findUsernameService;
	private UserResponseService userResponseService;
	private ApplicationContext app;
	private Map<String, String> params;
	private boolean firstWindowAdded = false;

	public Map<String, String> getParams() {
		return params;
	}

	/**
	 * @param userResponseService
	 */
	@Autowired
	public FindUsernameView(ApplicationContext app, UserResponseService userResponseService) {
		super();

		this.userResponseService = userResponseService;
		this.findUsernameService = new FindUsernameViewService();
		this.app = app;
		params = new HashMap<>();
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
		addComponent(this.findUsernameService.getPanel());
		this.findUsernameService.getConfirmButton().addClickListener(click -> {

			FactoryResponse fr = FactoryResponse.builder().build();

			if (!ListenerUtility.isValidSourceEvent(click.getComponent(),
					this.findUsernameService.getConfirmButton())) {
				return;
			}
			// if the userModel is null, means event is triggered by user
			// and not programmatically from select users screen.
			// we wanna check for field values to validate against. like
			// username
			fr = this.userResponseService.getUserByUser(null,
					Utils.val(this.findUsernameService.getUsernameFld().getOptionalValue()));
			// if the request is success means we have an existing
			// username available in database
			// TODO: have to compare msg for failure, in-case it says
			// resource is not found only the would we allow the user to
			// continue further.

			if (fr != null && SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
				MessagePopupView message = new MessagePopupView("Username is available",
						"Username is available to register, click to continue", 40.0f);
				if (!firstWindowAdded) {
					getUI().addWindow(message);
					firstWindowAdded = true;
				}

				message.addClickListener(success -> {

					getUI().getNavigator().navigateTo(
							ViewConstants.USER_PROFILE_VIEW + "/" + findUsernameService.getUsernameFld().getValue());

				});

			} else {

				this.findUsernameService.getUsernameFld().setCaption("Username is already taken.");
				this.findUsernameService.getUsernameFld().setCaptionAsHtml(true);
				MessagePopupView message = new MessagePopupView("Username exists!",
						"Username is not available to register!,", 40.0f);
				getUI().addWindow(message);
				this.findUsernameService.getUsernameFld().setValue("");
			}
		});
	}

	/**
	 * presentation layer for findusernameview
	 * 
	 * @author Ankit
	 *
	 */
	@Data
	private static class FindUsernameViewService {
		private Panel panel;
		private VerticalLayout rootLayout;
		private TextField usernameFld;

		private Button confirmButton;

		FindUsernameViewService() {
			initUI();
			paint();
		}

		protected void initUI() {
			this.panel = new Panel();
			// initializing a vertical layout
			this.rootLayout = new VerticalLayout();
			// initializing and setting attributes using a TextFieldWrapper
			// builder
			this.usernameFld = VaadinWrapper.builder().placeholder("search username")
					.caption("Enter desired username you would like to register!")
					.description("username provided during signup").icon(VaadinIcons.USER).build().textField();
			this.panel.setCaption("Register user!");
			this.panel.setCaptionAsHtml(true);
			this.confirmButton = new Button("Check Availability!");
			this.confirmButton.setEnabled(false);

			this.panel.setContent(this.rootLayout);
			this.panel.setSizeFull();
			this.rootLayout.setSizeFull();
			this.usernameFld.setSizeFull();
		}

		protected void paint() {
			this.usernameFld.setIcon(VaadinIcons.USER);

			this.confirmButton.addStyleNames(ValoTheme.BUTTON_PRIMARY);
			// by adding it to root layout, it allows us to show elements on UI.
			this.rootLayout.addComponents(this.usernameFld, this.confirmButton);
			this.rootLayout.setComponentAlignment(this.confirmButton, Alignment.MIDDLE_RIGHT);

			EmptyFieldListener<String> usernameListener = (EmptyFieldListener<String>) getEmptyFieldListener();
			usernameListener.setSourceField(this.getUsernameFld());
			this.getUsernameFld().addValueChangeListener(usernameListener);

		}

		protected EmptyFieldListener<?> getEmptyFieldListener() {
			EmptyFieldListener<?> lis = new EmptyFieldListener<>();
			lis.setMandatoryFields(this.usernameFld);
			lis.setTargetBtn(this.confirmButton);
			return lis;
		}
	}
}
