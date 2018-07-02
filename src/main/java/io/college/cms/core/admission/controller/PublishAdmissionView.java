package io.college.cms.core.admission.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.data.Binder;
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
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.admission.model.AdmissionMetaModel;
import io.college.cms.core.admission.services.AdmissionResponseService;
import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.Utils;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.db.CourseModel.SubjectModel;
import io.college.cms.core.courses.service.CourseResponseService;
import io.college.cms.core.ui.builder.DeletePopupView;
import io.college.cms.core.ui.builder.MessagePopupView;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.services.CoreUiService;
import io.college.cms.core.ui.util.ElementHelper;
import io.college.cms.core.ui.util.ListenerUtility;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class PublishAdmissionView extends VerticalLayout implements View {
	private static final long serialVersionUID = 1L;
	private CourseResponseService courseResponseService;
	private TextField fees;
	private RichTextArea additionalDetails;
	private CheckBox verifyFeesReceipt;
	private Button feesHelpBtn;
	private ListSelect<String> subjects;
	private ComboBox<String> courses;
	private CheckBox showEnrolledOutOf;
	private Button showEnrolledHelpBtn;
	private Label maxStudents;
	private Label totalEnrolled;
	private Button disableAdmissions;
	private CoreUiService uiService;
	private Binder<AdmissionMetaModel> binder;
	private ComboBox<String> semester;
	private AdmissionResponseService admissionService;
	@Setter
	private AdmissionMetaModel model;

	@Autowired
	public PublishAdmissionView(CoreUiService coreUiService, AdmissionResponseService admissionService,
			CourseResponseService courseResponse) {
		this.uiService = coreUiService;
		this.admissionService = admissionService;
		this.courseResponseService = courseResponse;
		this.binder = new Binder<>();
	}

	@PostConstruct
	public void paint() {
		Panel rootPanel = new Panel();
		rootPanel.setSizeFull();

		VerticalLayout rootLayout = new VerticalLayout();
		HorizontalLayout firstLayout = new HorizontalLayout();
		VerticalLayout verticalLayoutFirstPart = new VerticalLayout();
		VerticalLayout verticalLayoutSecondPart = new VerticalLayout();
		this.disableAdmissions = new Button("Disable");
		this.disableAdmissions.addStyleNames(ValoTheme.BUTTON_DANGER);
		this.disableAdmissions.addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), this.showEnrolledHelpBtn)) {
				return;
			}
			DeletePopupView deleteAction = new DeletePopupView();
			deleteAction.center();
			deleteAction.show(getUI(), event -> {
				if (!ListenerUtility.isValidSourceEvent(event.getComponent(), deleteAction.getDelete())) {
					return;
				}
				// TODO: implement apply admission page.
			});
			getUI().addWindow(deleteAction);
		});
		this.showEnrolledOutOf = new CheckBox("Show 'enrolled / out of' <br/>to students? ");
		this.showEnrolledOutOf.setCaptionAsHtml(true);
		this.showEnrolledOutOf.setResponsive(true);
		this.maxStudents = new Label();
		this.maxStudents.setCaptionAsHtml(true);
		this.maxStudents.setCaption("<b>Max Intake</b>: N/A");
		this.totalEnrolled = new Label();
		this.totalEnrolled.setCaption("<b>Enrolled</b>: N/A");
		this.totalEnrolled.setCaptionAsHtml(true);
		this.showEnrolledHelpBtn = new Button();
		this.showEnrolledHelpBtn.addStyleNames(ValoTheme.BUTTON_QUIET, ValoTheme.BUTTON_ICON_ONLY);
		this.showEnrolledHelpBtn.setIcon(VaadinIcons.QUESTION);
		this.showEnrolledHelpBtn.addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), this.showEnrolledHelpBtn)) {
				return;
			}
			getUI().addWindow(new MessagePopupView("What's this?",
					"Enabling allows students to view total number of seats that are enrolled <br/>at the time of applying for admission."));
		});
		this.courses = new ComboBox<String>();
		this.courses.setSizeFull();
		this.fees = new TextField();
		this.additionalDetails = new RichTextArea();
		this.verifyFeesReceipt = new CheckBox("Require fees using CMS?");
		this.feesHelpBtn = new Button();
		this.feesHelpBtn.setCaption("Caption");
		this.feesHelpBtn.setDescription("This is description");
		this.feesHelpBtn.setIcon(VaadinIcons.QUESTION);
		this.feesHelpBtn.addStyleNames(ValoTheme.BUTTON_QUIET, ValoTheme.BUTTON_ICON_ONLY);
		this.subjects = new ListSelect<>("Subjects");
		this.subjects.setItems("");
		this.subjects.setWidth("100%");
		this.subjects.setEnabled(false);

		semester = new ComboBox<String>();
		semester.setSizeFull();
		DateField courseYear = new DateField();
		Button addTab = new Button();
		addComponent(rootPanel);
		setComponentAlignment(rootPanel, Alignment.TOP_RIGHT);
		rootPanel.setContent(rootLayout);
		rootLayout.addComponent(firstLayout);

		HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
		horizontalSplitPanel.addComponents(verticalLayoutFirstPart, verticalLayoutSecondPart);
		horizontalSplitPanel.setSplitPosition(67.0f);
		horizontalSplitPanel.setLocked(false);

		firstLayout.addComponent(horizontalSplitPanel);
		firstLayout.setSizeFull();

		HorizontalLayout feesLayout = new HorizontalLayout(this.verifyFeesReceipt, this.feesHelpBtn);
		verticalLayoutSecondPart.addComponents(feesLayout);
		verticalLayoutSecondPart.addComponents(new HorizontalLayout(this.showEnrolledOutOf, this.showEnrolledHelpBtn),
				this.maxStudents, this.totalEnrolled);
		setSizeFull();
		setComponentAlignment(rootPanel, Alignment.MIDDLE_RIGHT);

		this.courses.setCaption("Apply for course");
		this.courses.setPlaceholder("Select course");
		this.courses.setRequiredIndicatorVisible(true);
		this.courses.setVisible(true);
		this.courses.setEnabled(true);
		this.courses.addStyleNames(ValoTheme.COMBOBOX_ALIGN_CENTER, ValoTheme.COMBOBOX_LARGE);

		verticalLayoutFirstPart.addComponent(courses);
		semester.setCaption("Select semester");
		semester.setPlaceholder("Select semester");
		semester.setRequiredIndicatorVisible(true);
		semester.setVisible(true);
		semester.setEnabled(true);
		semester.addStyleNames(ValoTheme.COMBOBOX_ALIGN_CENTER, ValoTheme.COMBOBOX_LARGE);
		verticalLayoutFirstPart.addComponent(semester);

		this.fees.addStyleNames(ValoTheme.TEXTFIELD_LARGE, ValoTheme.TEXTFIELD_INLINE_ICON);
		this.additionalDetails.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_LARGE,
				ValoTheme.TEXTFIELD_INLINE_ICON);

		this.additionalDetails.setCaptionAsHtml(true);
		this.additionalDetails.setCaption(
				"<p>Provide additional details that <br/>might be required or <br/>that can be used by student.</p>");
		this.fees.setValue("Rs. ");
		this.fees.setCaptionAsHtml(true);
		this.fees.setCaption("Provide details about fees <br/>to be shown to student.");
		this.fees.setSizeFull();

		binder.bind(this.courses, AdmissionMetaModel::getCourseName, AdmissionMetaModel::setCourseName);
		binder.bind(semester, AdmissionMetaModel::getSemester, AdmissionMetaModel::setSemester);
		binder.bind(fees, AdmissionMetaModel::getFees, AdmissionMetaModel::setFees);
		binder.bind(additionalDetails, AdmissionMetaModel::getAdditionalInformation,
				AdmissionMetaModel::setAdditionalInformation);
		binder.bind(this.verifyFeesReceipt, AdmissionMetaModel::isRequireFeesVerification,
				AdmissionMetaModel::setRequireFeesVerification);
		binder.bind(this.showEnrolledOutOf, AdmissionMetaModel::isShowEnrolledOutOf,
				AdmissionMetaModel::setShowEnrolledOutOf);

		verticalLayoutFirstPart.addComponents(this.fees, this.additionalDetails);
		verticalLayoutSecondPart.addComponent(this.subjects);
		addTab.setCaption("Save");
		addTab.addStyleNames(ValoTheme.BUTTON_PRIMARY);
		addTab.setVisible(true);
		addTab.setEnabled(false);
		HorizontalLayout actionButtonLayout = new HorizontalLayout();
		actionButtonLayout.addComponents(this.disableAdmissions, addTab);
		actionButtonLayout.setSpacing(true);
		rootLayout.addComponent(actionButtonLayout);
		rootLayout.setComponentAlignment(actionButtonLayout, Alignment.BOTTOM_RIGHT);
		// adding listeners throughout page to listen for event and execute
		// their snippet.
		this.courses.addSelectionListener(select -> {
			if (!ListenerUtility.isValidSourceEvent(select.getComponent(), courses)) {
				return;
			}
			List<String> semesters = new ArrayList<>();
			List<SubjectModel> courseSubjectModel = new ArrayList<>();
			List<String> subjectNames = new ArrayList<>();
			FactoryResponse fr = null;
			// this.subjects.setItems();
			semester.setVisible(CollectionUtils.isNotEmpty(select.getAllSelectedItems()));
			String courseName = select.getFirstSelectedItem().get();
			this.courses.setValue(courseName);

			fr = courseResponseService.findByCourseName(null, courseName);
			Utils.showFactoryResponseOnlyError(fr);
			if (Utils.isError(fr)) {
				return;
			}
			CourseModel course = (CourseModel) fr.getResponse();
			if (Utils.isNull(course)) {
				return;
			}
			this.maxStudents.setCaption(new StringBuilder().append("<b>Max Intake:</b> ")
					.append(course.getMaxStudentsAllowed()).toString());
			this.totalEnrolled.setCaption(
					new StringBuilder().append("<b>Enrolled:</b> ").append(course.getEnrolledStudents()).toString());
			if (course.getCourseName().equalsIgnoreCase(courseName)) {
				if (CollectionUtils.isEmpty(course.getSemesters())) {
					semesters.add("Sem 1");
				} else {
					semesters.addAll(course.getSemesters());
				}
				courseSubjectModel.addAll(course.getSubjects());
			}
			semester.setItems(semesters);
			if (CollectionUtils.isEmpty(courseSubjectModel)) {
				return;
			} // TODO: WE are not validating the subjects against a semester,
				// need to validate against semester.
			courseSubjectModel.forEach(subject -> {
				subjectNames.add(new StringBuilder().append(subject.getSubjectName())
						.append(subject.isOptional() ? " (Opt)" : " (Req)").toString());
			});
			this.subjects.setItems(subjectNames);

		});
		this.fees.addValueChangeListener(value -> {
			if (!ListenerUtility.isValidSourceEvent(value.getComponent(), this.fees)) {
				return;
			}
			if (!this.fees.getOptionalValue().isPresent()) {
				this.fees.setValue("Rs. ");
			} else if (!this.fees.getValue().startsWith("Rs. ")) {
				this.fees.setValue("Rs. ");
			}

			if (!StringUtils.isNumericSpace(this.fees.getValue().substring("Rs. ".length()))) {
				ElementHelper.addComponentError(this.fees, "Fees has to be in numeric value.");
				this.fees.setValue("Rs. ");
			} else {
				ElementHelper.removeComponentError(this.fees);
			}
		});
		this.feesHelpBtn.addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), this.feesHelpBtn)) {
				return;
			}
			MessagePopupView messageView = new MessagePopupView("What's this?",
					"By checking fees checkbox, <br/>CMS requires a verification or payment of fees online whichever is suitable and available <br/>before enrolling into course. ");
			getUI().addWindow(messageView);
		});
		addTab.addClickListener(click -> {

			try {
				AdmissionMetaModel model = AdmissionMetaModel.builder().build();				
				binder.writeBean(model);
				FactoryResponse fr = admissionService.saveUpdate(model);
				Utils.showFactoryResponseMsg(fr);
				binder.readBean(AdmissionMetaModel.builder().build());
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				Utils.showErrorNotification("Error while saving admission details");
			}
		});
		EmptyFieldListener<String> coursesListener = new EmptyFieldListener<String>();
		coursesListener.setSourceListField(courses);
		coursesListener.setTargetBtn(addTab);
		coursesListener.setMandatoryFields(courseYear);
		coursesListener.setMandatoryListFields(courses, semester);
		courses.addValueChangeListener(coursesListener);
		EmptyFieldListener<LocalDate> courseYearListener = new EmptyFieldListener<LocalDate>();
		courseYearListener.setSourceDateField(courseYear);
		courseYearListener.setTargetBtn(addTab);
		courseYearListener.setMandatoryDateFields(courseYear);
		courseYearListener.setMandatoryFields(courseYear);
		courseYearListener.setMandatoryListFields(courses, semester);
		courseYear.addValueChangeListener(courseYearListener);
		EmptyFieldListener<String> semesterListener = new EmptyFieldListener<String>();
		semesterListener.setSourceListField(semester);
		semesterListener.setTargetBtn(addTab);
		semesterListener.setMandatoryFields(courseYear);
		semesterListener.setMandatoryListFields(courses, semester);
		semester.addValueChangeListener(semesterListener);
		setSpacing(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			if (model != null) {
				binder.readBean(model);
			} else {
				this.uiService.setItemsCourseNames(this.courses);
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
		//binder.readBean(null);
	}

}
