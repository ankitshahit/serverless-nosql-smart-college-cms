package io.college.cms.core.notification.controller;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
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
import com.vaadin.ui.ComboBox;
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
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ConfigureNotificationView extends VerticalLayout implements View {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ComboBox<String> selectCourse;
	private CheckBoxGroup<String> subscribePreferencesCb;
	private Label sendViaMessageLbl;
	private CheckBoxGroup<String> communicationPreferencesCb;
	private Button reset = new Button();
	private Button save = new Button();
	private CoreUiService coreUi;
	private Binder<ConfigureNotificationModel> binder;
	private NotificationResponseService notificationResponseService;

	/**
	 * @param coreUi
	 */
	public ConfigureNotificationView(CoreUiService coreUi, NotificationResponseService notificationResponseService) {
		super();
		this.coreUi = coreUi;
		this.notificationResponseService = notificationResponseService;
		this.binder = new Binder<>();
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
		this.selectCourse = coreUi.getCoursesList();

		this.subscribePreferencesCb.setItems("Admission", "Exam", "Announcement", "Results", "Document update",
				"Job posting", "Assignment submission");
		this.selectCourse.setSizeFull();
		HorizontalLayout itemsList = new HorizontalLayout();
		itemsList.addComponent(subscribePreferencesCb);
		Panel panelLeft2 = new Panel();
		panelLeft2.setContent(itemsList);
		VerticalLayout layoutLeft = new VerticalLayout();
		layoutLeft.addComponent(selectCourse);
		layoutLeft.addComponent(panelLeft2);
		Panel panelLeft = new Panel();
		panelLeft.setContent(layoutLeft);

		sendViaMessageLbl.setCaption("<h3><b>Send communication via</b>: </h3>");
		sendViaMessageLbl.setCaptionAsHtml(true);
		communicationPreferencesCb.setItems("Email", "My notification");

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
		binder.bind(this.selectCourse, ConfigureNotificationModel::getCourseName,
				ConfigureNotificationModel::setCourseName);

		this.save.addClickListener(click -> {
			ConfigureNotificationModel model = ConfigureNotificationModel.builder().build();
			try {
				binder.writeBean(model);
				if (CollectionUtils.isNotEmpty(this.subscribePreferencesCb.getSelectedItems())) {
					model.setMyNotification(this.subscribePreferencesCb.getSelectedItems()
							.contains(NotificationType.NotificationMode.MY_NOTIFICATIONS));
					model.setEmail(this.subscribePreferencesCb.getSelectedItems()
							.contains(NotificationType.NotificationMode.EMAIL));
				}
				FactoryResponse fr = notificationResponseService.saveNotificationConfiguration(model);
				Utils.showFactoryResponseOnlyError(fr);
				if (fr != null && SummaryMessageEnum.SUCCESS == fr.getSummaryMessage()) {
					MessagePopupView message = new MessagePopupView("Success", "Saved configuration!", 30.0f);
					getUI().addWindow(message);
				}
			} catch (ValidationException e) {
				LOGGER.error(e.getMessage());
			}

		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			this.coreUi.setItemsCourseNames(this.selectCourse);
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
