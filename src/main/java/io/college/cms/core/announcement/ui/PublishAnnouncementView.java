package io.college.cms.core.announcement.ui;

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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
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
public class PublishAnnouncementView extends VerticalLayout implements View {
	private static final float SPLITTER_POSITION = 65.0F;
	private static final long serialVersionUID = 1L;
	private CourseResponseService courseResponseService;
	private List<CourseModel> models;
	private ComboBox<String> selectCourse;

	@Autowired
	public PublishAnnouncementView(CourseResponseService courseResponseService) {
		this.courseResponseService = courseResponseService;
	}

	@PostConstruct
	public void paint() {
		Panel panel = new Panel();
		VerticalLayout rootLayout = new VerticalLayout();
		CheckBox announceToAll = new CheckBox();
		selectCourse = new ComboBox<String>();
		TextField subject = new TextField();
		DateField scheduledDate = new DateField();
		RichTextArea announcementDescription = new RichTextArea();
		Button publish = new Button();

		announceToAll.setCaption("Send announcement to all?");
		announceToAll.setVisible(true);
		announceToAll.setEnabled(true);

		this.selectCourse.setCaption("Select course to send announcement to:");
		this.selectCourse.setPlaceholder("Type starting letter of course name");
		this.selectCourse.setRequiredIndicatorVisible(true);
		this.selectCourse.setVisible(true);
		this.selectCourse.setEnabled(true);
		this.selectCourse.setSizeFull();
		this.selectCourse.addStyleNames(ValoTheme.COMBOBOX_LARGE);
		// selectCourse.setItems(models);

		subject.setCaption("Title");
		subject.setPlaceholder("Write 100 characters at max");
		subject.setRequiredIndicatorVisible(true);
		subject.setVisible(true);
		subject.setEnabled(true);
		subject.setMaxLength(100);
		subject.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_LARGE,
				ValoTheme.TEXTFIELD_INLINE_ICON);
		subject.setSizeFull();

		scheduledDate.setCaption("Schedule date for announcement!");
		scheduledDate.setValue(LocalDate.now());
		scheduledDate.setRequiredIndicatorVisible(true);
		scheduledDate.setVisible(true);
		scheduledDate.setEnabled(true);
		scheduledDate.addStyleNames(ValoTheme.DATEFIELD_ALIGN_CENTER, ValoTheme.DATEFIELD_LARGE);
		scheduledDate.setSizeFull();

		announcementDescription.setCaption("Description for announcement, keep it simple!");
		announcementDescription.setRequiredIndicatorVisible(true);
		announcementDescription.setVisible(true);
		announcementDescription.setEnabled(true);
		announcementDescription.setSizeFull();
		publish.setCaption("Publish");
		publish.setVisible(true);
		publish.setEnabled(false);
		publish.addStyleNames(ValoTheme.BUTTON_PRIMARY);
		// attaching and positioning on ui page
		HorizontalSplitPanel pageSplit = new HorizontalSplitPanel();
		VerticalLayout firstPageLayout = new VerticalLayout();
		VerticalLayout secondPageLayout = new VerticalLayout();
		firstPageLayout.addComponents(subject, announcementDescription);
		Label typeMsg = new Label("");
		typeMsg.setValue("OR");
		typeMsg.setCaptionAsHtml(true);
		secondPageLayout.addComponents(announceToAll, typeMsg, this.selectCourse, scheduledDate);
		pageSplit.setSplitPosition(SPLITTER_POSITION);
		pageSplit.addComponents(firstPageLayout, secondPageLayout);
		rootLayout.addComponents(pageSplit, publish);
		rootLayout.setComponentAlignment(publish, Alignment.BOTTOM_RIGHT);
		panel.setContent(rootLayout);
		addComponent(panel);
		// attaching event listeners as per vaadin's documentation to listen for
		// snippets that are to be executed on event trigger
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
		this.selectCourse.addValueChangeListener(value -> {
			if (!ListenerUtility.isValidSourceEvent(value.getComponent(), this.selectCourse)) {
				return;
			}
			announceToAll.setEnabled(!this.selectCourse.getOptionalValue().isPresent());
		});
		announceToAll.addValueChangeListener(value -> {
			if (!ListenerUtility.isValidSourceEvent(value.getComponent(), announceToAll)) {
				return;
			}
			if (announceToAll.getValue()) {
				selectCourse.clear();
				selectCourse.setEnabled(false);
			} else {
				selectCourse.setEnabled(true);
			}
		});
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
			this.models = (List<CourseModel>) courseResponse.getResponse();
			if (CollectionUtils.isEmpty(this.models)) {
				Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
				notifi.setIcon(VaadinIcons.STOP);
				notifi.setCaption("Error");
				notifi.setDescription("We couldn't load course data");
				notifi.setDelayMsec(Notification.DELAY_FOREVER);
				return;
			}
			List<String> courseNames = new ArrayList<>();
			this.models.forEach(course -> {
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
	}

}
