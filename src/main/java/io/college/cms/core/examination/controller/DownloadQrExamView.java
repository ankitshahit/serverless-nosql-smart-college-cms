package io.college.cms.core.examination.controller;

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
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.application.Utils;
import io.college.cms.core.courses.controller.constants.SubjectType;
import io.college.cms.core.examination.model.ExaminationModel;
import io.college.cms.core.examination.service.ExamResponseService;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
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
	private ProgressBar progressBar;
	private ExamResponseService examResponseService;
	private Window progressWindow;

	@Autowired
	public DownloadQrExamView(ExamResponseService examResponseService) {
		super();
		this.examResponseService = examResponseService;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			FactoryResponse fr = examResponseService.findAllExams();
			if (Utils.isError(fr)) {
				return;
			}
			List<ExaminationModel> examModels = (List<ExaminationModel>) fr.getResponse();
			if (CollectionUtils.isEmpty(examModels)) {
				return;
			}
			List<String> examNames = new ArrayList<>();

			examModels.forEach(exam -> {
				examNames.add(exam.getExamName());
			});
			this.selectExam.setItems(examNames);

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
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
	public void paint() {
		progressWindow = new Window();
		progressBar = new ProgressBar();
		progressBar.setVisible(false);
		progressWindow.center();
		progressWindow.setResizable(false);
		progressWindow.setClosable(false);
		VerticalLayout progressLayout = new VerticalLayout();
		progressLayout.addComponent(progressBar);
		progressWindow.setContent(progressLayout);
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
		examType.setItems(SubjectType.THEORY.toString(), SubjectType.PRACTICAL.toString(),
				SubjectType.INTERNAL.toString(), SubjectType.OTHER.toString());
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

		downloadQr.addClickListener(click -> {
			getUI().addWindow(progressWindow);
			if (this.selectByStudent.getOptionalValue().isPresent()) {

				examResponseService.qr(selectExam.getOptionalValue().get(), selectSubject.getOptionalValue().get(),
						examType.getOptionalValue().get(), "download.pdf",
						Utils.val(this.selectByStudent.getOptionalValue()), this::fileDownloadBtn,
						this::progressListener, this::successListener);
			} else {
				examResponseService.qr(selectExam.getOptionalValue().get(), selectSubject.getOptionalValue().get(),
						examType.getOptionalValue().get(), "download.pdf", "", this::fileDownloadBtn,
						this::progressListener, this::successListener);
			}
		});
		EmptyFieldListener<String> exam = getEmptyFieldListener();
		EmptyFieldListener<String> subject = getEmptyFieldListener();
		EmptyFieldListener<String> examType = getEmptyFieldListener();
		this.selectExam.addValueChangeListener(exam);
		this.selectSubject.addValueChangeListener(subject);
		this.examType.addValueChangeListener(examType);
		this.downloadQr.setEnabled(false);
		this.allStudents.setValue(true);
		this.allStudents.addValueChangeListener(value -> {
			this.downloadQr.setEnabled((value.getValue() || this.selectByStudent.getOptionalValue().isPresent())
					&& this.selectExam.getOptionalValue().isPresent()
					&& this.selectSubject.getOptionalValue().isPresent()
					&& this.examType.getOptionalValue().isPresent());
		});
		this.selectExam.addSelectionListener(select -> {
			if (!select.getFirstSelectedItem().isPresent()) {
				return;
			}
			List<ExaminationModel.ExamSubject> examSubjectsModel = new ArrayList<>();
			FactoryResponse fr = examResponseService.getExamByExamId(null, this.selectExam.getOptionalValue().get());
			Utils.showFactoryResponseOnlyError(fr);
			if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
				return;
			}
			ExaminationModel model = (ExaminationModel) fr.getResponse();
			examSubjectsModel = model.getExamSubjects();
			if (CollectionUtils.isEmpty(examSubjectsModel)) {
				return;
			}
			List<String> subjectNames = new ArrayList<>();
			examSubjectsModel.forEach(subjectModel -> {
				subjectNames.add(subjectModel.getSubjectName());
			});
			this.selectSubject.setItems(subjectNames);
		});
	}

	private void fileDownloadBtn(FileResource fileResource) {
		getUI().access(() -> {
			FileDownloader fileDownloader = new FileDownloader(fileResource);
			fileDownloader.extend(downloadQr);
		});
	}

	private void progressListener() {
		getUI().access(() -> {
			this.progressBar.setVisible(true);
			this.progressBar.setEnabled(true);
			this.progressBar.setIndeterminate(true);

		});
	}

	private void successListener() {
		getUI().access(() -> {
			this.progressBar.setEnabled(false);
			this.progressBar.setVisible(false);
			Notification.show("Processing completed! Starting file download");
			progressWindow.close();
		});
		// seems this will break due to being accessed from different thread.

	}

	public void setCbMandatoryField(ComboBox<String> cb, String caption, String placeholder) {
		cb = (ComboBox<String>) VaadinWrapper.builder().caption(caption).placeholder(placeholder).build().comboBox();
	}

	private EmptyFieldListener<String> getEmptyFieldListener() {
		EmptyFieldListener<String> emptyFieldListener = new EmptyFieldListener<>();
		emptyFieldListener.setMandatoryListFields(this.selectExam, this.selectSubject, this.examType);
		emptyFieldListener.setTargetBtn(this.downloadQr);
		return emptyFieldListener;
	}

}