package io.college.cms.core.announcement.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
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
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class PublishAnnouncementView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private CourseResponseService courseResponseService;
	private List<CourseModel> models;

	@Autowired
	public PublishAnnouncementView(CourseResponseService courseResponseService) {
		this.courseResponseService = courseResponseService;
	}

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
		FactoryResponse fr = courseResponseService.findAllCourses(null, 0L, 0L);
		if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
			Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
			notifi.setIcon(VaadinIcons.STOP);
			notifi.setCaption("Error");
			notifi.setDescription("We couldn't load course data");
			notifi.setDelayMsec(Notification.DELAY_FOREVER);
			return;
		}
		models = (List<CourseModel>) fr.getResponse();
		List<String> courseNames = new ArrayList<>();
		models.forEach(course -> {
			courseNames.add(course.getCourseName());
		});
		selectCourse.setItems(courseNames);
		selectCourse.setCaption("Select course to send announcement to:");
		selectCourse.setPlaceholder("Type starting letter of course name");
		selectCourse.setRequiredIndicatorVisible(true);
		selectCourse.setVisible(true);
		selectCourse.setEnabled(true);
		// selectCourse.setItems(models);
		rootLayout.addComponent(selectCourse);
		rootLayout.addComponent(horizontalSubAndDate);
		subject.setCaption("Subject");
		subject.setPlaceholder("Write 100 characters at max");
		subject.setRequiredIndicatorVisible(true);
		subject.setVisible(true);
		subject.setEnabled(true);
		subject.setMaxLength(100);
		subject.addStyleNames(ValoTheme.TEXTFIELD_BORDERLESS);
		subject.setSizeFull();
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

		EmptyFieldListener<String> subjectListener = new EmptyFieldListener<String>();
		subjectListener.setSourceField(subject);
		subjectListener.setTargetBtn(publish);
		subjectListener.setMandatoryFields(subject, announcementDescription);

		subjectListener.setMandatoryDateFields(scheduledDate);
		subject.addValueChangeListener(subjectListener);

		EmptyFieldListener<LocalDate> scheduledDateListener = new EmptyFieldListener<LocalDate>();
		scheduledDateListener.setSourceDateField(scheduledDate);
		scheduledDateListener.setTargetBtn(publish);
		scheduledDateListener.setMandatoryDateFields(scheduledDate);
		scheduledDateListener.setMandatoryFields(subject, announcementDescription);
		scheduledDate.addValueChangeListener(scheduledDateListener);

		EmptyFieldListener<String> announcementDescriptionListener = new EmptyFieldListener<String>();
		announcementDescriptionListener.setSourceField(announcementDescription);
		announcementDescriptionListener.setTargetBtn(publish);
		announcementDescriptionListener.setMandatoryDateFields(scheduledDate);
		announcementDescriptionListener.setMandatoryFields(subject, announcementDescription);
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
