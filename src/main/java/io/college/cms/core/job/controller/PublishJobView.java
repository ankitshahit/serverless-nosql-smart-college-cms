package io.college.cms.core.job.controller;

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
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.ui.builder.VaadinWrapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
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

	/**
	 * 
	 */
	public PublishJobView() {
		super();
		this.jobTitle = new TextField();
		this.jobDescription = new RichTextArea();
		this.additionalInfo = new RichTextArea();
		this.totalSalary = new TextField();
		this.interViewDate = new DateField();
		this.location = new TextField();
		this.postedBy = new TextArea();
		this.contactInformation = new TextArea();
	}

	@PostConstruct
	public void paint() {
		Panel panel = new Panel();

		VerticalLayout rightSplit = new VerticalLayout(totalSalary, location, interViewDate, postedBy,
				contactInformation);

		VerticalLayout rootLayout = new VerticalLayout();
		VerticalLayout leftSplit = new VerticalLayout(jobTitle, jobDescription, additionalInfo);

		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(leftSplit, rightSplit);
		splitPanel.setSplitPosition(70.0f);
		addComponent(panel);
		setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
		panel.setContent(rootLayout);
		this.jobTitle.focus();
		this.jobTitle.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_INLINE_ICON,
				ValoTheme.TEXTFIELD_LARGE);
		this.jobTitle.setSizeFull();
		jobDescription.addStyleNames(ValoTheme.TEXTAREA_ALIGN_CENTER, ValoTheme.TEXTAREA_LARGE);
		jobDescription.setCaptionAsHtml(true);
		jobDescription.setCaption("<p>Job Description</p>");
		VaadinWrapper.setTextField(jobTitle, "Job Title", "job title");
		VaadinWrapper.setRichTextArea(additionalInfo, "Addditional Information", "information..");
		VaadinWrapper.setTextField(totalSalary, "Total Salary", "salary..");
		VaadinWrapper.setTextField(location, "Location", "location");
		location.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_INLINE_ICON,
				ValoTheme.TEXTFIELD_LARGE);
		interViewDate.setCaption("Interview Date");
		interViewDate.setRequiredIndicatorVisible(true);
		interViewDate.addStyleNames(ValoTheme.DATEFIELD_ALIGN_CENTER, ValoTheme.DATEFIELD_LARGE);
		totalSalary.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_INLINE_ICON,
				ValoTheme.TEXTFIELD_LARGE);
		interViewDate.setValue(LocalDate.now());
		postedBy.setCaption("Posted By");
		postedBy.setPlaceholder("posted by..");
		postedBy.setVisible(true);
		postedBy.setEnabled(true);
		postedBy.setRequiredIndicatorVisible(true);
		postedBy.setResponsive(true);
		postedBy.addStyleNames(ValoTheme.TEXTAREA_ALIGN_CENTER, ValoTheme.TEXTAREA_LARGE);
		contactInformation.setCaption("Contact Information");
		contactInformation.setPlaceholder("contact info..");
		contactInformation.setVisible(true);
		contactInformation.setEnabled(true);
		contactInformation.setRequiredIndicatorVisible(true);
		contactInformation.setResponsive(true);
		contactInformation.addStyleNames(ValoTheme.TEXTAREA_ALIGN_CENTER, ValoTheme.TEXTAREA_LARGE);
		Button disableBtn = new Button("Disable");
		disableBtn.addStyleNames(ValoTheme.BUTTON_DANGER);
		disableBtn.addClickListener(click -> {
		});
		Button postBtn = new Button("Post");
		postBtn.addStyleNames(ValoTheme.BUTTON_PRIMARY);
		postBtn.addClickListener(click -> {
		});
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.addComponents(disableBtn, postBtn);

		VerticalLayout layout2 = new VerticalLayout();
		layout2.addComponents(splitPanel);
		layout2.addComponent(buttonLayout);
		layout2.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
		Panel panel2 = new Panel();
		panel2.setContent(layout2);
		rootLayout.addComponent(panel2);

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