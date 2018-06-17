package io.college.cms.core.examination.controller;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.examination.db.ExaminationModel;
import io.college.cms.core.examination.service.ExamResponseService;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.util.ElementHelper;
import io.college.cms.core.ui.util.ListenerUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class PublishExamView extends VerticalLayout implements View {
	
	private static final long serialVersionUID = 1L;
	private ExamResponseService examResponseService;
	private ExaminationModel model;
	private Panel panel = new Panel();
	private VerticalLayout rootLayout = new VerticalLayout();
	private TextField examName = new TextField();
	private ComboBox<String> selectCourse = new ComboBox<String>();
	private ComboBox<String> selectSem = new ComboBox<String>();
	private DateField startExamDate = new DateField();
	private DateField endExamDate = new DateField();
	private HorizontalLayout horizontalLayout = new HorizontalLayout();
	private Button deleteExam = new Button();
	private Button publish = new Button();
	private Panel panelStep2 = new Panel();
	private HorizontalLayout rootLayoutStep2 = new HorizontalLayout();
	private VerticalLayout vRootLayoutStep2 = new VerticalLayout();
	private ComboBox<String> selectSubject = new ComboBox<String>();
	private DateField startTimeSubject = new DateField();
	private DateField endTimeSubject = new DateField();
	private Button addTab = new Button();
	private Button saveExams = new Button();
	private ExaminationModel.ExaminationModelBuilder builder;

	public void setModel(ExaminationModel model) {
		this.model = model;
	}

	@Autowired
	public void setExamResponseService(ExamResponseService examResponseService) {
		this.examResponseService = examResponseService;
	}

	@PostConstruct
	public void paint() {
		Accordion accord = new Accordion();

		selectCourse.setSizeFull();
		selectSem.setSizeFull();
		startExamDate.setSizeFull();
		endExamDate.setSizeFull();
		panel.setContent(rootLayout);
		examName.setCaption("Exam name");
		examName.setPlaceholder("Say, Exam 2018- Course name");
		examName.setVisible(true);
		examName.setEnabled(true);
		examName.setRequiredIndicatorVisible(true);
		examName.setWidth("50%");
		examName.setResponsive(true);
		examName.focus();
		rootLayout.setResponsive(true);
		rootLayout.addComponent(examName);
		rootLayout.setComponentAlignment(examName, Alignment.TOP_RIGHT);

		selectCourse.setCaption("Course name");
		selectCourse.setRequiredIndicatorVisible(true);
		selectCourse.setVisible(true);
		selectCourse.setEnabled(true);
		selectCourse.setResponsive(true);

		rootLayout.addComponent(selectCourse);

		selectSem.setCaption("Semester");
		selectSem.setRequiredIndicatorVisible(true);
		selectSem.setVisible(true);
		selectSem.setEnabled(true);
		selectSem.setResponsive(true);
		rootLayout.addComponent(selectSem);
		startExamDate.setCaption("Exam start date");
		startExamDate.setRequiredIndicatorVisible(true);
		startExamDate.setVisible(true);
		startExamDate.setEnabled(true);
		startExamDate.setResponsive(true);
		rootLayout.addComponent(startExamDate);
		endExamDate.setCaption("Exam end date");
		endExamDate.setRequiredIndicatorVisible(true);
		endExamDate.setVisible(true);
		endExamDate.setEnabled(true);
		endExamDate.setResponsive(true);
		rootLayout.addComponent(endExamDate);
		rootLayout.addComponent(horizontalLayout);
		deleteExam.setCaption("Delete examination");
		deleteExam.setVisible(true);
		deleteExam.setEnabled(true);
		deleteExam.setStyleName(ValoTheme.BUTTON_DANGER);
		deleteExam.setResponsive(true);
		deleteExam.addClickListener(click -> {
			if (ListenerUtility.isValidSourceEvent(click.getComponent(), deleteExam)) {
				Window window = new Window();
				window.setResizable(false);
				window.setClosable(false);
				window.setWidth("50%");
				window.center();
				VerticalLayout verticalLayout = new VerticalLayout();
				verticalLayout.addComponent(
						new Label("You're about to delete content, once deleted it cannot be recovered."));
				HorizontalLayout hLayout = new HorizontalLayout();
				Button delete = new Button("Delete");
				Button close = new Button("Close");
				delete.setStyleName(ValoTheme.BUTTON_DANGER);
				close.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
				delete.addClickListener(clickDelete -> {
					window.setVisible(false);
					FactoryResponse fr = examResponseService
							.deleteByExamName(ElementHelper.value(examName.getOptionalValue()));
					if (SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
						Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
						notifi.setDescription(String.valueOf(fr.getResponse()));
						System.out.println(notifi.getDescription());
						notifi.setDelayMsec(Notification.DELAY_FOREVER);
						notifi.setCaption("Unable to delete");
						notifi.setIcon(VaadinIcons.EDIT);

					} else {
						Notification notifi = Notification.show("", Type.HUMANIZED_MESSAGE);
						notifi.setDelayMsec(Notification.DELAY_FOREVER);
						notifi.setIcon(VaadinIcons.CHECK);
						notifi.setCaption("Exam deleted");
						notifi.setDescription(
								"Exam is now successfully deleted! Please refresh the page if content is still available or connect with web admin.");

					}
					clickDelete.getComponent().setVisible(false);
					clickDelete.getComponent().getParent().setVisible(false);
				});
				close.addClickListener(clickUser -> {
					window.close();
				});
				hLayout.addComponents(close, delete);
				verticalLayout.addComponent(hLayout);
				verticalLayout.setComponentAlignment(hLayout, Alignment.BOTTOM_RIGHT);
				window.setContent(verticalLayout);
				getUI().addWindow(window);
			}
		});
		horizontalLayout.addComponent(deleteExam);
		publish.setCaption("Save & Next");
		publish.setVisible(true);
		publish.setEnabled(false);
		publish.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		publish.setResponsive(true);
		horizontalLayout.addComponent(publish);
		horizontalLayout.setResponsive(true);
		rootLayout.setComponentAlignment(horizontalLayout, Alignment.BOTTOM_RIGHT);
		EmptyFieldListener<LocalDate> startExamDateListener = new EmptyFieldListener<LocalDate>();
		startExamDateListener.setSourceDateField(startExamDate);
		startExamDateListener.setTargetBtn(publish);
		startExamDateListener.setMandatoryDateFields(startExamDate);
		startExamDateListener.setMandatoryFields(startExamDate, endExamDate);
		startExamDateListener.setMandatoryListFields(selectCourse, selectSem);
		startExamDate.addValueChangeListener(startExamDateListener);
		EmptyFieldListener<String> selectCourseListener = new EmptyFieldListener<String>();
		selectCourseListener.setSourceListField(selectCourse);
		selectCourseListener.setTargetBtn(publish);
		selectCourseListener.setMandatoryFields(startExamDate, endExamDate);
		selectCourseListener.setMandatoryListFields(selectCourse, selectSem);
		selectCourse.addValueChangeListener(selectCourseListener);
		EmptyFieldListener<String> selectSemListener = new EmptyFieldListener<String>();
		selectSemListener.setSourceListField(selectSem);
		selectSemListener.setTargetBtn(publish);
		selectSemListener.setMandatoryFields(startExamDate, endExamDate);
		selectSemListener.setMandatoryListFields(selectCourse, selectSem);
		selectSem.addValueChangeListener(selectSemListener);
		EmptyFieldListener<LocalDate> endExamDateListener = new EmptyFieldListener<LocalDate>();
		endExamDateListener.setSourceDateField(endExamDate);
		endExamDateListener.setTargetBtn(publish);
		endExamDateListener.setMandatoryDateFields(endExamDate);
		endExamDateListener.setMandatoryFields(startExamDate, endExamDate);
		endExamDateListener.setMandatoryListFields(selectCourse, selectSem);
		endExamDate.addValueChangeListener(endExamDateListener);
		accord.addTab(panel, "Exam (1/2)");
		accord.setResponsive(true);
		accord.setWidth("50%");
		addComponent(accord);

		setComponentAlignment(accord, Alignment.MIDDLE_CENTER);

		addComponent(panelStep2);
		setComponentAlignment(panelStep2, Alignment.MIDDLE_CENTER);
		panelStep2.setContent(rootLayoutStep2);
		panelStep2.setResponsive(true);
		rootLayoutStep2.setResponsive(true);
		rootLayoutStep2.addComponent(vRootLayoutStep2);
		selectSubject.setResponsive(true);
		selectSubject.setCaption("Subject Name");
		selectSubject.setPlaceholder("Say, Subject 2018- ");
		selectSubject.setRequiredIndicatorVisible(true);
		selectSubject.setVisible(true);
		selectSubject.setEnabled(true);
		vRootLayoutStep2.addComponent(selectSubject);
		startTimeSubject.setResponsive(true);
		startTimeSubject.setCaption("Subject start time");
		startTimeSubject.setRequiredIndicatorVisible(true);
		startTimeSubject.setVisible(true);
		startTimeSubject.setEnabled(true);
		vRootLayoutStep2.addComponent(startTimeSubject);
		endTimeSubject.setResponsive(true);
		endTimeSubject.setCaption("Subject End time");
		endTimeSubject.setRequiredIndicatorVisible(true);
		endTimeSubject.setVisible(true);
		endTimeSubject.setEnabled(true);
		vRootLayoutStep2.addComponent(endTimeSubject);
		addTab.setCaption("Add time table");
		addTab.setVisible(true);
		addTab.setEnabled(false);
		addTab.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		addTab.setResponsive(true);
		vRootLayoutStep2.addComponent(addTab);
		saveExams.setCaption("Save");
		saveExams.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		saveExams.setVisible(true);
		saveExams.setEnabled(true);
		saveExams.setResponsive(true);
		EmptyFieldListener<LocalDate> startTimeSubjectListener = new EmptyFieldListener<LocalDate>();
		startTimeSubjectListener.setSourceDateField(startTimeSubject);
		startTimeSubjectListener.setTargetBtn(addTab);
		startTimeSubjectListener.setMandatoryDateFields(startTimeSubject);
		startTimeSubjectListener.setMandatoryFields(startTimeSubject, endTimeSubject);
		startTimeSubjectListener.setMandatoryListFields(selectSubject);
		startTimeSubject.addValueChangeListener(startTimeSubjectListener);
		EmptyFieldListener<String> selectSubjectListener = new EmptyFieldListener<String>();
		selectSubjectListener.setSourceListField(selectSubject);
		selectSubjectListener.setTargetBtn(addTab);
		selectSubjectListener.setMandatoryFields(startTimeSubject, endTimeSubject);
		selectSubjectListener.setMandatoryListFields(selectSubject);
		selectSubject.addValueChangeListener(selectSubjectListener);
		EmptyFieldListener<LocalDate> endTimeSubjectListener = new EmptyFieldListener<LocalDate>();
		endTimeSubjectListener.setSourceDateField(endTimeSubject);
		endTimeSubjectListener.setTargetBtn(addTab);
		endTimeSubjectListener.setMandatoryDateFields(endTimeSubject);
		endTimeSubjectListener.setMandatoryFields(startTimeSubject, endTimeSubject);
		endTimeSubjectListener.setMandatoryListFields(selectSubject);
		endTimeSubject.addValueChangeListener(endTimeSubjectListener);
		VerticalLayout secondStep2ListLayout = new VerticalLayout();
		ListSelect<String> listSelectSubjects = new ListSelect<>("Subjects");
		listSelectSubjects.setSizeFull();
		secondStep2ListLayout.addComponents(listSelectSubjects);
		secondStep2ListLayout.addComponent(saveExams);
		secondStep2ListLayout.setComponentAlignment(saveExams, Alignment.BOTTOM_RIGHT);

		rootLayoutStep2.addComponents(secondStep2ListLayout);
		accord.addTab(rootLayoutStep2, "Exam (2/2)");
		accord.getTab(1).setEnabled(false);
		rootLayoutStep2.setSizeFull();		
		publish.addClickListener(list -> {
			builder = ExaminationModel.builder().courseName(ElementHelper.value(selectCourse.getSelectedItem()))
					.examName(ElementHelper.value(examName.getOptionalValue()))
					.examStartDate(ElementHelper.value(startExamDate.getOptionalValue()))
					.examEndDate(ElementHelper.value(endExamDate.getOptionalValue()));
			FactoryResponse fr = examResponseService.saveExamMetadata(builder.build());

			if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
				Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
				notifi.setDelayMsec(Notification.DELAY_FOREVER);
				notifi.setCaption("Unable to save/update");
				if (fr != null) {
					notifi.setDescription((String) fr.getResponse());
				} else {
					notifi.setDescription("We couldn't save/update exam details.");
				}

			} else {
				accord.getTab(1).setEnabled(true);
			}
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			LOGGER.debug("request received view : {}", event);
			if (model != null) {
				examName.setValue(model.getExamName());
				if (model.getExamStartDate() != null) {
					startExamDate.setValue(LocalDate.from(model.getExamStartDate()));
				}
				if (model.getExamEndDate() != null) {
					endExamDate.setValue(LocalDate.from(model.getExamEndDate()));
				}
				if (StringUtils.isNotEmpty(model.getCourseName())) {
					selectCourse.setSelectedItem(model.getCourseName());
				}
				if (StringUtils.isNotEmpty(model.getSemester())) {
					selectSem.setSelectedItem(model.getSemester());
				}
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
	}

}
