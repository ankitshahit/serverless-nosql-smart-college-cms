package io.college.cms.core.announcement.ui;

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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import io.college.cms.core.ui.listener.EmptyFieldListener;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class PublishAnnouncementView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PostConstruct
	public void paint() {
		Panel panel = new Panel();
		VerticalLayout rootLayout = new VerticalLayout();
		CheckBox announceToAll = new CheckBox();
		ComboBox<String> selectCourse = new ComboBox<String>();
		HorizontalLayout horizontalSubAndDate = new HorizontalLayout();
		TextField subject = new TextField();
		DateField scheduledDate = new DateField();
		RichTextArea announcementDescription = new RichTextArea();
		Button publish = new Button();
		addComponent(panel);
		setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
		panel.setContent(rootLayout);
		announceToAll.setCaption("Send announcement to all?");
		announceToAll.setVisible(true);
		announceToAll.setEnabled(true);
		rootLayout.addComponent(announceToAll);
		selectCourse.setCaption("Select course to send announcement to:");
		selectCourse.setPlaceholder("Type starting letter of course name");
		selectCourse.setRequiredIndicatorVisible(true);
		selectCourse.setVisible(true);
		selectCourse.setEnabled(true);
		rootLayout.addComponent(selectCourse);
		rootLayout.addComponent(horizontalSubAndDate);
		subject.setCaption("Heading for announcement, keep it simple!");
		subject.setPlaceholder("Write 100 characters at max");
		subject.setRequiredIndicatorVisible(true);
		subject.setVisible(true);
		subject.setEnabled(true);
		subject.setMaxLength(100);
		horizontalSubAndDate.addComponent(subject);
		scheduledDate.setCaption("Schedule date for announcement!");
		scheduledDate.setPlaceholder("Select as today's date to publish immediately");
		scheduledDate.setRequiredIndicatorVisible(true);
		scheduledDate.setVisible(true);
		scheduledDate.setEnabled(true);
		horizontalSubAndDate.addComponent(scheduledDate);
		announcementDescription.setCaption("Description for announcement, keep it simple!");
		announcementDescription.setRequiredIndicatorVisible(true);
		announcementDescription.setVisible(true);
		announcementDescription.setEnabled(true);
		rootLayout.addComponent(announcementDescription);
		publish.setCaption("Publish");
		publish.setVisible(true);
		publish.setEnabled(false);
		rootLayout.addComponent(publish);
		rootLayout.setComponentAlignment(publish, Alignment.BOTTOM_RIGHT);
		EmptyFieldListener<String> selectCourseListener = new EmptyFieldListener<String>();
		selectCourseListener.setSourceListField(selectCourse);
		selectCourseListener.setTargetBtn(publish);
		selectCourseListener.setMandatoryFields(subject, scheduledDate, announcementDescription);
		selectCourseListener.setMandatoryListFields(selectCourse);
		selectCourse.addValueChangeListener(selectCourseListener);
		EmptyFieldListener<String> subjectListener = new EmptyFieldListener<String>();
		subjectListener.setSourceField(subject);
		subjectListener.setTargetBtn(publish);
		subjectListener.setMandatoryFields(subject, scheduledDate, announcementDescription);
		subjectListener.setMandatoryListFields(selectCourse);
		subject.addValueChangeListener(subjectListener);
		EmptyFieldListener<LocalDate> scheduledDateListener = new EmptyFieldListener<LocalDate>();
		scheduledDateListener.setSourceDateField(scheduledDate);
		scheduledDateListener.setTargetBtn(publish);
		scheduledDateListener.setMandatoryDateFields(scheduledDate);
		scheduledDateListener.setMandatoryFields(subject, scheduledDate, announcementDescription);
		scheduledDateListener.setMandatoryListFields(selectCourse);
		scheduledDate.addValueChangeListener(scheduledDateListener);
		EmptyFieldListener<String> announcementDescriptionListener = new EmptyFieldListener<String>();
		announcementDescriptionListener.setSourceField(announcementDescription);
		announcementDescriptionListener.setTargetBtn(publish);
		announcementDescriptionListener.setMandatoryFields(subject, scheduledDate, announcementDescription);
		announcementDescriptionListener.setMandatoryListFields(selectCourse);
		announcementDescription.addValueChangeListener(announcementDescriptionListener);

	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		LOGGER.debug("Entered");
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

}
