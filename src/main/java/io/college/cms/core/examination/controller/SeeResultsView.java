package io.college.cms.core.examination.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.application.Utils;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.service.CourseResponseService;
import io.college.cms.core.ui.listener.ClearValuesListener;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.listener.ShowHideListener;
import io.college.cms.core.ui.util.ListenerUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class SeeResultsView extends VerticalLayout implements View {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ComboBox<String> selectCourse;
	private ComboBox<String> selectSubject;
	private ComboBox<String> selectSem;
	private CourseResponseService courseResponseService;
	private CourseModel courseModel;

	/**
	 * @param courseResponseService
	 */
	@Autowired
	public SeeResultsView(CourseResponseService courseResponseService) {
		super();
		this.courseResponseService = courseResponseService;
	}

	@PostConstruct
	public void paint() {
		Panel panel = new Panel();
		VerticalLayout rootLayout = new VerticalLayout();
		TextField findStudent = new TextField();
		this.selectCourse = new ComboBox<String>();
		this.selectSubject = new ComboBox<String>();
		this.selectSem = new ComboBox<String>();
		Button publish = new Button();
		Button reset = new Button("Clear");
		addComponent(panel);
		setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
		panel.setContent(rootLayout);

		HorizontalLayout welcomeLabel = new HorizontalLayout();
		welcomeLabel.setSizeFull();
		Label label = new Label();
		label.setCaption("<h2>See Results</h2>");
		label.setCaptionAsHtml(true);

		findStudent.setPlaceholder("Email@Address.com");
		findStudent.setRequiredIndicatorVisible(true);
		findStudent.setVisible(true);
		findStudent.setEnabled(true);
		findStudent.setCaption("Find by email");
		findStudent.setIcon(VaadinIcons.INSTITUTION);

		welcomeLabel.addComponents(label, findStudent);
		welcomeLabel.setComponentAlignment(findStudent, Alignment.TOP_RIGHT);
		rootLayout.addComponent(welcomeLabel);

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

		rootLayout.addComponents(this.selectCourse, this.selectSem, this.selectSubject);
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
		clearValues.setMandatoryFields(findStudent);
		clearValues.setMandatoryListFields(selectCourse, selectSem, selectSubject);
		reset.addClickListener(clearValues);

		ShowHideListener<String> showHideStudentListener = new ShowHideListener<>();
		showHideStudentListener.setSourceField(findStudent);
		showHideStudentListener.setMandatoryListFields(selectCourse, selectSubject, selectSem);
		findStudent.addValueChangeListener(showHideStudentListener);

		EmptyFieldListener<String> findStudentListener = new EmptyFieldListener<String>();
		findStudentListener.setSourceField(findStudent);
		findStudentListener.setTargetBtn(publish);
		findStudentListener.setMandatoryFields(findStudent);
		findStudentListener.setMandatoryListFields(selectCourse);
		findStudent.addValueChangeListener(findStudentListener);

		EmptyFieldListener<String> selectCourseListener = new EmptyFieldListener<String>();
		selectCourseListener.setSourceListField(selectCourse);
		selectCourseListener.setTargetBtn(publish);
		selectCourseListener.setMandatoryFields(findStudent);
		selectCourseListener.setMandatoryListFields(selectCourse);

		this.selectCourse.addValueChangeListener(selectCourseListener);
		this.selectSem.setVisible(false);
		this.selectSubject.setVisible(false);
		this.selectCourse.addSelectionListener(select -> {
			if (!ListenerUtility.isValidSourceEvent(select.getComponent(), this.selectCourse)) {
				return;
			}
			this.selectSem.setVisible(this.selectCourse.getOptionalValue().isPresent());
			if (!this.selectSem.isVisible()) {
				return;
			}
			List<String> semesters = new ArrayList<>();

			String courseName = Utils.val(this.selectCourse.getOptionalValue());
			FactoryResponse fr = courseResponseService.findByCourseName(null, courseName);
			Utils.showFactoryResponseOnlyError(fr);
			if (Utils.isError(fr)) {
				return;
			}
			this.courseModel = (CourseModel) fr.getResponse();
			if (Utils.isNull(courseModel)) {
				return;
			}

			if (CollectionUtils.isEmpty(courseModel.getSemesters())) {
				semesters.add("Sem 1");
			} else {
				semesters.addAll(courseModel.getSemesters());
			}
			this.selectSem.setItems(semesters);
			// TODO: compare subjects against all semesters avaiable in cms. to
			// show an option of subjects.

		});
		this.selectSem.addSelectionListener(value -> {
			if (!ListenerUtility.isValidSourceEvent(value.getComponent(), this.selectSem)) {
				return;
			}
			List<String> subjectNames = new ArrayList<>();
			List<CourseModel.SubjectModel> subjects = null;
			this.selectSubject.setVisible(this.selectSem.getOptionalValue().isPresent());
			if (!this.selectSubject.isVisible() || Utils.isNull(this.courseModel)) {
				return;
			}
			subjects = this.courseModel.getSubjects();
			if (CollectionUtils.isEmpty(subjects)) {
				Utils.showErrorNotification("No subjects are available for the sem. Contact admin for help!");
				return;
			}

			subjects.forEach(subject -> {
				if (this.selectSem.getValue().equalsIgnoreCase(subject.getSemester())) {
					subjectNames.add(subject.getSubjectName());
				}
			});
			if (CollectionUtils.isEmpty(subjectNames)) {
				Utils.showErrorNotification("No subjects are available for the sem. Contact admin for help!");
				return;
			}
			this.selectSubject.setItems(subjectNames);
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			LOGGER.debug("request received view : {}", event);
			FactoryResponse courseResponse = courseResponseService.findAllCourses(null, 0L, 0L);

			if (courseResponse == null || SummaryMessageEnum.SUCCESS != courseResponse.getSummaryMessage()) {
				Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
				notifi.setIcon(VaadinIcons.STOP);
				notifi.setCaption("Error");
				notifi.setDescription("We couldn't load course data");
				notifi.setDelayMsec(Notification.DELAY_FOREVER);
				return;
			}
			List<CourseModel> models = (List<CourseModel>) courseResponse.getResponse();
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
			this.selectCourse.setItems(courseNames);
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
