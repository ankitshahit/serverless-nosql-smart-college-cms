package io.college.cms.core.examination.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.data.Binder;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.Utils;
import io.college.cms.core.examination.model.FeesModel;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.services.CoreUiService;
import io.college.cms.core.ui.util.ListenerUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class PublishFeesView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ComboBox<String> courses;
	private ComboBox<String> semester;
	private TextField fees;
	private RichTextArea contactInformation;
	private Button redirectToPay;
	private Upload upload;
	private Button seeWaysToPayOffline;
	private Button completeVerification;
	private Button cancelApplication;
	private CoreUiService coreUi;
	private Binder<FeesModel> binder;
	/**
	 * @param coreUi
	 */
	public PublishFeesView(CoreUiService coreUi) {
		super();
		this.coreUi = coreUi;
		this.courses = this.coreUi.getCoursesList();
		this.semester = this.coreUi.getSemesterList();
		this.fees = this.coreUi.getFees();
		this.contactInformation = new RichTextArea();
		this.redirectToPay = new Button();
		this.seeWaysToPayOffline = new Button();
		this.completeVerification = new Button();
		this.upload = new Upload();
		this.cancelApplication = new Button();
	}

	/**
	 * set mandatory fields for button
	 * 
	 * @param btn
	 * @param caption
	 */
	public void setBtnMandatoryFields(Button btn, String caption) {
		btn.setCaption(caption);
		btn.setVisible(true);
		btn.setEnabled(true);
		btn.setResponsive(true);
	}

	@PostConstruct
	public void paint() {
		Panel panel = new Panel();
		VerticalLayout rootLayout = new VerticalLayout();
		this.cancelApplication.setCaption("Cancel Admission");
		this.cancelApplication.setStyleName(ValoTheme.BUTTON_DANGER);
		Label admissionStatus = this.coreUi.getLabel("Admission Status: Confirmation after fees payment verification");
		this.contactInformation = VaadinWrapper.builder().caption("Contact Information").build().richTextArea();
		Label payOnline = this.coreUi.getLabel("<b>Pay Online</b>");

		Label payOffline = this.coreUi.getLabel("<b>Pay offline and upload receipt <br/>for verification</b>");

		setBtnMandatoryFields(redirectToPay, "Redirect To Pay");
		setBtnMandatoryFields(seeWaysToPayOffline, "see ways to pay offline");
		setBtnMandatoryFields(completeVerification, "Complete Verification");
		upload.setStyleName(ValoTheme.BUTTON_PRIMARY);
		seeWaysToPayOffline.setStyleName(ValoTheme.BUTTON_LINK);
		completeVerification.setStyleName(ValoTheme.BUTTON_PRIMARY);
		redirectToPay.setStyleName(ValoTheme.BUTTON_LINK);
		this.courses.setSizeFull();
		this.semester.setSizeFull();
		this.fees.setSizeFull();

		VerticalLayout leftSplit = new VerticalLayout(courses, semester, admissionStatus, fees, contactInformation);
		VerticalLayout rightSplit = new VerticalLayout(payOnline, redirectToPay, payOffline, seeWaysToPayOffline,
				upload);
		HorizontalLayout buttonHLayout = new HorizontalLayout(this.cancelApplication, this.completeVerification);
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(leftSplit, rightSplit);
		splitPanel.setSplitPosition(70.0f);
		rootLayout.addComponents(splitPanel);
		rootLayout.addComponent(buttonHLayout);
		rootLayout.setComponentAlignment(buttonHLayout, Alignment.BOTTOM_RIGHT);
		panel.setContent(rootLayout);
		addComponent(panel);
		setComponentAlignment(panel, Alignment.MIDDLE_CENTER);

		this.courses.addSelectionListener(select -> {
			if (!ListenerUtility.isValidSourceEvent(select.getComponent(), this.courses))
				return;
			if (!select.getFirstSelectedItem().isPresent()) {
				return;
			}
			this.coreUi.setItemsSemester(this.semester, Utils.val(select.getFirstSelectedItem()));
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		this.coreUi.setItemsCourseNames(this.courses);
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}
}