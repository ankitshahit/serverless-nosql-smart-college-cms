package io.college.cms.core.examination.controller;

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
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import io.college.cms.core.ui.listener.EmptyFieldListener;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class SeeResultsView extends VerticalLayout implements View {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PostConstruct
	public void paint() {
		Panel panel = new Panel();
		VerticalLayout rootLayout = new VerticalLayout();
		TextField findStudent = new TextField();
		ComboBox<String> selectCourse = new ComboBox<String>();
		ComboBox<String> selectSubject = new ComboBox<String>();
		ComboBox<String> selectSem = new ComboBox<String>();
		Button publish = new Button();
		addComponent(panel);
		setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
		panel.setContent(rootLayout);
		findStudent.setCaption("Provide with student information");
		findStudent.setPlaceholder("Student email id");
		findStudent.setRequiredIndicatorVisible(true);
		findStudent.setVisible(true);
		findStudent.setEnabled(true);
		rootLayout.addComponent(findStudent);
		selectCourse.setCaption("Select course to view results for: ");
		selectCourse.setPlaceholder("Type starting letter of course name");
		selectCourse.setRequiredIndicatorVisible(true);
		selectCourse.setVisible(true);
		selectCourse.setEnabled(true);
		rootLayout.addComponent(selectCourse);
		selectSubject.setCaption("Select Subject: ");
		selectSubject.setPlaceholder("Select by entering subject name");
		selectSubject.setVisible(true);
		selectSubject.setEnabled(true);
		rootLayout.addComponent(selectSubject);
		selectSem.setCaption("Select Semester: ");
		selectSem.setPlaceholder("Select by entering semester");
		selectSem.setVisible(true);
		selectSem.setEnabled(true);
		rootLayout.addComponent(selectSem);
		publish.setCaption("Publish");
		publish.setVisible(true);
		publish.setEnabled(false);
		rootLayout.addComponent(publish);
		rootLayout.setComponentAlignment(publish, Alignment.BOTTOM_RIGHT);
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
		selectCourse.addValueChangeListener(selectCourseListener);
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
