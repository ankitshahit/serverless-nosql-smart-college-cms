package io.college.cms.core.attendance.controller;

import java.io.File;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.Utils;
import io.college.cms.core.attendance.model.AttendanceModel;
import io.college.cms.core.attendance.services.AttendanceResponseService;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.ClearValuesListener;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.listener.ShowHideListener;
import io.college.cms.core.ui.services.CoreUiService;
import io.college.cms.core.ui.util.ListenerUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class SeeAttendanceView extends VerticalLayout implements View {
	private static final long serialVersionUID = 1L;
	private ComboBox<String> selectCourse;
	private ComboBox<String> selectSubject;
	private ComboBox<String> selectSem;
	private ProgressBar progress;
	private Window mainWindow;
	private Button publish;
	private ComboBox<String> users;
	private ApplicationContext app;
	private CoreUiService uiService;
	private AttendanceResponseService attendanceResponseService;

	/**
	 * @param courseResponseService
	 */
	@Autowired
	public SeeAttendanceView(ApplicationContext app) {
		super();
		this.app = app;
		this.uiService = app.getBean(CoreUiService.class);
		this.attendanceResponseService = app.getBean(AttendanceResponseService.class);
	}

	private void successListener() {
		getUI().access(() -> {
			progress.setCaption(progress.getCaption() + " <br/>Download should be starting in few seconds.");
			progress.setVisible(true);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mainWindow.close();
		});
	}

	private void progressListener(Float value) {
		getUI().access(() -> {
			progress.setCaption("<b>Processing</b>: ( " + value + "/100)%");
			progress.setValue(value);
		});
	}

	private void errorMsgLbl(String msg) {
		getUI().access(() -> {
			Notification notifi = Notification.show(msg, Type.ERROR_MESSAGE);
			notifi.setHtmlContentAllowed(true);
			notifi.addCloseListener(close -> mainWindow.close());

			/*
			 * errorLblMainWindow.setValue(msg);
			 * errorLblMainWindow.setVisible(true);
			 */ progress.setCaption(progress.getCaption() + " <p style=color:red>Download stopped.</p>");
		});
	}

	private void downloadFile(File file) {
		getUI().access(() -> {

			Page.getCurrent().open(new FileResource(file), "_blank", true);
		});
	}

	@PostConstruct
	public void paint() {
	
		progress = new ProgressBar();
		progress.setCaption("<b>Processing</b>");
		progress.setCaptionAsHtml(true);
		mainWindow = new Window();
		mainWindow.center();
		mainWindow.setSizeFull();
		mainWindow.setResizable(false);

		mainWindow.addCloseListener(close -> {
			progress.setCaption("<b>Processing</b>");
			progress.setValue(0.0f);
		
			progress.setVisible(false);			
		});

		users = (ComboBox<String>) VaadinWrapper.builder().caption("Filter by username").placeholder("type username")
				.build().comboBox();
		VerticalLayout mainWindowLayout = new VerticalLayout();
		mainWindowLayout.addComponents( progress);
		
		mainWindowLayout.setComponentAlignment(progress, Alignment.MIDDLE_CENTER);
		mainWindowLayout.setSizeFull();
		Panel mainWindowPanel = new Panel();
		mainWindowPanel.setSizeFull();
		mainWindowPanel.setContent(mainWindowLayout);
		mainWindow.setContent(mainWindowPanel);

		Panel panel = new Panel();
		VerticalLayout rootLayout = new VerticalLayout();
		// TextField findStudent = new TextField();
		this.selectCourse = new ComboBox<String>();
		this.selectSubject = new ComboBox<String>();
		this.selectSem = new ComboBox<String>();
		publish = new Button();
		Button reset = new Button("Clear");
		addComponent(panel);
		setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
		panel.setContent(rootLayout);

		HorizontalLayout welcomeLabel = new HorizontalLayout();
		welcomeLabel.setSizeFull();
		Label label = new Label();
		label.setCaption("<h1><center>View Results</center></h1>");
		label.setValue(
				"Filter & download report in PDF/Excel format, for additional configuration view documentation.");
		label.setCaptionAsHtml(true);

		/*
		 * findStudent.setPlaceholder("Email@Address.com");
		 * findStudent.setRequiredIndicatorVisible(true);
		 * findStudent.setVisible(true); findStudent.setEnabled(true);
		 * findStudent.setCaption("Find by email");
		 * findStudent.setIcon(VaadinIcons.INSTITUTION);
		 * 
		 * welcomeLabel.addComponents(label, findStudent);
		 * welcomeLabel.setComponentAlignment(findStudent, Alignment.TOP_RIGHT);
		 */
		// rootLayout.addComponent(welcomeLabel);

		this.selectCourse.setCaption("Select course to view results for: ");
		this.selectCourse.setPlaceholder("Type starting letter of course name");
		this.selectCourse.setRequiredIndicatorVisible(true);
		this.selectCourse.setVisible(true);
		this.selectCourse.setEnabled(true);
		this.selectCourse.setSizeFull();
		this.selectCourse.addStyleNames(ValoTheme.COMBOBOX_LARGE);

		this.selectSubject.setCaption("Select Subject: ");
		this.selectSubject.setPlaceholder("Select by entering subject name");
		this.selectSubject.setVisible(true);
		this.selectSubject.setEnabled(true);
		this.selectSubject.setSizeFull();
		this.selectSubject.addStyleNames(ValoTheme.COMBOBOX_LARGE);

		this.selectSem.setCaption("Select Semester: ");
		this.selectSem.setPlaceholder("Select by entering semester");
		this.selectSem.setVisible(true);
		this.selectSem.setEnabled(true);
		this.selectSem.addStyleNames(ValoTheme.COMBOBOX_LARGE);
		this.selectSem.setSizeFull();

		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(new VerticalLayout(new Label("Filter by subject"),
				this.selectCourse, this.selectSem, this.selectSubject),
				new VerticalLayout(new Label("Filter by student"), users));
		splitPanel.setSplitPosition(62.0f);

		rootLayout.addComponents(splitPanel);
		reset.setStyleName(ValoTheme.BUTTON_DANGER);
		rootLayout.addComponent(reset);

		publish.setCaption("View");
		publish.setVisible(true);
		publish.setStyleName(ValoTheme.BUTTON_PRIMARY);
		publish.setEnabled(false);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.addComponents(reset, publish);

		rootLayout.addComponent(btnLayout);
		rootLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_RIGHT);

		panel.setWidth("80%");

		ClearValuesListener<String> clearValues = new ClearValuesListener<>();
		clearValues.setMandatoryListFields(selectCourse, selectSem, selectSubject, users);
		reset.addClickListener(clearValues);

		ShowHideListener<String> showHideStudentListener = new ShowHideListener<>();
		showHideStudentListener.setSourceListField(users);
		showHideStudentListener.setMandatoryListFields(selectCourse, selectSubject, selectSem);
		users.addValueChangeListener(showHideStudentListener);

		ShowHideListener<String> showHideCourseListener = new ShowHideListener<>();
		showHideStudentListener.setSourceListField(selectCourse);
		showHideStudentListener.setMandatoryListFields(users);
		selectCourse.addValueChangeListener(showHideCourseListener);
		EmptyFieldListener<String> findStudentListener = new EmptyFieldListener<String>();
		findStudentListener.setSourceListField(users);
		findStudentListener.setTargetBtn(publish);
		findStudentListener.setMandatoryListFields(users);
		users.addValueChangeListener(findStudentListener);

		EmptyFieldListener<String> selectCourseListener = new EmptyFieldListener<String>();
		selectCourseListener.setSourceListField(selectCourse);
		selectCourseListener.setTargetBtn(publish);
		selectCourseListener.setMandatoryListFields(selectCourse);
		selectCourse.addValueChangeListener(selectCourseListener);

		this.selectCourse.addValueChangeListener(selectCourseListener);
		this.selectSem.setVisible(false);
		this.selectSubject.setVisible(false);
		this.selectCourse.addSelectionListener(select -> {
			if (!ListenerUtility.isValidSourceEvent(select.getComponent(), this.selectCourse)) {
				return;
			}
			this.selectSem.setVisible(this.selectCourse.getOptionalValue().isPresent());
			this.users.setVisible(!this.selectCourse.getOptionalValue().isPresent());
			if (!this.selectCourse.getOptionalValue().isPresent()) {
				return;
			}
			uiService.setItemsSemester(selectSem, Utils.val(selectCourse.getOptionalValue()));

			// TODO: compare subjects against all semesters avaiable in cms. to
			// show an option of subjects.

		});
		this.selectSem.addSelectionListener(value -> {
			if (!ListenerUtility.isValidSourceEvent(value.getComponent(), this.selectSem)) {
				return;
			}
			uiService.setItemsSubject(selectSubject, Utils.val(selectCourse.getOptionalValue()),
					Utils.val(selectSem.getOptionalValue()));
			selectSubject.setVisible(this.selectSem.getOptionalValue().isPresent());
		});

		publish.addClickListener(click -> {
			progress.setVisible(true);
			getUI().addWindow(mainWindow);
			AttendanceModel.AttendanceModelBuilder builder = AttendanceModel.builder();
			if (users.getOptionalValue().isPresent()) {
				builder.username(Utils.val(users.getOptionalValue()));
			} else {
				builder.courseName(Utils.val(selectCourse.getOptionalValue()))
						.semester(Utils.val(selectSem.getOptionalValue()))
						.subjectName(Utils.val(selectSubject.getOptionalValue()));
			}
			attendanceResponseService.download(this::downloadFile, this::progressListener, this::errorMsgLbl,
					this::successListener, builder.build());
		});
		users.addValueChangeListener(value -> this.selectCourse.setVisible(!users.getOptionalValue().isPresent()));
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			LOGGER.debug("request received view : {}", event);
			uiService.setItemsCourseNames(selectCourse);
			uiService.setItemsUser(users);
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
		ClearValuesListener<String> clearValues = new ClearValuesListener<>();
		clearValues.setMandatoryListFields(selectCourse, selectSem, selectSubject);
		clearValues.buttonClick(null);
	}
}
