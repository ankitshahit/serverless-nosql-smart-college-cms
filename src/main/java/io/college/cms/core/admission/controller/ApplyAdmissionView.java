package io.college.cms.core.admission.controller;

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
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.ui.services.CoreUiService;
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

	@PostConstruct
	protected void paint() {
		addComponent(this.applyAdmissionService.dto.rootPanel);
	}

	/**
	 * @param coreUiService
	 */
	@Autowired
	public ApplyAdmissionView(CoreUiService coreUiService) {
		super();
		this.coreUiService = coreUiService;
		this.applyAdmissionService = new ApplyAdmissionService(this.coreUiService);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			this.coreUiService.setItemsCourseNames(this.applyAdmissionService.dto.courses);
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
			Label label = this.ui.getLabel();
			label.setValue(
					"<p><b>Note</b>: You'd receive notification <br/> of action performed in <br/> '<i>My notification</i>'.  <br/>It maybe required to apply <br/>'fees verification' based <br/>on information provided above. </p>");
			firstVLayout.addComponents(this.dto.courses, this.dto.semester, this.dto.subjects);
			secondVLayout.addComponents(this.dto.fees, this.dto.additionalDetails, this.dto.maxStudents,
					this.dto.totalEnrolled, label);
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
