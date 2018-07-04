package io.college.cms.core.attendance.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.Utils;
import io.college.cms.core.attendance.model.AttendanceModel;
import io.college.cms.core.attendance.services.AttendanceResponseService;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.model.ViewConstants;
import io.college.cms.core.ui.services.CoreUiService;
import io.college.cms.core.ui.util.ListenerUtility;
import io.college.cms.core.user.service.SecurityService;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class TagAttendanceView extends VerticalLayout implements View, Upload.Receiver, Upload.SucceededListener {
	private static final long serialVersionUID = 1L;
	private Panel rootPanel;
	private VerticalLayout rootLayout;
	private HorizontalSplitPanel splitPanel;
	private VerticalLayout firstLayout;
	private ComboBox<String> courseNamesCb;
	private ComboBox<String> semesterCb;
	private ComboBox<String> subjectNamesCb;
	private DateField attendance;
	private Label totalStudentLbl;
	private VerticalLayout secondLayout;
	private ComboBox<String> usersCb;
	private Button saveBtn;
	private Upload upload;
	private ListSelect<String> studentsUsernameList;
	private Button removeStudentUsernameBtn;
	private CoreUiService uiService;
	private SecurityService securityService;
	private AttendanceResponseService attendanceResponseService;
	private ApplicationContext app;
	private Binder<AttendanceModel> binder;
	private Window mainWindow;
	private ProgressBar progress;

	/**
	 * @param uiService
	 * @param securityService
	 * @param attendanceResponseService
	 * @param app
	 */
	@Autowired
	public TagAttendanceView(CoreUiService uiService, SecurityService securityService,
			AttendanceResponseService attendanceResponseService, ApplicationContext app) {
		super();
		this.uiService = uiService;
		this.securityService = securityService;
		this.attendanceResponseService = attendanceResponseService;
		this.app = app;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		uiService.setItemsCourseNames(courseNamesCb);
		uiService.setItemsUser(usersCb);
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

	private void successListener() {
		getUI().access(() -> {
			progress.setVisible(false);
			mainWindow.close();
		});
	}

	private void listSelectUpdate(List<String> items) {
		getUI().access(() -> {
			studentsUsernameList.setItems(items);
			totalStudentLbl.setValue(String.valueOf(CollectionUtils.size(items)));
		});
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		attendanceResponseService.tag(new FileResource(new File(event.getFilename())), this::listSelectUpdate,
				this::successListener);
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		try {
			return new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void paint() {
		mainWindow = new Window();
		progress = new ProgressBar();
		binder = new Binder<>();
		this.rootPanel = new Panel();
		this.rootLayout = new VerticalLayout();
		this.splitPanel = new HorizontalSplitPanel();
		this.firstLayout = new VerticalLayout();
		this.courseNamesCb = new ComboBox<String>();
		this.semesterCb = new ComboBox<String>();
		this.subjectNamesCb = new ComboBox<String>();
		this.attendance = new DateField();
		this.secondLayout = new VerticalLayout();
		this.usersCb = new ComboBox<String>();
		this.totalStudentLbl = new Label();
		this.saveBtn = new Button();
		this.upload = new Upload();
		this.studentsUsernameList = new ListSelect<>("Added students");
		this.studentsUsernameList.setSizeFull();
		this.studentsUsernameList.setItems("");
		progress.setVisible(false);
		progress.setCaption("<b>Processing</b>");
		progress.setCaptionAsHtml(true);
		this.courseNamesCb = (ComboBox<String>) VaadinWrapper.builder().caption("Course name")
				.placeholder("search by course").required(true).visible(true).enabled(true).build().comboBox();
		this.semesterCb = (ComboBox<String>) VaadinWrapper.builder().caption("Semester")
				.placeholder("search by semester").required(true).visible(true).enabled(true).build().comboBox();
		this.subjectNamesCb = (ComboBox<String>) VaadinWrapper.builder().caption("subject name")
				.placeholder("search by subject").required(true).visible(true).enabled(true).build().comboBox();

		this.attendance = VaadinWrapper.builder().caption("Attendance date").required(true).visible(true).enabled(true)
				.build().dateField();
		this.usersCb = (ComboBox<String>) VaadinWrapper.builder().caption("Missing student?")
				.placeholder("search by name").required(false).visible(true).enabled(true).build().comboBox();

		this.totalStudentLbl = VaadinWrapper.builder().caption("Total students count").required(true).visible(true)
				.enabled(true).build().label();

		this.saveBtn = VaadinWrapper.builder().caption("Save settings").required(true).visible(true).enabled(false)
				.build().button();

		this.removeStudentUsernameBtn = new Button("Delete");
		this.removeStudentUsernameBtn.setVisible(false);
		this.removeStudentUsernameBtn.addStyleName(ValoTheme.BUTTON_DANGER);

		this.upload.setCaption("Upload image");
		this.upload.setCaptionAsHtml(true);
		this.upload.setButtonCaption("select file ...");
		this.upload.addStartedListener(listener -> {
			mainWindow.setResizable(false);
			mainWindow.center();
			VerticalLayout layout = new VerticalLayout();
			progress.setVisible(true);
			progress.setIndeterminate(true);
			layout.setSizeFull();
			layout.addComponents(progress);
			layout.setComponentAlignment(progress, Alignment.MIDDLE_CENTER);
			mainWindow.setContent(layout);
			mainWindow.setSizeFull();
			getUI().addWindow(mainWindow);

		});
		this.upload.setReceiver(this);
		this.upload.addSucceededListener(this);
		this.upload.setImmediateMode(true);
		this.upload.setSizeFull();
		this.saveBtn.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
		this.saveBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
		this.saveBtn.setCaption("Save attendance");
		firstLayout.addComponent(courseNamesCb);
		firstLayout.addComponent(semesterCb);
		firstLayout.addComponent(subjectNamesCb);
		firstLayout.addComponent(attendance);
		Panel firstPanel = new Panel(firstLayout);
		firstPanel.setSizeFull();
		splitPanel.addComponent(firstPanel);
		splitPanel.addComponent(new Panel(secondLayout));
		splitPanel.setSplitPosition(30.0f);

		secondLayout.addComponents(this.upload, usersCb, totalStudentLbl, this.studentsUsernameList,
				this.removeStudentUsernameBtn);

		rootLayout.addComponents(splitPanel, saveBtn);
		rootLayout.setComponentAlignment(this.saveBtn, Alignment.BOTTOM_RIGHT);
		rootPanel.setContent(rootLayout);
		addComponent(rootPanel);

		EmptyFieldListener<String> courseNamesCbListener = new EmptyFieldListener<String>();
		courseNamesCbListener.setSourceListField(courseNamesCb);
		courseNamesCbListener.setTargetBtn(saveBtn);
		courseNamesCbListener.setMandatoryDateFields(attendance);
		courseNamesCbListener.setMandatoryListFields(courseNamesCb, semesterCb, subjectNamesCb);
		courseNamesCb.addValueChangeListener(courseNamesCbListener);
		EmptyFieldListener<String> subjectNamesCbListener = new EmptyFieldListener<String>();
		subjectNamesCbListener.setSourceListField(subjectNamesCb);
		subjectNamesCbListener.setTargetBtn(saveBtn);
		subjectNamesCbListener.setMandatoryDateFields(attendance);
		subjectNamesCbListener.setMandatoryListFields(courseNamesCb, semesterCb, subjectNamesCb);
		subjectNamesCb.addValueChangeListener(subjectNamesCbListener);
		EmptyFieldListener<String> semesterCbListener = new EmptyFieldListener<String>();
		semesterCbListener.setSourceListField(semesterCb);
		semesterCbListener.setTargetBtn(saveBtn);
		semesterCbListener.setMandatoryDateFields(attendance);
		semesterCbListener.setMandatoryListFields(courseNamesCb, semesterCb, subjectNamesCb);
		semesterCb.addValueChangeListener(semesterCbListener);

		EmptyFieldListener<LocalDate> attendanceListener = new EmptyFieldListener<LocalDate>();
		attendanceListener.setSourceDateField(attendance);
		attendanceListener.setTargetBtn(saveBtn);
		attendanceListener.setMandatoryDateFields(attendance);
		attendanceListener.setMandatoryListFields(courseNamesCb, semesterCb, subjectNamesCb);
		attendance.addValueChangeListener(attendanceListener);
		binder.bind(courseNamesCb, AttendanceModel::getCourseName, AttendanceModel::setCourseName);
		binder.bind(semesterCb, AttendanceModel::getSemester, AttendanceModel::setSemester);
		binder.bind(subjectNamesCb, AttendanceModel::getSubjectName, AttendanceModel::setSubjectName);
		binder.bind(attendance, AttendanceModel::getAttendanceDate, AttendanceModel::setAttendanceDate);

		this.saveBtn.addClickListener(click -> {
			ListDataProvider<String> dataProvider = (ListDataProvider<String>) studentsUsernameList.getDataProvider();
			AttendanceModel model = AttendanceModel.builder().build();
			try {
				binder.writeBean(model);
				List<String> users = new ArrayList<>();
				users.addAll(dataProvider.getItems());
				model.setUsers(users);
				model.setActionBy(securityService.getPrincipal());
				FactoryResponse fr = attendanceResponseService.saveAttendance(model);
				Notification notifi = Utils.showFactoryResponseMsg(fr);
				notifi.addCloseListener(close -> getUI().getNavigator().navigateTo(ViewConstants.VIEW_ATTENDANCE));
			} catch (ValidationException e) {
				Utils.showErrorNotification("Unable to save");
			}
		});
		this.usersCb.addSelectionListener(select -> {
			if (!this.usersCb.getOptionalValue().isPresent()) {
				return;
			}
			ListDataProvider<String> dataProvider = (ListDataProvider<String>) studentsUsernameList.getDataProvider();
			Collection<String> users = dataProvider.getItems();
			if (CollectionUtils.isNotEmpty(users) && users.contains(usersCb.getOptionalValue().get())) {
				Notification.show("Student already tagged.", Type.WARNING_MESSAGE);
				return;
			}
			users.add(usersCb.getOptionalValue().get());
			studentsUsernameList.setItems(users);
			totalStudentLbl.setValue(String.valueOf(CollectionUtils.size(users)));
		});
		this.courseNamesCb.addSelectionListener(select -> {
			if (!courseNamesCb.getSelectedItem().isPresent()) {
				return;
			}
			this.uiService.setItemsSemester(semesterCb, courseNamesCb.getOptionalValue().get());
		});
		this.semesterCb.addSelectionListener(select -> {
			if (!courseNamesCb.getSelectedItem().isPresent() && !semesterCb.getSelectedItem().isPresent()) {
				return;
			}
			this.uiService.setItemsSubject(subjectNamesCb, courseNamesCb.getOptionalValue().get(),
					semesterCb.getOptionalValue().get());
		});
		this.studentsUsernameList.addSelectionListener(value -> {
			if (!ListenerUtility.isValidSourceEvent(value.getComponent(), this.studentsUsernameList)) {
				return;
			}
			this.removeStudentUsernameBtn.setVisible(CollectionUtils.isNotEmpty(value.getAllSelectedItems()));
		});
		this.removeStudentUsernameBtn.addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), this.removeStudentUsernameBtn)) {
				return;
			}
			if (CollectionUtils.isEmpty(this.studentsUsernameList.getSelectedItems())) {
				this.removeStudentUsernameBtn.setVisible(false);
			}
			ListDataProvider<String> dataProvider = (ListDataProvider<String>) studentsUsernameList.getDataProvider();
			Collection<String> users = dataProvider.getItems();
			if (CollectionUtils.isEmpty(users)) {
				return;
			}
			users.removeAll(studentsUsernameList.getSelectedItems());
			studentsUsernameList.setItems(users);
			// TODO: NEEd to get all of the items made available in listselect

		});
	}

}
