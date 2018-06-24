package io.college.cms.core.admission.controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.IconGenerator;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.application.Utils;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.service.CourseResponseService;
import io.college.cms.core.ui.listener.ClearValuesListener;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.util.ElementHelper;
import io.college.cms.core.ui.util.ListenerUtility;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ConfigureAdmissionActionView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private CourseResponseService courseResponseService;
	private ConfigureAdmissionActionViewService configureAdmissionVewService;

	/**
	 * @param courseResponseService
	 */
	@Autowired
	public ConfigureAdmissionActionView(CourseResponseService courseResponseService) {
		super();
		this.courseResponseService = courseResponseService;
		this.configureAdmissionVewService = new ConfigureAdmissionActionViewService();

	}

	@PostConstruct
	protected void paint() {
		addComponents(configureAdmissionVewService.getDto().getRootPanel(), new Label());
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			FactoryResponse courseResponse = courseResponseService.findAllCourses(null, 0L, 0L);

			if (courseResponse == null || SummaryMessageEnum.SUCCESS != courseResponse.getSummaryMessage()) {
				Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
				notifi.setIcon(VaadinIcons.STOP);
				notifi.setCaption("Error");
				notifi.setDescription("We couldn't load course data");
				notifi.setDelayMsec(Notification.DELAY_FOREVER);
				return;
			}
			List<CourseModel> models = null;
			models = (List<CourseModel>) courseResponse.getResponse();
			if (CollectionUtils.isEmpty(models)) {
				Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
				notifi.setIcon(VaadinIcons.STOP);
				notifi.setCaption("Error");
				notifi.setDescription("We couldn't load course data");
				notifi.setDelayMsec(Notification.DELAY_FOREVER);
				return;
			}
			List<String> courseNames = new ArrayList<>();
			models.forEach(course -> {
				courseNames.add(course.getCourseName());
			});
			this.configureAdmissionVewService.getDto().getCoursesList().setItems(courseNames);
		} catch (Exception e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
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
		// we need to clear fields once user leaves the view.
		configureAdmissionVewService.getClearValuesListener().buttonClick(null);
	}

	@Data
	private static class ConfigureAdmissionActionViewService {
		private AdmissionServiceDTO dto;
		private Set<String> addedUsers;

		/**
		 * @param dto
		 */
		public ConfigureAdmissionActionViewService() {
			super();
			dto = AdmissionServiceDTO.builder().rootPanel(new Panel()).coursesList(new ComboBox<String>(""))
					.usersList(new ComboBox<String>("<p><b>Select users</b>: </p>")).addUserBtn(new Button("Add user"))
					.addedUsersList(new ListSelect<String>("<p><b>Requires approval from</b>: </p> "))
					.deleteElementsByListBtn(new Button("")).clearBtn(new Button("Clear"))
					.saveBtn(new Button("Save settings")).build();
			this.addedUsers = new LinkedHashSet<>();

			initVaadinPage();

		}

		protected void initVaadinPage() {
			initUI();
			initStyles();
			initLayout();
			initListener();
		}

		protected void initUI() {
			dto.getCoursesList().setCaption("<p><b>Select for Course</b>: </p>");
			dto.getCoursesList().setCaptionAsHtml(true);
			dto.getCoursesList().setEmptySelectionAllowed(false);
			// dto.getCoursesList().setEmptySelectionCaption("Select option");
			dto.getCoursesList().setPlaceholder("Type course name...");
			dto.getCoursesList().setVisible(true);
			dto.getCoursesList().setEnabled(true);
			dto.getCoursesList().focus();
			dto.getUsersList().setPlaceholder("Type username...");
			dto.getUsersList().setCaptionAsHtml(true);
			dto.getAddedUsersList().setCaptionAsHtml(true);
			dto.getDeleteElementsByListBtn().setVisible(false);

		}

		protected void initStyles() {
			dto.getClearBtn().addStyleNames(ValoTheme.BUTTON_DANGER);
			dto.getCoursesList().addStyleNames(ValoTheme.COMBOBOX_ALIGN_CENTER, ValoTheme.COMBOBOX_HUGE);
			dto.getCoursesList().setWidth("100%");
			dto.getUsersList().addStyleNames(ValoTheme.COMBOBOX_ALIGN_CENTER, ValoTheme.COMBOBOX_HUGE);
			dto.getUsersList().setWidth("100%");

			dto.getUsersList().setItemIconGenerator(new IconGenerator<String>() {
				@Override
				public Resource apply(String item) {
					return VaadinIcons.USER;
				}
			});
			dto.getAddedUsersList().setWidth("100%");
			dto.getUsersList().setItems("User 1", "User 2");
			dto.getUsersList().setRequiredIndicatorVisible(true);
			dto.getCoursesList().setRequiredIndicatorVisible(true);
			dto.getRootPanel().setSizeFull();
			dto.getAddUserBtn().addStyleNames(ValoTheme.BUTTON_FRIENDLY);
			dto.getAddUserBtn().setEnabled(false);
			dto.getSaveBtn().addStyleNames(ValoTheme.BUTTON_PRIMARY);
			dto.getSaveBtn().setEnabled(false);
			dto.getDeleteElementsByListBtn().setIcon(VaadinIcons.CROSS_CUTLERY);
			dto.getDeleteElementsByListBtn().setCaption("Remove");
			dto.getDeleteElementsByListBtn().addStyleNames(ValoTheme.BUTTON_ICON_ONLY, ValoTheme.BUTTON_DANGER);
		}

		protected void initLayout() {

			VerticalLayout verticalLayout = new VerticalLayout();
			HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
			VerticalLayout firstVLayout = new VerticalLayout();
			VerticalLayout secondVLayout = new VerticalLayout();

			firstVLayout.addComponents(dto.getCoursesList(), dto.getUsersList(), dto.getAddUserBtn());
			firstVLayout.setComponentAlignment(dto.getAddUserBtn(), Alignment.MIDDLE_RIGHT);
			secondVLayout.addComponents(dto.getAddedUsersList(), dto.getDeleteElementsByListBtn());

			splitPanel.addComponents(firstVLayout, secondVLayout);
			splitPanel.setSplitPosition(40.0f);
			HorizontalLayout btnLayout = new HorizontalLayout(dto.getClearBtn(), dto.getSaveBtn());
			verticalLayout.addComponents(splitPanel, btnLayout);
			verticalLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_RIGHT);
			btnLayout.setSpacing(true);
			verticalLayout.setSpacing(true);
			dto.getRootPanel().setContent(verticalLayout);
		}

		protected void initListener() {
			dto.getClearBtn().addClickListener(click -> {
				if (!ListenerUtility.isValidSourceEvent(click.getComponent(), dto.getClearBtn())) {
					return;
				}
				dto.getAddedUsersList().clear();
				getClearValuesListener().buttonClick(click);
			});
			dto.getUsersList().addValueChangeListener(select -> {
				if (!ListenerUtility.isValidSourceEvent(select.getComponent(), dto.getUsersList())) {
					return;
				}
				ListenerUtility.emptyValueListener(dto.getCoursesList());
			});
			dto.getAddUserBtn().addClickListener(click -> {
				if (!ListenerUtility.isValidSourceEvent(click.getComponent(), dto.getAddUserBtn())) {
					return;
				}
				String courseName = Utils.val(dto.getCoursesList().getOptionalValue());
				String username = Utils.val(dto.getUsersList().getOptionalValue());
				if (StringUtils.isEmpty(courseName) || StringUtils.isEmpty(username)) {
					Utils.showErrorNotification("Course name or username is empty");
					return;
				}
				if (this.addedUsers.contains(username)) {
					Utils.showErrorNotification(String.format("username %s is already added. ", username));
					return;
				}
				this.addedUsers.add(username);
				dto.getAddedUsersList().setItems(this.addedUsers);
				dto.getUsersList().clear();
				ElementHelper.removeComponentError(dto.getUsersList());
				// workaround for enabling save settings button, we assume that
				// because there's a new entry into listselect instance, we
				// would forcefully enable the button.
				dto.getSaveBtn().setEnabled(true);
			});
			EmptyFieldListener<String> coursesListener = (EmptyFieldListener<String>) getEmptyFieldListenerAddUser();
			coursesListener.setSourceListField(dto.getCoursesList());
			dto.getCoursesList().addValueChangeListener(coursesListener);

			EmptyFieldListener<String> userListener = (EmptyFieldListener<String>) getEmptyFieldListenerAddUser();
			userListener.setSourceListField(dto.getUsersList());
			dto.getUsersList().addValueChangeListener(userListener);

			dto.getAddedUsersList().addValueChangeListener(select -> {
				if (!ListenerUtility.isValidSourceEvent(select.getComponent(), dto.getAddedUsersList())) {
					return;
				}
				dto.getSaveBtn().setEnabled(dto.getAddedUsersList().getOptionalValue().isPresent());
				dto.getDeleteElementsByListBtn().setVisible(dto.getAddedUsersList().getOptionalValue().isPresent());
			});
			dto.getDeleteElementsByListBtn().addClickListener(click -> {
				if (!ListenerUtility.isValidSourceEvent(click.getComponent(), dto.getDeleteElementsByListBtn())) {
					return;
				}
				this.addedUsers.removeAll(dto.getAddedUsersList().getSelectedItems());
				dto.getAddedUsersList().setItems(this.addedUsers);
				dto.getSaveBtn().setEnabled(CollectionUtils.isNotEmpty(this.addedUsers));
			});
		}

		protected ClearValuesListener<?> getClearValuesListener() {
			ClearValuesListener<?> clearValuesListener = new ClearValuesListener<>();
			clearValuesListener.setMandatoryListFields(dto.getCoursesList(), dto.getUsersList());
			clearValuesListener.setMandatoryListFields(dto.getAddedUsersList());
			return clearValuesListener;
		}

		protected EmptyFieldListener<?> getEmptyFieldListenerAddUser() {
			EmptyFieldListener fieldListener = new EmptyFieldListener();
			fieldListener.setMandatoryListFields(dto.getCoursesList(), dto.getUsersList());
			fieldListener.setTargetBtn(dto.getAddUserBtn());
			return fieldListener;
		}
	}

	@Data
	@Builder
	private static class AdmissionServiceDTO {
		private Panel rootPanel;
		private ComboBox<String> coursesList;
		private ComboBox<String> usersList;
		private Button addUserBtn;
		private ListSelect<String> addedUsersList;
		private Button saveBtn;
		private Button deleteElementsByListBtn;
		private Button clearBtn;
	}

}
