package io.college.cms.core.examination.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class DownloadQrExamView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ComboBox<String> selectExam = new ComboBox<String>();
	private ComboBox<String> selectSubject = new ComboBox<String>();
	private RadioButtonGroup<String> examType = new RadioButtonGroup<>();
	private ComboBox<String> selectByStudent = new ComboBox<String>();
	private CheckBox allStudents = new CheckBox("All Students");
	private Button clear = new Button();
	private Button downloadQr = new Button();

	public void setCbMandatoryField(ComboBox<String> cb, String caption, String placeholder) {
		cb.setCaption(caption);
		cb.setPlaceholder(placeholder);
		cb.setVisible(true);
		cb.setEnabled(true);
		cb.setSizeFull();
		cb.addStyleNames(ValoTheme.COMBOBOX_LARGE, ValoTheme.COMBOBOX_ALIGN_CENTER);
	}

	@PostConstruct
	public void paint() {
		Panel panel = new Panel();
		VerticalLayout rootLayout = new VerticalLayout();
		addComponent(panel);
		setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
		panel.setContent(rootLayout);

		Label downloadQrExam = new Label();
		downloadQrExam.setValue("<h1><center>Download Exam Qr</center></h1>");
		downloadQrExam.setContentMode(ContentMode.HTML);
		downloadQrExam.setCaptionAsHtml(true);

		setCbMandatoryField(selectExam, "Select Exam", "exam..");
		setCbMandatoryField(selectSubject, "Select Subject", "subject..");
		Panel panel2 = new Panel();
		examType.setCaption("Exam Type");
		examType.setItems("theory", "practical", "internal", "others");
		panel2.setContent(examType);
		VerticalLayout leftSplit = new VerticalLayout(downloadQrExam, selectExam, selectSubject, panel2);

		Label or = new Label();
		or.setValue("<i><center>OR</center></i>");
		or.setContentMode(ContentMode.HTML);
		or.setCaptionAsHtml(true);

		setCbMandatoryField(selectByStudent, "Select Student", "student..");

		VerticalLayout rightSplit = new VerticalLayout(allStudents, or, selectByStudent);

		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(new Panel(leftSplit), new Panel(rightSplit));
		splitPanel.setSplitPosition(62.0f);

		downloadQr.setCaption("Download Qr");
		downloadQr.setStyleName(ValoTheme.BUTTON_PRIMARY);
		downloadQr.setVisible(true);
		downloadQr.setEnabled(true);
		downloadQr.setResponsive(true);
		clear.setCaption("Clear");
		clear.setStyleName(ValoTheme.BUTTON_DANGER);
		clear.setVisible(true);
		clear.setEnabled(true);
		clear.setResponsive(true);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.addComponents(clear, downloadQr);

		rootLayout.addComponents(splitPanel);
		rootLayout.addComponent(btnLayout);
		rootLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_RIGHT);
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