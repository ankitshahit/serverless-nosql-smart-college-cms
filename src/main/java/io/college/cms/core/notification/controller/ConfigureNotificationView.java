package io.college.cms.core.notification.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.application.Utils;
import io.college.cms.core.notification.model.ConfigureNotificationModel;
import io.college.cms.core.notification.model.NotificationType;
import io.college.cms.core.notification.services.NotificationResponseService;
import io.college.cms.core.ui.builder.MessagePopupView;
import io.college.cms.core.ui.services.CoreUiService;
import io.college.cms.core.user.service.SecurityService;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ConfigureNotificationView extends VerticalLayout implements View {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// private ComboBox<String> selectCourse;
	private CheckBoxGroup<String> subscribePreferencesCb;
	private Label sendViaMessageLbl;
	private CheckBoxGroup<String> communicationPreferencesCb;
	private Button reset = new Button();
	private Button save = new Button();
	private CoreUiService coreUi;
	private Binder<ConfigureNotificationModel> binder;
	private NotificationResponseService notificationResponseService;
	private SecurityService securityService;

	/**
	 * @param coreUi
	 */
	public ConfigureNotificationView(CoreUiService coreUi, NotificationResponseService notificationResponseService,
			SecurityService securityService) {
		super();
		this.coreUi = coreUi;
		this.notificationResponseService = notificationResponseService;
		this.binder = new Binder<>();
		this.securityService = securityService;
	}

	@PostConstruct
	public void paint() {
		Panel panel = new Panel();
		VerticalLayout rootLayout = new VerticalLayout();
		addComponent(panel);
		setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
		panel.setContent(rootLayout);
		this.subscribePreferencesCb = new CheckBoxGroup<>();
		this.communicationPreferencesCb = new CheckBoxGroup<>();
		this.sendViaMessageLbl = coreUi.getLabel();

		this.subscribePreferencesCb.setItems("Admission", "Exam", "Announcement", "Results", "Document update",
				"Job posting", "Assignment submission");
		HorizontalLayout itemsList = new HorizontalLayout();
		itemsList.addComponent(subscribePreferencesCb);
		Panel panelLeft2 = new Panel();
		panelLeft2.setContent(itemsList);
		VerticalLayout layoutLeft = new VerticalLayout();
		layoutLeft.addComponent(panelLeft2);
		Panel panelLeft = new Panel();
		panelLeft.setContent(layoutLeft);

		sendViaMessageLbl.setCaption("<h3><b>Send communication via</b>: </h3>");
		sendViaMessageLbl.setCaptionAsHtml(true);
		communicationPreferencesCb.setItems(NotificationType.NotificationMode.EMAIL.toString(),
				NotificationType.NotificationMode.MY_NOTIFICATIONS.toString());

		VerticalLayout layoutRight2 = new VerticalLayout();
		layoutRight2.addComponent(communicationPreferencesCb);

		Panel panelRight = new Panel();
		panelRight.setContent(layoutRight2);
		VerticalLayout layoutRight = new VerticalLayout();
		layoutRight.addComponent(sendViaMessageLbl);
		layoutRight.addComponent(panelRight);
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(panelLeft, layoutRight);
		splitPanel.setSplitPosition(62.0f);
		rootLayout.addComponents(splitPanel);

		save.setCaption("Save");
		save.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		save.setVisible(true);
		save.setEnabled(true);
		save.setResponsive(true);
		reset.setCaption("Reset");
		reset.setStyleName(ValoTheme.BUTTON_DANGER);
		reset.setVisible(true);
		reset.setEnabled(true);
		reset.setResponsive(true);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.addComponents(reset, save);

		rootLayout.addComponent(btnLayout);
		rootLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_RIGHT);
		this.save.addStyleName(ValoTheme.BUTTON_PRIMARY);

		this.save.addClickListener(click -> {
			ConfigureNotificationModel model = ConfigureNotificationModel.builder().build();
			try {
				List<String> checkBoxes = new ArrayList<>();
				checkBoxes.addAll(this.subscribePreferencesCb.getSelectedItems());

				model.setMyNotification(this.communicationPreferencesCb.getSelectedItems()
						.contains(NotificationType.NotificationMode.MY_NOTIFICATIONS.toString()));
				model.setEmail(this.communicationPreferencesCb.getSelectedItems()
						.contains(NotificationType.NotificationMode.EMAIL.toString()));
				model.setCheckBoxGroup(checkBoxes);
				model.setUsername(securityService.getPrincipal());
				FactoryResponse fr = notificationResponseService.saveNotificationConfiguration(model);
				Utils.showFactoryResponseOnlyError(fr);
				if (fr != null && SummaryMessageEnum.SUCCESS == fr.getSummaryMessage()) {
					MessagePopupView message = new MessagePopupView("Success", "Saved configuration!", 40.0f);
					getUI().addWindow(message);
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}

		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			if (SecurityService.ANONYMOUS_USER.equalsIgnoreCase(securityService.getPrincipal())) {
				return;
			}
			FactoryResponse fr = notificationResponseService
					.findNotificationConfiguration(securityService.getPrincipal());
			if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
				return;
			}
			ConfigureNotificationModel configureNotification = (ConfigureNotificationModel) fr.getResponse();
			if (configureNotification == null || StringUtils.isEmpty(configureNotification.getUsername())) {
				return;
			}
			if (CollectionUtils.isNotEmpty(configureNotification.getCheckBoxGroup())) {
				// TODO: ideally it should mean that the checkboxs are checked.
				String[] array =  configureNotification.getCheckBoxGroup().toArray(new String[0]);
				this.subscribePreferencesCb
				.select(array);
			}

			String[] checkedMultipleItems = new String[2];
			if (configureNotification.isEmail()) {
				checkedMultipleItems[0] = NotificationType.NotificationMode.EMAIL.toString();
			}
			if (configureNotification.isMyNotification()) {
				checkedMultipleItems[1] = NotificationType.NotificationMode.MY_NOTIFICATIONS.toString();
			}
			if (checkedMultipleItems.length > 0 && checkedMultipleItems[0] != null) {
				communicationPreferencesCb.select(checkedMultipleItems);
			}

			/*
			 * communicationPreferencesCb
			 * .select(configureNotification.isEmail() ?
			 * NotificationType.NotificationMode.EMAIL.toString() : "");
			 */
			// this.communicationPreferencesCb.setItemEnabledProvider(item ->
			// !(checkedMultipleItems.contains(item)));
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
			notifi.setCaption("Application error");
			notifi.setIcon(VaadinIcons.STOP_COG);
			notifi.setDescription(
					"We were unable to process request for some reason! Please try again later or contact admin");
			notifi.setDelayMsec(Notification.DELAY_FOREVER);

		}
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}
}
