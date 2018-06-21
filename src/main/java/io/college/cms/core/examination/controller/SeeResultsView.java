package io.college.cms.core.examination.controller;

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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.ui.listener.ClearValuesListener;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.listener.ShowHideListener;
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

		selectCourse.setCaption("Select course to view results for: ");
		selectCourse.setPlaceholder("Type starting letter of course name");
		selectCourse.setRequiredIndicatorVisible(true);
		selectCourse.setVisible(true);
		selectCourse.setEnabled(true);
		selectCourse.setSizeFull();
		rootLayout.addComponent(selectCourse);

		selectSubject.setCaption("Select Subject: ");
		selectSubject.setPlaceholder("Select by entering subject name");
		selectSubject.setVisible(true);
		selectSubject.setEnabled(true);
		selectSubject.setSizeFull();
		rootLayout.addComponent(selectSubject);

		selectSem.setCaption("Select Semester: ");
		selectSem.setPlaceholder("Select by entering semester");
		selectSem.setVisible(true);
		selectSem.setEnabled(true);
		selectSem.setSizeFull();

		rootLayout.addComponent(selectSem);

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

		panel.setWidth("50%");
		ClearValuesListener<String> clearValues = new ClearValuesListener<>();
		clearValues.setMandatoryFields(findStudent);
		clearValues.setMandatoryListFields(selectCourse, selectSubject, selectSem);
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
		selectCourse.addValueChangeListener(selectCourseListener);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			LOGGER.debug("request received view : {}", event);
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
