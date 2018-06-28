package io.college.cms.core.announcement.ui;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
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

import io.college.cms.core.announcement.model.AnnouncementModel;
import io.college.cms.core.announcement.services.AnnouncementResponseService;
import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.application.Utils;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.model.ViewConstants;
import io.college.cms.core.ui.services.CoreUiService;
import io.college.cms.core.ui.util.ListenerUtility;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class PublishAnnouncementView extends VerticalLayout implements View {
	private static final float SPLITTER_POSITION = 65.0F;
	private static final long serialVersionUID = 1L;
	private Binder<AnnouncementModel> binder;
	private ComboBox<String> selectCourse;
	private CheckBox announceToAll;
	private TextField subject;
	private DateField scheduledDate;
	private RichTextArea announcementDescription;
	private CoreUiService uiService;
	private AnnouncementResponseService announcementResponseService;
	@Setter
	private AnnouncementModel announcementModel;

	@Autowired
	public PublishAnnouncementView(CoreUiService uiService, AnnouncementResponseService announcementResponseService) {
		super();
		this.uiService = uiService;
		this.announcementResponseService = announcementResponseService;
	}

	@PostConstruct
	public void paint() {
		Panel panel = new Panel();
		VerticalLayout rootLayout = new VerticalLayout();
		this.announceToAll = new CheckBox();
		this.selectCourse = new ComboBox<String>();
		this.subject = new TextField();
		this.scheduledDate = new DateField();
		this.announcementDescription = new RichTextArea();
		Button publish = new Button();

		this.announceToAll.setCaption("Send announcement to all?");
		this.announceToAll.setVisible(true);
		this.announceToAll.setEnabled(true);

		this.selectCourse.setCaption("Select course to send announcement to:");
		this.selectCourse.setPlaceholder("Type starting letter of course name");
		this.selectCourse.setRequiredIndicatorVisible(true);
		this.selectCourse.setVisible(true);
		this.selectCourse.setEnabled(true);
		this.selectCourse.setSizeFull();
		this.selectCourse.addStyleNames(ValoTheme.COMBOBOX_LARGE);
		// selectCourse.setItems(models);

		this.subject = VaadinWrapper.builder().caption("Title").placeholder("Write 100 characters at max")
				.maxLength(100).build().textField();

		this.scheduledDate = VaadinWrapper.builder().caption("Schedule date for announcement!").build().dateField();
		this.scheduledDate.setValue(LocalDate.now());

		this.announcementDescription = VaadinWrapper.builder().caption("Description for announcement, keep it simple!")
				.build().richTextArea();

		binder.bind(this.selectCourse, AnnouncementModel::getCourseName, AnnouncementModel::setCourseName);
		binder.bind(this.subject, AnnouncementModel::getSubject, AnnouncementModel::setSubject);
		binder.bind(this.announceToAll, AnnouncementModel::isSendToAll, AnnouncementModel::setSendToAll);
		binder.bind(this.announcementDescription, AnnouncementModel::getDescription, AnnouncementModel::setDescription);
		binder.bind(this.scheduledDate, AnnouncementModel::getScheduleDate, AnnouncementModel::setScheduleDate);
		binder.bind(this.subject, AnnouncementModel::getSubject, AnnouncementModel::setSubject);

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
		publish.addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), publish)) {
				return;
			}
			AnnouncementModel model = AnnouncementModel.builder().build();
			try {
				this.binder.writeBean(model);
				FactoryResponse fr = this.announcementResponseService.saveUpdate(model);
				Utils.showFactoryResponseMsg(fr);
				if (SummaryMessageEnum.SUCCESS == fr.getSummaryMessage()) {
					Utils.showFactoryResponseMsg(fr, close -> {
						getUI().getNavigator().navigateTo(ViewConstants.SEE_ANNOUNCEMENT);
					});
				}
			} catch (ValidationException e) {
				LOGGER.error(e.getMessage());
				Utils.showErrorNotification("Unable to post/update");
			}
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);

		try {
			// TODO: implement a security check before entering into view.
			if (this.announcementModel != null) {
				this.binder.readBean(announcementModel);
				return;
			}
			this.uiService.setItemsCourseNames(this.selectCourse);

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
		this.binder.setBean(AnnouncementModel.builder().build());
	}

}
