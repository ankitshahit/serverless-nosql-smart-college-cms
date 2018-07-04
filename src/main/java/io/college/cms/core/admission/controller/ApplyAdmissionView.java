package io.college.cms.core.admission.controller;

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
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.admission.model.AdmissionMetaModel;
import io.college.cms.core.admission.model.ApplyAdmissionModel;
import io.college.cms.core.admission.services.AdmissionResponseService;
import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.application.Utils;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.db.CourseModel.SubjectModel;
import io.college.cms.core.courses.service.CourseResponseService;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.services.CoreUiService;
import io.college.cms.core.user.service.SecurityService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ApplyAdmissionView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	protected CoreUiService coreUiService;
	private ApplyAdmissionService applyAdmissionService;
	private AdmissionResponseService admissionResponseService;
	private SecurityService securityService;
	private CourseResponseService courseResponseService;

	/**
	 * @param coreUiService
	 * @param applyAdmissionService
	 * @param admissionResponseService
	 * @param securityService
	 * @param courseResponseService
	 */
	@Autowired
	public ApplyAdmissionView(CoreUiService coreUiService, AdmissionResponseService admissionResponseService,
			SecurityService securityService, CourseResponseService courseResponseService) {
		super();
		this.coreUiService = coreUiService;
		this.applyAdmissionService = new ApplyAdmissionService(this.coreUiService);
		this.admissionResponseService = admissionResponseService;
		this.securityService = securityService;
		this.courseResponseService = courseResponseService;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			FactoryResponse fr = admissionResponseService.findMetaAdmissions();
			Utils.showFactoryResponseOnlyError(fr);
			if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
				return;
			}
			List<AdmissionMetaModel> admissionMetaModel = (List<AdmissionMetaModel>) fr.getResponse();

			List<String> courseNames = new ArrayList<>();
			for (AdmissionMetaModel meta : admissionMetaModel) {
				courseNames.add(meta.getCourseName());
			}
			this.applyAdmissionService.dto.courses.setItems(courseNames);
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

	@PostConstruct
	protected void paint() {
		this.applyAdmissionService.dto.courses.addSelectionListener(select -> {
			if (!select.getFirstSelectedItem().isPresent()) {
				return;
			}
			String courseName = select.getFirstSelectedItem().get();
			FactoryResponse fr = admissionResponseService.findAdmissionMetaDetails(courseName);
			Utils.showFactoryResponseOnlyError(fr);
			if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
				return;
			}
			AdmissionMetaModel admissionMetaModel = (AdmissionMetaModel) fr.getResponse();
			this.applyAdmissionService.dto.fees.setValue(admissionMetaModel.getFees());
			this.applyAdmissionService.dto.semester.setCaption("");
			this.applyAdmissionService.dto.semester.setValue("" + admissionMetaModel.getSemester());
			this.applyAdmissionService.dto.verifyFeesReceipt.setValue(admissionMetaModel.isRequireFeesVerification()
					? "Verification of fees required!" : "No fees receipt verification required.");

			this.applyAdmissionService.dto.additionalDetails.setCaption("<b>Additional information</b>");
			this.applyAdmissionService.dto.additionalDetails.setValue(admissionMetaModel.getAdditionalInformation());
			fr = courseResponseService.findByCourseName(null, courseName);
			Utils.showFactoryResponseOnlyError(fr);
			if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
				return;
			}
			CourseModel courseModel = (CourseModel) fr.getResponse();
			if (admissionMetaModel.isShowEnrolledOutOf()) {
				this.applyAdmissionService.dto.maxStudents.setCaption("Max students");
				this.applyAdmissionService.dto.maxStudents
						.setValue(String.valueOf(courseModel.getMaxStudentsAllowed()));
				this.applyAdmissionService.dto.totalEnrolled.setCaption("Enrolled students");
				this.applyAdmissionService.dto.totalEnrolled
						.setValue(String.valueOf(courseModel.getEnrolledStudents()));
			}
			List<SubjectModel> subjects = courseModel.getSubjects();
			List<String> subjectNames = new ArrayList<>();
			List<String> requiredSubjects = new ArrayList<>();
			subjects.forEach(subject -> {
				if (!subject.isOptional()) {
					requiredSubjects.add(new StringBuilder().append(subject.getSubjectName()).append("").toString());
				}
				subjectNames.add(new StringBuilder().append(subject.getSubjectName()).append("").toString());
			});
			this.applyAdmissionService.dto.subjects.setItems(subjectNames);

			this.applyAdmissionService.dto.subjects.setItemEnabledProvider(item -> requiredSubjects.contains(item));

		});
		this.applyAdmissionService.dto.apply.addClickListener(click -> {
			String courseName = this.applyAdmissionService.dto.courses.getSelectedItem().get();
			String username = securityService.getPrincipal();
			List<String> subjects = new ArrayList<>();
			subjects.addAll(this.applyAdmissionService.dto.subjects.getSelectedItems());
			String semester = this.applyAdmissionService.dto.getSemester().getValue();
			FactoryResponse fr = admissionResponseService.findAdmissionMetaDetails(courseName);
			Utils.showFactoryResponseOnlyError(fr);
			if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
				return;
			}
			AdmissionMetaModel admissionMetaModel = (AdmissionMetaModel) fr.getResponse();
			ApplyAdmissionModel.ApplyAdmissionModelBuilder applyModel = ApplyAdmissionModel.builder();
			fr = admissionResponseService.saveUpdate(applyModel.roleMembers(admissionMetaModel.getUsers())
					.username(username).courseName(courseName).semester(semester).subjects(subjects).build());
			Utils.showFactoryResponseMsg(fr);
		});
		addComponent(this.applyAdmissionService.dto.rootPanel);
	}

	@Data
	private class ApplyAdmissionService {
		public static final float SPLIT_POSITION = 67.0F;
		private ApplyAdmissionDTO dto;
		private CoreUiService ui;

		public ApplyAdmissionService(CoreUiService coreService) {
			this.dto = ApplyAdmissionDTO.builder().build();
			this.ui = coreService;
			this.dto.fees = this.ui.getLabel();
			this.dto.additionalDetails = this.ui.getLabel();
			this.dto.maxStudents = this.ui.getLabel();
			this.dto.totalEnrolled = this.ui.getLabel();
			this.dto.courses = this.ui.getCoursesList();
			this.dto.semester = this.ui.getLabel();

			initUI();
		}

		void initUI() {
			this.dto.subjects.setItems("No subject");
			this.dto.fees.setValue("<p><b>Fees</b>: Rs. 0/-</p>");
			// TODO: need to convert these long help text as a help button on
			// screen, due to them not fitting on page.
			this.dto.additionalDetails.setValue(
					"<p><b>Additional information</b>: <br/><ul><li>SSC</li><li>HSC</li></ul> documents are required <br/>to be made avaialble <br/>in <i>My documents</i></p>");
			this.dto.apply.setCaption("Apply admission");
			this.dto.maxStudents.setValue("<p><b>Max intake</b>: N/A</p>");
			this.dto.totalEnrolled.setValue("<p><b>Enrolled</b>: N/A</p>");
			this.dto.semester.setValue("<p><b>Select course to view semester</b></p>");
			addComponents();
			initStyle();
			addListeners();
		}

		void initStyle() {
			this.dto.apply.addStyleNames(ValoTheme.BUTTON_PRIMARY);
			this.dto.courses.setSizeFull();
		}

		void addComponents() {
			VerticalLayout verticalLayout = new VerticalLayout();
			VerticalLayout firstVLayout = new VerticalLayout();
			VerticalLayout secondVLayout = new VerticalLayout();
			dto.verifyFeesReceipt = VaadinWrapper.builder().caption("Verification of fees required?").build().label();
			Label label = this.ui.getLabel();
			label.setValue(
					"<p><b>Note</b>: You'd receive notification <br/> of action performed in <br/> '<i>My notification</i>'.  <br/>It maybe required to apply <br/>'fees verification' based <br/>on information provided above. </p>");
			firstVLayout.addComponents(this.dto.courses, this.dto.semester, this.dto.subjects);
			secondVLayout.addComponents(this.dto.fees, dto.verifyFeesReceipt, this.dto.additionalDetails,
					this.dto.maxStudents, this.dto.totalEnrolled, label);
			HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(firstVLayout, secondVLayout);
			splitPanel.setSplitPosition(SPLIT_POSITION);

			verticalLayout.addComponents(splitPanel, this.dto.apply);
			verticalLayout.setComponentAlignment(this.dto.apply, Alignment.BOTTOM_RIGHT);
			this.dto.rootPanel.setContent(verticalLayout);
		}

		void addListeners() {
			this.dto.courses.addSelectionListener(select -> {

			});
		}

	}

	@Builder
	@Data
	protected static class ApplyAdmissionDTO {

		private Label semester;
		@Builder.Default
		private Panel rootPanel = new Panel();
		@Builder.Default
		private Label fees = new Label();
		@Builder.Default
		private Label additionalDetails = new Label();
		@Builder.Default
		private Label verifyFeesReceipt = new Label();
		@Builder.Default
		private Button feesHelpBtn = new Button();
		@Builder.Default
		private ComboBox<String> courses = new ComboBox<>();
		@Builder.Default
		private CheckBox showEnrolledOutOf = new CheckBox();
		@Builder.Default
		private Button showEnrolledHelpBtn = new Button();
		@Builder.Default
		private Label maxStudents = new Label();
		@Builder.Default
		private Label totalEnrolled = new Label();
		@Builder.Default
		private Button disableAdmissions = new Button();
		@Builder.Default
		private CheckBoxGroup<String> subjects = new CheckBoxGroup<>();
		@Builder.Default
		private Button apply = new Button();
	}
}
