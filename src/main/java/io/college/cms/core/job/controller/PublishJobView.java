package io.college.cms.core.job.controller;

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
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.application.Utils;
import io.college.cms.core.job.model.JobModel;
import io.college.cms.core.job.services.JobResponseService;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.model.ViewConstants;
import io.college.cms.core.ui.services.CoreUiService;
import io.college.cms.core.ui.util.ListenerUtility;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
@EqualsAndHashCode(callSuper = false)
@Data
public class PublishJobView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private TextField jobTitle;
	private RichTextArea jobDescription;
	private RichTextArea additionalInfo;
	private TextField totalSalary;
	private DateField interViewDate;
	private TextField location;
	private TextArea postedBy;
	private TextArea contactInformation;
	private JobResponseService jobResponseService;
	private CoreUiService uiService;
	private JobModel jobModel;
	private Binder<JobModel> binder;

	@Autowired
	public PublishJobView(JobResponseService jobResponseService, CoreUiService coreUiService) {
		super();
		this.jobResponseService = jobResponseService;
		this.uiService = coreUiService;
		this.jobTitle = new TextField();
		this.jobDescription = new RichTextArea();
		this.additionalInfo = new RichTextArea();
		this.totalSalary = new TextField();
		this.interViewDate = new DateField();
		this.location = new TextField();
		this.postedBy = new TextArea();
		this.contactInformation = new TextArea();
		this.binder = new Binder<>();
	}

	@PostConstruct
	public void paint() {

		this.jobTitle = VaadinWrapper.builder().caption("Job Title").build().textField();
		this.jobDescription = VaadinWrapper.builder().caption("Job Description").build().richTextArea();
		this.location = VaadinWrapper.builder().caption("Location").placeholder("Details about interview location")
				.build().textField();
		this.totalSalary = VaadinWrapper.builder().caption("Salary").placeholder("salary").build().textField();
		this.postedBy = VaadinWrapper.builder().caption("Posted by").placeholder("posted by").build().textArea();
		this.interViewDate = VaadinWrapper.builder().caption("Interview date").build().dateField();
		this.interViewDate.setValue(LocalDate.now());
		this.additionalInfo = VaadinWrapper.builder().caption("Additional information").build().richTextArea();

		this.contactInformation = VaadinWrapper.builder().caption("Contact information")
				.placeholder("Contact information").build().textArea();
		this.jobTitle.focus();
		Button disableBtn = new Button("Disable");
		disableBtn.addStyleNames(ValoTheme.BUTTON_DANGER);
		disableBtn.addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), disableBtn)) {
				return;
			}
		});
		binder.bind(jobTitle, JobModel::getTitle, JobModel::setTitle);
		binder.bind(jobDescription, JobModel::getDescription, JobModel::setDescription);
		binder.bind(contactInformation, JobModel::getContactInformation, JobModel::setContactInformation);
		binder.bind(postedBy, JobModel::getPostedBy, JobModel::setPostedBy);
		binder.bind(interViewDate, JobModel::getInterViewDate, JobModel::setInterViewDate);
		binder.bind(totalSalary, JobModel::getSalary, JobModel::setSalary);
		binder.bind(additionalInfo, JobModel::getAdditionalInformation, JobModel::setAdditionalInformation);
		binder.bind(location, JobModel::getLocation, JobModel::setLocation);
		Button postBtn = new Button("Post");
		postBtn.addStyleNames(ValoTheme.BUTTON_PRIMARY);
		postBtn.addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), postBtn)) {
				return;
			}
			JobModel model = JobModel.builder().build();

			try {
				binder.writeBean(model);
				FactoryResponse fr = this.jobResponseService.saveUpdate(model);
				Utils.showFactoryResponseMsg(fr);
				if (SummaryMessageEnum.SUCCESS == fr.getSummaryMessage()) {
					Utils.showFactoryResponseMsg(fr, close -> {
						// TODO: make sure that the view all jobs screen
						// redirects us to here?
						getUI().getNavigator().navigateTo(ViewConstants.VIEW_ALL_JOB);
						return;
					});
				}

			} catch (ValidationException e) {
				LOGGER.error(e.getLocalizedMessage());
				Utils.showErrorNotification("Unable to post/update");
			}
		});

		VerticalLayout rootLayout = new VerticalLayout();
		VerticalLayout leftSplit = new VerticalLayout(jobTitle, jobDescription, additionalInfo);
		VerticalLayout rightSplit = new VerticalLayout(totalSalary, location, interViewDate, postedBy,
				contactInformation);
		leftSplit.setSizeFull();
		rightSplit.setSizeFull();

		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(leftSplit, rightSplit);
		splitPanel.setSplitPosition(70.0f);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.addComponents(disableBtn, postBtn);

		VerticalLayout layout2 = new VerticalLayout();
		layout2.addComponents(splitPanel);
		layout2.addComponent(buttonLayout);
		layout2.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
		Panel panel2 = new Panel();
		panel2.setContent(layout2);
		splitPanel.setSizeFull();
		layout2.setSizeFull();
		rootLayout.setSizeFull();
		panel2.setSizeFull();
		rootLayout.addComponent(panel2);
		VerticalLayout layout = new VerticalLayout();
		Panel layoutPanel = new Panel();
		layoutPanel.setContent(rootLayout);
		layoutPanel.setSizeFull();
		layout.setSizeFull();
		layout.addComponents(layoutPanel);
		addComponent(layout);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			LOGGER.debug("request received view : {}", event);
			if (this.jobModel != null) {
				binder.setBean(jobModel);
				return;
			}
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
		// TODO: Do we want to clear all the fields?
		// should clear the page.
		binder.readBean(JobModel.builder().build());
	}

}