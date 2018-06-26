package io.college.cms.core.attendance.controller;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.navigator.View;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.util.ListenerUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class TagAttendanceView extends VerticalLayout implements View {
	private static final long serialVersionUID = 1L;
	private Panel rootPanel;
	private VerticalLayout rootLayout;
	private HorizontalSplitPanel splitPanel;
	private VerticalLayout firstLayout;
	private ComboBox<String> courseNamesCb;
	private ComboBox<String> semesterCb;
	private ComboBox<String> subjectNamesCb;
	private DateField attendance;
	private Label totalStudentCb;
	private VerticalLayout secondLayout;
	private ComboBox<String> usersCb;
	private Button saveBtn;
	private Upload upload;
	private ListSelect<String> studentsUsernameList;
	private Button removeStudentUsernameBtn;

	@PostConstruct
	public void paint() {
		this.rootPanel = new Panel();
		this.rootLayout = new VerticalLayout();
		this.splitPanel = new HorizontalSplitPanel();
		this.firstLayout = new VerticalLayout();
		this.courseNamesCb = new ComboBox<String>();
		this.semesterCb = new ComboBox<String>();
		this.subjectNamesCb = new ComboBox<String>();
		this.attendance = new DateField();
		this.secondLayout = new VerticalLayout();
		this.usersCb = new ComboBox<String>();
		this.totalStudentCb = new Label();
		this.saveBtn = new Button();
		this.upload = new Upload();
		this.studentsUsernameList = new ListSelect<>("Added students");
		this.studentsUsernameList.setSizeFull();
		this.studentsUsernameList.setItems("user 1");
		this.courseNamesCb = (ComboBox<String>) VaadinWrapper.builder().caption("Course name")
				.placeholder("search by course").required(true).visible(true).enabled(true).build().comboBox();
		this.semesterCb = (ComboBox<String>) VaadinWrapper.builder().caption("Semester")
				.placeholder("search by semester").required(true).visible(true).enabled(true).build().comboBox();
		this.subjectNamesCb = (ComboBox<String>) VaadinWrapper.builder().caption("subject name")
				.placeholder("search by subject").required(true).visible(true).enabled(true).build().comboBox();

		this.attendance = VaadinWrapper.builder().caption("Attendance date").required(true).visible(true).enabled(true)
				.build().dateField();
		this.usersCb = (ComboBox<String>) VaadinWrapper.builder().caption("Missing student?")
				.placeholder("search by name").required(false).visible(true).enabled(true).build().comboBox();

		this.totalStudentCb = VaadinWrapper.builder().caption("Total students count").required(true).visible(true)
				.enabled(true).build().label();

		this.saveBtn = VaadinWrapper.builder().caption("Save settings").required(true).visible(true).enabled(false)
				.build().button();

		this.removeStudentUsernameBtn = new Button("Delete");
		this.removeStudentUsernameBtn.setVisible(false);
		this.removeStudentUsernameBtn.addStyleName(ValoTheme.BUTTON_DANGER);

		this.upload.setCaption("Upload image");
		this.upload.setCaptionAsHtml(true);
		this.upload.setButtonCaption("select file ...");
		this.upload.setImmediateMode(true);
		this.upload.setSizeFull();
		this.saveBtn.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
		this.saveBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
		this.saveBtn.setCaption("Save attendance");
		firstLayout.addComponent(courseNamesCb);
		firstLayout.addComponent(semesterCb);
		firstLayout.addComponent(subjectNamesCb);
		firstLayout.addComponent(attendance);
		Panel firstPanel = new Panel(firstLayout);
		firstPanel.setSizeFull();
		splitPanel.addComponent(firstPanel);	
		splitPanel.addComponent(new Panel(secondLayout));
		splitPanel.setSplitPosition(30.0f);

		secondLayout.addComponents(this.upload, usersCb, totalStudentCb, this.studentsUsernameList,
				this.removeStudentUsernameBtn);

		rootLayout.addComponents(splitPanel, saveBtn);
		rootLayout.setComponentAlignment(this.saveBtn, Alignment.BOTTOM_RIGHT);
		rootPanel.setContent(rootLayout);
		addComponent(rootPanel);

		EmptyFieldListener<String> courseNamesCbListener = new EmptyFieldListener<String>();
		courseNamesCbListener.setSourceListField(courseNamesCb);
		courseNamesCbListener.setTargetBtn(saveBtn);
		courseNamesCbListener.setMandatoryDateFields(attendance);
		courseNamesCbListener.setMandatoryListFields(courseNamesCb, semesterCb, subjectNamesCb, usersCb);
		courseNamesCb.addValueChangeListener(courseNamesCbListener);
		EmptyFieldListener<String> subjectNamesCbListener = new EmptyFieldListener<String>();
		subjectNamesCbListener.setSourceListField(subjectNamesCb);
		subjectNamesCbListener.setTargetBtn(saveBtn);
		subjectNamesCbListener.setMandatoryListFields(courseNamesCb, semesterCb, subjectNamesCb, usersCb);
		subjectNamesCb.addValueChangeListener(subjectNamesCbListener);
		EmptyFieldListener<String> semesterCbListener = new EmptyFieldListener<String>();
		semesterCbListener.setSourceListField(semesterCb);
		semesterCbListener.setTargetBtn(saveBtn);
		semesterCbListener.setMandatoryListFields(courseNamesCb, semesterCb, subjectNamesCb, usersCb);
		semesterCb.addValueChangeListener(semesterCbListener);
		EmptyFieldListener<String> usersCbListener = new EmptyFieldListener<String>();
		usersCbListener.setSourceListField(usersCb);
		usersCbListener.setTargetBtn(saveBtn);
		usersCbListener.setMandatoryListFields(courseNamesCb, semesterCb, subjectNamesCb, usersCb);
		usersCb.addValueChangeListener(usersCbListener);
		EmptyFieldListener<LocalDate> attendanceListener = new EmptyFieldListener<LocalDate>();
		attendanceListener.setSourceDateField(attendance);
		attendanceListener.setTargetBtn(saveBtn);
		attendanceListener.setMandatoryDateFields(attendance);
		attendanceListener.setMandatoryListFields(courseNamesCb, semesterCb, subjectNamesCb, usersCb);
		attendance.addValueChangeListener(attendanceListener);
		this.studentsUsernameList.addSelectionListener(value -> {
			if (!ListenerUtility.isValidSourceEvent(value.getComponent(), this.studentsUsernameList)) {
				return;
			}
			this.removeStudentUsernameBtn.setVisible(CollectionUtils.isNotEmpty(value.getAllSelectedItems()));
		});
		this.removeStudentUsernameBtn.addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), this.removeStudentUsernameBtn)) {
				return;
			}
			if (CollectionUtils.isEmpty(this.studentsUsernameList.getSelectedItems())) {
				this.removeStudentUsernameBtn.setVisible(false);
			}
			// TODO: NEEd to get all of the items made available in listselect
			// and remove items that are available in select list
			for (String value : this.studentsUsernameList.getSelectedItems()) {
				// TODO: remove operation
			}
		});
	}

}
