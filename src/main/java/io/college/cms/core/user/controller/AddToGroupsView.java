package io.college.cms.core.user.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class AddToGroupsView extends VerticalLayout implements View {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Panel rootPanel;
	private VerticalLayout rootLayout;
	private ComboBox<String> groupNames;
	private ComboBox<String> userNames;
	private Button moveToButton;
	private Button saveBtn;
	private CheckBoxGroup<String> groupsCheckBoxGroup;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void paint() {

		this.rootPanel = new Panel();
		this.rootLayout = new VerticalLayout();
		this.groupNames = new ComboBox<String>();
		this.userNames = new ComboBox<String>();
		this.moveToButton = new Button();
		this.saveBtn = new Button();
		this.groupsCheckBoxGroup = new CheckBoxGroup<String>();
		addComponent(rootPanel);
		setComponentAlignment(rootPanel, Alignment.MIDDLE_CENTER);
		rootPanel.setContent(rootLayout);
		groupNames = (ComboBox<String>) VaadinWrapper.builder().caption("User groups").placeholder("search by group")
				.required(true).visible(true).enabled(true).build().comboBox();
		rootLayout.addComponent(groupNames);
		userNames = (ComboBox<String>) VaadinWrapper.builder().caption("Users").placeholder("search by username")
				.required(true).visible(true).enabled(true).build().comboBox();
		rootLayout.addComponent(userNames);
		moveToButton = VaadinWrapper.builder().caption("Move to group").required(true).visible(true).enabled(false)
				.build().button();
		rootLayout.addComponent(moveToButton);
		saveBtn = VaadinWrapper.builder().caption("Save settings").required(true).visible(true).enabled(true).build()
				.button();
		this.saveBtn.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
		this.saveBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
		this.saveBtn.setCaption("Save settings");
		this.moveToButton.setCaption("Move to");
		this.groupsCheckBoxGroup.setItems("Group 1", "Group 2");
		this.groupsCheckBoxGroup.setEnabled(true);
		this.groupsCheckBoxGroup.setVisible(true);
		this.groupsCheckBoxGroup.setRequiredIndicatorVisible(true);
		VerticalLayout firstLayout = new VerticalLayout();
		firstLayout.addComponents(this.groupNames, this.userNames, this.moveToButton);
		firstLayout.setComponentAlignment(this.moveToButton, Alignment.MIDDLE_RIGHT);
		VerticalLayout secondLayout = new VerticalLayout();
		this.groupsCheckBoxGroup.setCaption("<p><b>Select groups to be added into</b></p>");
		this.groupsCheckBoxGroup.setCaptionAsHtml(true);
		this.groupsCheckBoxGroup.setHtmlContentAllowed(true);
		this.groupsCheckBoxGroup.setSizeFull();
		secondLayout.addComponents(this.groupsCheckBoxGroup);
		Panel firstPanel = new Panel(firstLayout);
		Panel secondPanel = new Panel(secondLayout);
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(firstPanel, secondPanel);
		splitPanel.setSplitPosition(62.0f);
		rootLayout.addComponents(splitPanel, this.saveBtn);
		this.rootLayout.setComponentAlignment(this.saveBtn, Alignment.BOTTOM_RIGHT);
		EmptyFieldListener<String> groupNamesListener = new EmptyFieldListener<String>();
		groupNamesListener.setSourceListField(groupNames);
		groupNamesListener.setTargetBtn(moveToButton);
		groupNamesListener.setMandatoryListFields(groupNames, userNames);
		groupNames.addValueChangeListener(groupNamesListener);
		EmptyFieldListener<String> userNamesListener = new EmptyFieldListener<String>();
		userNamesListener.setSourceListField(userNames);
		userNamesListener.setTargetBtn(moveToButton);
		userNamesListener.setMandatoryListFields(groupNames, userNames);
		userNames.addValueChangeListener(userNamesListener);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
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
