package io.college.cms.core.examination.controller;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.navigator.View;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ScheduleExamSubjectDateView extends VerticalLayout implements View {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Panel rootPanel;
	private VerticalLayout rootLayout;
	private HorizontalSplitPanel splitPanel;
	private VerticalLayout firstLayout;
	private ComboBox<String> examName;
	private Label courseName;
	private Label semester;
	private Label examStartDateTimeLbl;
	private Label examEndDateTimeLbl;
	private VerticalLayout secondLayout;
	private ComboBox<String> subjectName;
	private ComboBox<String> subjectType;
	private HorizontalLayout hDateLayout;
	private DateField examStartDateTimeDf;
	private DateField examEndDateTimeDf;
	private RichTextArea additionalInformation;
	private Button saveBtn;

	@PostConstruct
	public void paint() {

		this.rootPanel = new Panel();

		this.rootLayout = new VerticalLayout();

		this.splitPanel = new HorizontalSplitPanel();

		this.firstLayout = new VerticalLayout();

		this.examName = new ComboBox<String>();

		this.courseName = new Label();

		this.semester = new Label();

		this.examStartDateTimeLbl = new Label();

		this.examEndDateTimeLbl = new Label();

		this.secondLayout = new VerticalLayout();

		this.subjectName = new ComboBox<String>();

		this.subjectType = new ComboBox<String>();

		this.hDateLayout = new HorizontalLayout();

		this.examStartDateTimeDf = new DateField();

		this.examEndDateTimeDf = new DateField();

		this.additionalInformation = new RichTextArea();

		this.saveBtn = new Button();

		this.examName = (ComboBox<String>) VaadinWrapper.builder().caption("Exam name").placeholder("search by exam")
				.required(true).visible(true).enabled(true).build().comboBox();

		this.courseName = VaadinWrapper.builder().caption("Courses Name").required(true).visible(true).enabled(true)
				.build().label();

		this.semester = VaadinWrapper.builder().caption("Semester").required(true).visible(true).enabled(true).build()
				.label();

		this.examStartDateTimeLbl = VaadinWrapper.builder().caption("Exam start date time").required(true).visible(true)
				.enabled(true).build().label();

		this.examEndDateTimeLbl = VaadinWrapper.builder().caption("Exam end date time").required(true).visible(true)
				.enabled(true).build().label();

		this.subjectName = (ComboBox<String>) VaadinWrapper.builder().caption("Subject name")
				.placeholder("search by subject").required(true).visible(true).enabled(true).build().comboBox();

		this.subjectType = (ComboBox<String>) VaadinWrapper.builder().caption("Subject type")
				.placeholder("search by type").required(true).visible(true).enabled(true).build().comboBox();

		this.examStartDateTimeDf = VaadinWrapper.builder().caption("exam start date").required(true).visible(true)
				.enabled(true).build().dateField();

		this.examEndDateTimeDf = VaadinWrapper.builder().caption("Exam end date").required(true).visible(true)
				.enabled(true).build().dateField();

		this.additionalInformation = VaadinWrapper.builder().caption("Additional information").visible(true)
				.enabled(true).required(false).build().richTextArea();

		this.saveBtn = VaadinWrapper.builder().caption("Save settings").required(true).visible(true).enabled(false)
				.build().button();
		this.saveBtn.setCaption("Save settings");
		this.saveBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
		hDateLayout.setSizeFull();
		firstLayout.addComponents(examName, courseName, semester, examStartDateTimeLbl, examEndDateTimeLbl);

		secondLayout.addComponents(subjectName, subjectType, hDateLayout, additionalInformation);

		hDateLayout.addComponents(examStartDateTimeDf, examEndDateTimeDf);

		splitPanel.addComponents(new Panel(firstLayout), new Panel(secondLayout));
		splitPanel.setSplitPosition(30.0f);

		rootLayout.addComponents(splitPanel, saveBtn);
		rootLayout.setComponentAlignment(this.saveBtn, Alignment.BOTTOM_RIGHT);
		rootPanel.setContent(rootLayout);
		addComponent(rootPanel);
		EmptyFieldListener<LocalDate> examEndDateTimeListener = new EmptyFieldListener<LocalDate>();
		examEndDateTimeListener.setSourceDateField(examEndDateTimeDf);
		examEndDateTimeListener.setTargetBtn(saveBtn);
		examEndDateTimeListener.setMandatoryDateFields(examStartDateTimeDf, examEndDateTimeDf);
		examEndDateTimeListener.setMandatoryListFields(examName, subjectName, subjectType);

		EmptyFieldListener<LocalDate> examStartDateTimeListener = new EmptyFieldListener<LocalDate>();
		examStartDateTimeListener.setSourceDateField(examStartDateTimeDf);
		examStartDateTimeListener.setTargetBtn(saveBtn);
		examStartDateTimeListener.setMandatoryDateFields(examStartDateTimeDf, examEndDateTimeDf);
		examStartDateTimeListener.setMandatoryListFields(examName, subjectName, subjectType);
		examStartDateTimeDf.addValueChangeListener(examStartDateTimeListener);

		EmptyFieldListener<String> subjectTypeListener = new EmptyFieldListener<String>();
		subjectTypeListener.setSourceListField(subjectType);
		subjectTypeListener.setTargetBtn(saveBtn);
		subjectTypeListener.setMandatoryDateFields(examStartDateTimeDf, examEndDateTimeDf);
		subjectTypeListener.setMandatoryListFields(examName, subjectName, subjectType);
		subjectType.addValueChangeListener(subjectTypeListener);
		EmptyFieldListener<String> subjectNameListener = new EmptyFieldListener<String>();
		subjectNameListener.setSourceListField(subjectName);
		subjectNameListener.setTargetBtn(saveBtn);
		subjectNameListener.setMandatoryDateFields(examStartDateTimeDf, examEndDateTimeDf);
		subjectNameListener.setMandatoryListFields(examName, subjectName, subjectType);
		subjectName.addValueChangeListener(subjectNameListener);
	}

}
