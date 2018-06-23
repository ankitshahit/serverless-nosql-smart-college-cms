package io.college.cms.core.admission.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
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
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.service.CourseResponseService;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.util.ListenerUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class PublishAdmissionView extends VerticalLayout implements View {
	private static final long serialVersionUID = 1L;
	private CourseResponseService courseResponseService;
	private List<CourseModel> models;
	private TextField fees;
	private RichTextArea additionalDetails;

	@Autowired
	public PublishAdmissionView(CourseResponseService courseResponseService) {
		this.courseResponseService = courseResponseService;
	}

	@PostConstruct
	public void paint() {
		Panel rootPanel = new Panel();
		VerticalLayout rootLayout = new VerticalLayout();
		HorizontalLayout firstLayout = new HorizontalLayout();
		VerticalLayout verticalLayoutFirstPart = new VerticalLayout();
		VerticalLayout verticalLayoutSecondPart = new VerticalLayout();
		this.fees = new TextField();
		this.additionalDetails = new RichTextArea();
		Label message = new Label();
		/*
		 * message.setCaption(
		 * "You're about to apply for course(s), once applied your application will be reviewed and a notification will be sent once a decision is taken."
		 * );
		 */ message.setCaptionAsHtml(true);

		ComboBox<String> courses = new ComboBox<String>();
		ComboBox<String> semester = new ComboBox<String>();
		DateField courseYear = new DateField();
		Button addTab = new Button();
		addComponent(rootPanel);
		setComponentAlignment(rootPanel, Alignment.MIDDLE_CENTER);
		rootPanel.setContent(rootLayout);
		rootLayout.addComponent(firstLayout);

		HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
		horizontalSplitPanel.addComponents(verticalLayoutFirstPart, verticalLayoutSecondPart);
		horizontalSplitPanel.setSplitPosition(60.0f);
		firstLayout.addComponent(horizontalSplitPanel);
		firstLayout.setSizeFull();
		setSizeFull();
		setComponentAlignment(rootPanel, Alignment.MIDDLE_RIGHT);
		message.setVisible(true);
		message.setEnabled(true);
		rootLayout.addComponent(message);
		FactoryResponse courseResponse = courseResponseService.findAllCourses(null, 0L, 0L);
		if (courseResponse == null || SummaryMessageEnum.SUCCESS != courseResponse.getSummaryMessage()) {
			Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
			notifi.setIcon(VaadinIcons.STOP);
			notifi.setCaption("Error");
			notifi.setDescription("We couldn't load course data");
			notifi.setDelayMsec(Notification.DELAY_FOREVER);
			return;
		}
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
		courses.setCaption("Apply for course");
		courses.setPlaceholder("Select course");
		courses.setRequiredIndicatorVisible(true);
		courses.setVisible(true);
		courses.setEnabled(true);
		courses.setItems(courseNames);
		courses.addSelectionListener(select -> {
			if (!ListenerUtility.isValidSourceEvent(select.getComponent(), courses)) {
				return;
			}
			semester.setVisible(CollectionUtils.isNotEmpty(select.getAllSelectedItems()));
			String courseName = select.getFirstSelectedItem().get();
			List<String> semesters = new ArrayList<>();
			models.forEach(course -> {
				if (course.getCourseName().equalsIgnoreCase(courseName)) {
					if (CollectionUtils.isEmpty(course.getSemesters())) {
						semesters.add("Sem 1");
					} else {
						semesters.addAll(course.getSemesters());
					}
				}
			});
			semester.setItems(semesters);
		});
		verticalLayoutFirstPart.addComponent(courses);
		semester.setCaption("Select semester");
		semester.setPlaceholder("Select semester");
		semester.setRequiredIndicatorVisible(true);
		semester.setVisible(true);
		semester.setEnabled(true);
		verticalLayoutFirstPart.addComponent(semester);

		this.fees.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_LARGE,
				ValoTheme.TEXTFIELD_INLINE_ICON);
		this.additionalDetails.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_LARGE,
				ValoTheme.TEXTFIELD_INLINE_ICON);

		this.additionalDetails.setCaptionAsHtml(true);
		this.additionalDetails.setCaption(
				"<p>Provide additional details that <br/>might be required or <br/>that can be used by student.</p>");
		this.fees.setCaptionAsHtml(true);
		this.fees.setCaption("Provide details about fees <br/>to be shown to student.");
		this.fees.setSizeFull();
		verticalLayoutFirstPart.addComponents(this.fees, this.additionalDetails);
		addTab.setCaption("Save & Next");

		addTab.setVisible(true);
		addTab.setEnabled(true);
		rootLayout.addComponent(addTab);
		EmptyFieldListener<String> coursesListener = new EmptyFieldListener<String>();
		coursesListener.setSourceListField(courses);
		coursesListener.setTargetBtn(addTab);
		coursesListener.setMandatoryFields(courseYear);
		coursesListener.setMandatoryListFields(courses, semester);
		courses.addValueChangeListener(coursesListener);
		EmptyFieldListener<LocalDate> courseYearListener = new EmptyFieldListener<LocalDate>();
		courseYearListener.setSourceDateField(courseYear);
		courseYearListener.setTargetBtn(addTab);
		courseYearListener.setMandatoryDateFields(courseYear);
		courseYearListener.setMandatoryFields(courseYear);
		courseYearListener.setMandatoryListFields(courses, semester);
		courseYear.addValueChangeListener(courseYearListener);
		EmptyFieldListener<String> semesterListener = new EmptyFieldListener<String>();
		semesterListener.setSourceListField(semester);
		semesterListener.setTargetBtn(addTab);
		semesterListener.setMandatoryFields(courseYear);
		semesterListener.setMandatoryListFields(courses, semester);
		semester.addValueChangeListener(semesterListener);

	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

}
