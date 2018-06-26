package io.college.cms.core.admission.controller;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.admission.model.ApplyAdmissionModel;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.services.CoreUiService;
import io.college.cms.core.ui.util.ListenerUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ApproveRejectAdmissionView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private CoreUiService coreUi;

	private Grid<ApplyAdmissionModel> gridAdmissionModel;
	private ComboBox<String> courseNamesCb;

	/**
	 * @param coreUi
	 */
	@Autowired
	public ApproveRejectAdmissionView(CoreUiService coreUi) {
		super();
		this.coreUi = coreUi;
	}

	@PostConstruct
	protected void paint() {
		this.courseNamesCb = coreUi.getCoursesList();
		this.gridAdmissionModel = new Grid<>();
		this.gridAdmissionModel.setItems(ApplyAdmissionModel.builder().courseName("Sample course")
				.username("Ankit shah it").withRoleMember("Ankit").rejected(false)
				.feesVerificationReceiptRequired(false).appliedOn(LocalDate.now()).build());
		this.gridAdmissionModel.addColumn(ApplyAdmissionModel::getUsername).setCaption("Student username");
		this.gridAdmissionModel.addColumn(ApplyAdmissionModel::getAppliedOn).setCaption("Applied on");
		this.gridAdmissionModel.addColumn(ApplyAdmissionModel::isFeesVerficationDone)
				.setCaption("Fees verification done?");
		this.gridAdmissionModel.addColumn(ApplyAdmissionModel::isFeesVerificationReceiptRequired)
				.setCaption("Fees verification required?");
		this.gridAdmissionModel.addColumn(ApplyAdmissionModel::isRejected).setCaption("Rejected?");
		this.gridAdmissionModel.addColumn(ApplyAdmissionModel::isApproved).setCaption("Approved?");
		this.gridAdmissionModel.addColumn(ApplyAdmissionModel::getComments).setCaption("Comments");
		this.gridAdmissionModel.setSizeFull();
		Panel panel = new Panel();
		panel.setSizeFull();
		panel.setContent(this.gridAdmissionModel);
		addComponent(panel);
		setSizeFull();
		this.gridAdmissionModel.addItemClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getSource(), this.gridAdmissionModel)) {
				return;
			}
			if (click.getItem() == null) {
				return;
			}
			Window window = new Window();
			window.setResizable(false);
			window.center();
			VerticalLayout layout = new VerticalLayout();
			Label username = coreUi.getLabel("Student username");
			username.setValue(click.getItem().getUsername());
			Label courseNameLbl = coreUi.getLabel("Course name");
			courseNameLbl.setValue(click.getItem().getCourseName());
			courseNameLbl.setSizeFull();
			RadioButtonGroup<String> radioOption = new RadioButtonGroup<String>();
			radioOption.setCaption("<b>Application status</b>");
			radioOption.setCaptionAsHtml(true);
			radioOption.setItems("Approve", "Reject");
			RichTextArea textArea = VaadinWrapper.builder().caption("Comments").required(false).build().richTextArea();
			Button button = VaadinWrapper.builder().enabled(false).build().button();
			button.setCaption("Save");
			radioOption.addValueChangeListener(value -> {
				if (!ListenerUtility.isValidSourceEvent(value.getComponent(), radioOption)) {
					return;
				}
				button.setEnabled(radioOption.getOptionalValue().isPresent());
			});
			Button cancel = VaadinWrapper.builder().build().button();
			cancel.setCaption("Close");
			cancel.setStyleName(ValoTheme.BUTTON_QUIET);
			cancel.addClickListener(close -> {
				if (!ListenerUtility.isValidSourceEvent(close.getComponent(), cancel)) {
					return;
				}
				window.close();
			});
			HorizontalLayout buttonLayout = new HorizontalLayout(cancel, button);
			layout.addComponents(courseNameLbl, username, radioOption, textArea, buttonLayout);
			layout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);

			window.setContent(new Panel(layout));
			getUI().addWindow(window);
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		coreUi.setItemsCourseNames(this.courseNamesCb);
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

}
