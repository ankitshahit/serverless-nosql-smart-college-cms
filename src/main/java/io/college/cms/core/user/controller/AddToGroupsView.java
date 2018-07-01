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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.application.Utils;
import io.college.cms.core.ui.builder.MessagePopupView;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.services.CoreUiService;
import io.college.cms.core.ui.util.ElementHelper;
import io.college.cms.core.ui.util.ListenerUtility;
import io.college.cms.core.user.model.GroupModel;
import io.college.cms.core.user.service.GroupResponseService;
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
	private RadioButtonGroup<String> groupsCheckBoxGroup;
	private CoreUiService uiService;
	private GroupResponseService groupResponseService;
	private ProgressBar progressBar;

	/**
	 * @param uiService
	 */
	@Autowired
	public AddToGroupsView(CoreUiService uiService, GroupResponseService groupResponseService) {
		super();
		this.uiService = uiService;
		this.groupResponseService = groupResponseService;
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void paint() {
		this.progressBar = new ProgressBar();
		this.progressBar.setVisible(false);
		this.rootPanel = new Panel();
		this.rootLayout = new VerticalLayout();
		this.groupNames = new ComboBox<String>();
		this.userNames = new ComboBox<String>();
		this.moveToButton = new Button();
		this.saveBtn = new Button();
		this.groupsCheckBoxGroup = new RadioButtonGroup<String>();
		addComponent(rootPanel);
		setComponentAlignment(rootPanel, Alignment.MIDDLE_CENTER);
		rootPanel.setContent(rootLayout);
		rootPanel.setCaption("Move users from group!");
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

		this.groupsCheckBoxGroup.setEnabled(true);
		this.groupsCheckBoxGroup.setVisible(true);
		this.groupsCheckBoxGroup.setRequiredIndicatorVisible(true);
		this.groupsCheckBoxGroup.setItems("Student", "Staff", "Archive");
		this.groupNames.setItems("Student", "Staff", "Archive");
		VerticalLayout firstLayout = new VerticalLayout();
		firstLayout.addComponents(this.groupNames, this.userNames);

		VerticalLayout secondLayout = new VerticalLayout();
		this.groupsCheckBoxGroup.setCaption("<p><b>Select groups to be added into</b></p>");
		this.groupsCheckBoxGroup.setCaptionAsHtml(true);
		this.groupsCheckBoxGroup.setHtmlContentAllowed(true);
		this.groupsCheckBoxGroup.setSizeFull();
		Label msgLbl = VaadinWrapper.builder().caption("Note ").build().label();
		msgLbl.setValue(
				"Groups define level of access <br/>provided in CMS, moving from one group to <br/>another will <b>impact what an user can do<br/> and not do!</b>");
		secondLayout.addComponents(this.groupsCheckBoxGroup, msgLbl);

		Panel firstPanel = new Panel(firstLayout);
		Panel secondPanel = new Panel(secondLayout);
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(firstPanel, secondPanel);
		splitPanel.setSplitPosition(62.0f);
		rootLayout.addComponents(splitPanel, this.moveToButton);
		rootLayout.setComponentAlignment(this.moveToButton, Alignment.BOTTOM_RIGHT);
		// this.rootLayout.setComponentAlignment(this.saveBtn,
		// Alignment.BOTTOM_RIGHT);
		EmptyFieldListener<String> groupNamesListener = new EmptyFieldListener<String>();

		groupNamesListener.setSourceListField(groupNames);
		groupNamesListener.setTargetBtn(moveToButton);
		groupNamesListener.setMandatoryListFields(groupNames, userNames, this.groupsCheckBoxGroup);
		groupNames.addValueChangeListener(groupNamesListener);
		EmptyFieldListener<String> userNamesListener = new EmptyFieldListener<String>();
		userNamesListener.setSourceListField(userNames);
		userNamesListener.setTargetBtn(moveToButton);
		userNamesListener.setMandatoryListFields(groupNames, userNames, this.groupsCheckBoxGroup);
		EmptyFieldListener<String> radioButtonListener = new EmptyFieldListener<String>();
		radioButtonListener.setSourceListField(this.groupsCheckBoxGroup);
		radioButtonListener.setTargetBtn(moveToButton);
		radioButtonListener.setMandatoryListFields(groupNames, userNames, this.groupsCheckBoxGroup);
		this.groupsCheckBoxGroup.addValueChangeListener(radioButtonListener);
		userNames.addValueChangeListener(userNamesListener);
		this.groupNames.addValueChangeListener(value -> {
			if (!ListenerUtility.isValidSourceEvent(this.groupNames, value.getComponent())
					&& this.groupNames.getOptionalValue().isPresent() == false) {
				return;
			}
			this.progressBar.setVisible(true);
			Window window = new Window();
			window.setResizable(false);
			this.progressBar.setSizeFull();
			window.setContent(this.progressBar);
			window.center();
			getUI().addWindow(window);
			FactoryResponse fr = groupResponseService.listGroup(null,
					GroupModel.builder().groupName(value.getValue()).build());
			this.progressBar.setValue(10.0f);
			Utils.showFactoryResponseOnlyError(fr);
			if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {

				this.progressBar.setVisible(false);
				window.close();
				return;
			}
			this.progressBar.setValue(70.0f);
			GroupModel users = (GroupModel) fr.getResponse();
			if (users == null) {
				Utils.showErrorNotification("Unable to fetch users for group");

				this.progressBar.setVisible(false);
				window.close();
				return;
			}
			this.progressBar.setValue(100.0f);
			this.userNames.setItems(users.getUsernames());
			this.progressBar.setVisible(false);

			window.close();

		});

		this.moveToButton.addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(this.moveToButton, click.getComponent())) {
				return;
			}
			FactoryResponse fr = groupResponseService.removeUserFromGroup(null, GroupModel.builder()
					.groupName(this.groupNames.getValue()).withUsername(this.userNames.getValue()).build());
			Utils.showFactoryResponseOnlyError(fr);
			fr = groupResponseService.addUserToGroup(null, GroupModel.builder()
					.groupName(this.groupsCheckBoxGroup.getValue()).withUsername(this.userNames.getValue()).build());
			Utils.showFactoryResponseOnlyError(fr);
			if (fr != null && SummaryMessageEnum.SUCCESS == fr.getSummaryMessage()) {
				MessagePopupView message = new MessagePopupView("Moved!", "user is successfully moved", 30.0f);
				getUI().addWindow(message);
				this.userNames.clear();
				this.userNames.setItems("");
				this.groupsCheckBoxGroup.clear();
				ElementHelper.removeComponentError(this.userNames);
			}
		});
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
