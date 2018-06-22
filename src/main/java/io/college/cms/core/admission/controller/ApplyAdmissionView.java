package io.college.cms.core.admission.controller;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import io.college.cms.core.ui.listener.EmptyFieldListener;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ApplyAdmissionView extends VerticalLayout implements View {
	private static final long serialVersionUID = 1L;

	@PostConstruct
	public void paint() {
		Panel panelStep2 = new Panel();
		VerticalLayout rootLayoutStep2 = new VerticalLayout();
		HorizontalLayout horizontalLayout1 = new HorizontalLayout();
		VerticalLayout rootLayoutStep2FirstPart = new VerticalLayout();
		VerticalLayout rootLayoutStep2SecondPart = new VerticalLayout();
		Label message = new Label();
		message.setCaption(
				"You're about to apply for course(s), once applied your application will be reviewed and a notification will be sent once a decision is taken.");
		message.setCaptionAsHtml(true);
		ComboBox<String> courses = new ComboBox<String>();
		ComboBox<String> semester = new ComboBox<String>();
		DateField courseYear = new DateField();
		Button addTab = new Button();
		addComponent(panelStep2);
		setComponentAlignment(panelStep2, Alignment.MIDDLE_CENTER);
		panelStep2.setContent(rootLayoutStep2);
		rootLayoutStep2.addComponent(horizontalLayout1);
		horizontalLayout1.addComponent(rootLayoutStep2FirstPart);
		horizontalLayout1.addComponent(rootLayoutStep2SecondPart);
		message.setVisible(true);
		message.setEnabled(true);
		rootLayoutStep2.addComponent(message);
		courses.setCaption("Apply for course");
		courses.setPlaceholder("Select course");
		courses.setRequiredIndicatorVisible(true);
		courses.setVisible(true);
		courses.setEnabled(true);
		rootLayoutStep2FirstPart.addComponent(courses);
		semester.setCaption("Select semester");
		semester.setPlaceholder("Select semester");
		semester.setRequiredIndicatorVisible(true);
		semester.setVisible(true);
		semester.setEnabled(true);
		rootLayoutStep2FirstPart.addComponent(semester);
		courseYear.setPlaceholder("Course year");
		courseYear.setRequiredIndicatorVisible(true);
		courseYear.setVisible(true);
		courseYear.setEnabled(true);
		rootLayoutStep2FirstPart.addComponent(courseYear);
		addTab.setCaption("Save & Next");

		addTab.setVisible(true);
		addTab.setEnabled(true);
		rootLayoutStep2.addComponent(addTab);
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
