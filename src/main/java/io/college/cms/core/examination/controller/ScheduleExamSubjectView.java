package io.college.cms.core.examination.controller;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.Utils;
import io.college.cms.core.courses.controller.constants.SubjectType;
import io.college.cms.core.examination.model.ExaminationModel;
import io.college.cms.core.examination.model.TimeTableModel;
import io.college.cms.core.examination.service.ExamResponseService;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.model.ViewConstants;
import io.college.cms.core.ui.services.CoreUiService;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ScheduleExamSubjectView extends VerticalLayout implements View {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Panel rootPanel;
	private VerticalLayout rootLayout;
	private HorizontalSplitPanel splitPanel;
	private VerticalLayout firstLayout;
	private ComboBox<String> examsCb;
	private Label courseNameLb;
	private Label semesterLb;
	private Label examStartDateTimeLbl;
	private Label examEndDateTimeLbl;
	private VerticalLayout secondLayout;
	private ComboBox<String> subjectsCb;
	private ComboBox<String> subjectTypeCb;
	private HorizontalLayout hDateLayout;
	private DateField examStartDateTimeDf;
	private DateField examEndDateTimeDf;
	private RichTextArea additionalInformation;
	private Button saveBtn;
	private Binder<TimeTableModel> binder;
	private CoreUiService uiService;
	private ApplicationContext app;
	private ExamResponseService examResponseService;

	/**
	 * @param uiService
	 * @param app
	 */
	public ScheduleExamSubjectView(CoreUiService uiService, ApplicationContext app) {
		super();
		this.uiService = uiService;
		this.app = app;
		this.examResponseService = app.getBean(ExamResponseService.class);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		uiService.setExamsName(examsCb);
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

	@PostConstruct
	public void paint() {
		binder = new Binder<>();
		this.rootPanel = new Panel();

		this.rootLayout = new VerticalLayout();

		this.splitPanel = new HorizontalSplitPanel();

		this.firstLayout = new VerticalLayout();

		this.examsCb = new ComboBox<String>();

		this.courseNameLb = new Label();

		this.semesterLb = new Label();

		this.examStartDateTimeLbl = new Label();

		this.examEndDateTimeLbl = new Label();

		this.secondLayout = new VerticalLayout();

		this.subjectsCb = new ComboBox<String>();

		this.subjectTypeCb = new ComboBox<String>();

		this.hDateLayout = new HorizontalLayout();

		this.examStartDateTimeDf = new DateField();

		this.examEndDateTimeDf = new DateField();

		this.additionalInformation = new RichTextArea();

		this.saveBtn = new Button();

		this.examsCb = (ComboBox<String>) VaadinWrapper.builder().caption("Exam name").placeholder("search by exam")
				.required(true).visible(true).enabled(true).build().comboBox();

		this.courseNameLb = VaadinWrapper.builder().caption("Courses Name").required(true).visible(true).enabled(true)
				.build().label();

		this.semesterLb = VaadinWrapper.builder().caption("Semester").required(true).visible(true).enabled(true).build()
				.label();

		this.examStartDateTimeLbl = VaadinWrapper.builder().caption("Exam start date time").required(true).visible(true)
				.enabled(true).build().label();

		this.examEndDateTimeLbl = VaadinWrapper.builder().caption("Exam end date time").required(true).visible(true)
				.enabled(true).build().label();

		this.subjectsCb = uiService.getSubjectList();

		this.subjectTypeCb = (ComboBox<String>) VaadinWrapper.builder().caption("Subject type")
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
		firstLayout.addComponents(examsCb, courseNameLb, semesterLb, examStartDateTimeLbl, examEndDateTimeLbl);

		secondLayout.addComponents(subjectsCb, subjectTypeCb, hDateLayout, additionalInformation);

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
		examEndDateTimeListener.setMandatoryListFields(examsCb, subjectsCb, subjectTypeCb);

		EmptyFieldListener<LocalDate> examStartDateTimeListener = new EmptyFieldListener<LocalDate>();
		examStartDateTimeListener.setSourceDateField(examStartDateTimeDf);
		examStartDateTimeListener.setTargetBtn(saveBtn);
		examStartDateTimeListener.setMandatoryDateFields(examStartDateTimeDf, examEndDateTimeDf);
		examStartDateTimeListener.setMandatoryListFields(examsCb, subjectsCb, subjectTypeCb);
		examStartDateTimeDf.addValueChangeListener(examStartDateTimeListener);

		EmptyFieldListener<String> subjectTypeListener = new EmptyFieldListener<String>();
		subjectTypeListener.setSourceListField(subjectTypeCb);
		subjectTypeListener.setTargetBtn(saveBtn);
		subjectTypeListener.setMandatoryDateFields(examStartDateTimeDf, examEndDateTimeDf);
		subjectTypeListener.setMandatoryListFields(examsCb, subjectsCb, subjectTypeCb);
		subjectTypeCb.addValueChangeListener(subjectTypeListener);
		EmptyFieldListener<String> subjectNameListener = new EmptyFieldListener<String>();
		subjectNameListener.setSourceListField(subjectsCb);
		subjectNameListener.setTargetBtn(saveBtn);
		subjectNameListener.setMandatoryDateFields(examStartDateTimeDf, examEndDateTimeDf);
		subjectNameListener.setMandatoryListFields(examsCb, subjectsCb, subjectTypeCb);
		subjectsCb.addValueChangeListener(subjectNameListener);

		subjectTypeCb.setItems(SubjectType.THEORY.toString(), SubjectType.PRACTICAL.toString(),
				SubjectType.OTHER.toString(), SubjectType.INTERNAL.toString());

		binder.bind(examsCb, TimeTableModel::getExamName, TimeTableModel::setExamName);
		binder.bind(subjectsCb, TimeTableModel::getSubject, TimeTableModel::setSubject);
		binder.bind(subjectTypeCb, TimeTableModel::getSubjectType, TimeTableModel::setSubjectType);
		binder.bind(examStartDateTimeDf, TimeTableModel::getStartTime, TimeTableModel::setStartTime);
		binder.bind(examEndDateTimeDf, TimeTableModel::getEndTime, TimeTableModel::setEndTime);
		binder.bind(additionalInformation, TimeTableModel::getAdditionalInformation,
				TimeTableModel::setAdditionalInformation);

		saveBtn.addClickListener(click -> {
			TimeTableModel model = TimeTableModel.builder().build();
			model.setCourseName(courseNameLb.getValue());
			model.setSemester(semesterLb.getValue());
			try {
				binder.writeBean(model);
				FactoryResponse fr = examResponseService.saveTimeTable(model);
				Notification notifi = Utils.showFactoryResponseMsg(fr);
				notifi.setCaption("Message");
				notifi.addCloseListener(
						close -> getUI().getNavigator().navigateTo(ViewConstants.SUBJECT_VIEW_TIME_TABLE));
			} catch (ValidationException e) {
				e.printStackTrace();
				Utils.showErrorNotification("Unable to save/update information");
			}
		});
		examsCb.addSelectionListener(select -> {
			if (examsCb.getSelectedItem().isPresent() == false) {
				return;
			}
			FactoryResponse fr = examResponseService.getExamByExamId(null, Utils.val(examsCb.getOptionalValue()));
			if (Utils.isError(fr)) {
				Utils.showErrorNotification("Unable to load exam data");
				return;
			}
			ExaminationModel examModel = (ExaminationModel) fr.getResponse();
			uiService.setItemsSubject(subjectsCb, examModel.getCourseName(), examModel.getSemester());
			courseNameLb.setValue(examModel.getCourseName());
			semesterLb.setValue(examModel.getSemester());
			examStartDateTimeLbl.setValue(String.valueOf(examModel.getExamStartDate()));
			examEndDateTimeLbl.setValue(String.valueOf(examModel.getExamEndDate()));

		});
	}

}
