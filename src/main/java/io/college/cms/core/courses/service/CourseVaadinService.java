package io.college.cms.core.courses.service;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.ui.builder.ButtonWrapper;
import io.college.cms.core.ui.builder.TextFieldWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.model.CourseDTO;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CourseVaadinService {
	public CourseDTO courseMetaDataStep1() {
		CourseDTO courseDTO = CourseDTO.builder()
				.courseName(TextFieldWrapper.builder().caption("Course")
						.description("Provide unique coursename that represents to actual available streams")
						.icon(VaadinIcons.DIPLOMA).build().textField())
				.build();
		courseDTO.getCourseName().setResponsive(true);
		courseDTO.getCourseName().focus();
		courseDTO.setMaxStudents(TextFieldWrapper.builder().caption("Max number of seats")
				.description("Provide total number of students that can be enrolled into.").build().textField());
		courseDTO.getMaxStudents().setResponsive(true);
		CheckBox archive = new CheckBox();
		archive.setIcon(VaadinIcons.ARCHIVES);
		archive.setCaption("Archive?");
		courseDTO.setIsArchive(archive);
		archive.setResponsive(true);
		RichTextArea area = new RichTextArea();
		area.setCaption("Course Description");
		area.setIcon(VaadinIcons.INFO);
		area.setRequiredIndicatorVisible(true);
		area.setResponsive(true);
		courseDTO.setCourseDescription(area);
		courseDTO.getCourseDescription().setResponsive(true);
		Label label = new Label("== Attributes ==");
		courseDTO.setAttributesSeperator(label);
		courseDTO.getAttributesSeperator().setResponsive(true);
		courseDTO.setSaveCourse(ButtonWrapper.builder().caption("Save & Next").build().button());
		courseDTO.getSaveCourse().setResponsive(true);
		courseDTO.setReset(ButtonWrapper.builder().caption("Delete").build().button());
		courseDTO.getReset().setResponsive(true);
		courseDTO.getReset().setStyleName(ValoTheme.BUTTON_DANGER);
		courseDTO.getSaveCourse().setStyleName(ValoTheme.BUTTON_QUIET);
		courseDTO.getSaveCourse().setResponsive(true);
		courseDTO.setTotalSem(TextFieldWrapper.builder().caption("Total semesters").required(true).build().textField());
		return courseDTO;
	}

	public VerticalLayout buildCoursePageOne(CourseDTO courseDTO) {
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.addComponent(courseDTO.getReset());
		hLayout.addComponent(courseDTO.getSaveCourse());
		hLayout.setResponsive(true);
		HorizontalLayout groupedSeatsAndSem = new HorizontalLayout(courseDTO.getMaxStudents(), courseDTO.getTotalSem());
		groupedSeatsAndSem.setResponsive(true);
		groupedSeatsAndSem.setSizeFull();
		groupedSeatsAndSem.setComponentAlignment(courseDTO.getMaxStudents(), Alignment.BOTTOM_LEFT);
		groupedSeatsAndSem.setComponentAlignment(courseDTO.getTotalSem(), Alignment.BOTTOM_RIGHT);
		VerticalLayout courseLayout = new VerticalLayout();
		courseLayout.setResponsive(true);
		courseLayout.addComponents(courseDTO.getCourseName(), courseDTO.getCourseDescription(),
				courseDTO.getAttributesSeperator(), groupedSeatsAndSem, courseDTO.getIsArchive(), hLayout);
		courseLayout.setComponentAlignment(hLayout, Alignment.BOTTOM_RIGHT);
		courseLayout.setComponentAlignment(courseDTO.getAttributesSeperator(), Alignment.MIDDLE_CENTER);
		courseLayout.setSizeFull();
		return courseLayout;
	}

	public void attachEmptyValueListenerCourseStep1(CourseDTO dto) {
		EmptyFieldListener<String> courseNameListener = new EmptyFieldListener<>(dto.getCourseName());
		courseNameListener.setMandatoryFields(dto.getCourseName(), dto.getCourseDescription(), dto.getMaxStudents());
		courseNameListener.setTargetBtn(dto.getSaveCourse());
		dto.getCourseName().addValueChangeListener(courseNameListener);

		EmptyFieldListener<String> courseDescription = new EmptyFieldListener<>(dto.getCourseDescription());
		courseDescription.setMandatoryFields(dto.getCourseName(), dto.getCourseDescription(), dto.getMaxStudents());
		courseDescription.setTargetBtn(dto.getSaveCourse());
		dto.getCourseDescription().addValueChangeListener(courseDescription);

		EmptyFieldListener<String> maxStudents = new EmptyFieldListener<>(dto.getMaxStudents());
		maxStudents.setMandatoryFields(dto.getCourseName(), dto.getCourseDescription(), dto.getMaxStudents());
		maxStudents.setTargetBtn(dto.getSaveCourse());
		dto.getMaxStudents().addValueChangeListener(maxStudents);
	}

	public CourseDTO courseMetadataStep2() {
		CourseDTO dto = CourseDTO.builder()
				.subjectName(TextFieldWrapper.builder().caption("Subject name")
						.description("Provide unique subject that represents to actual available subjects")
						.icon(VaadinIcons.DIPLOMA).build().textField())
				.build();
		dto.getSubjectName().setResponsive(true);
		CheckBoxGroup<String> cbg = new CheckBoxGroup<String>();
		cbg.setCaption("Attributes: ");
		cbg.setItems("Theory", "Practical", "Internal", "Others");
		dto.setSubjectAttributes(cbg);
		dto.setTheoryMarks(TextFieldWrapper.builder().caption("Total available marks ~ Theory").required(false)
				.visible(false).icon(VaadinIcons.ADJUST).build().textField());

		cbg.setResponsive(true);
		dto.setPracticalMarks(TextFieldWrapper.builder().caption("Total available marks ~ Practical").required(false)
				.visible(false).icon(VaadinIcons.ADJUST).style(ValoTheme.TEXTFIELD_TINY).build().textField());
		dto.getPracticalMarks().setResponsive(true);
		dto.setInternalMarks(TextFieldWrapper.builder().caption("Total available marks ~ Internal").required(false)
				.visible(false).build().textField());
		dto.getInternalMarks().setResponsive(true);
		dto.setOtherMarks(TextFieldWrapper.builder().caption("Total available marks ~ Others").required(false)
				.visible(false).build().textField());
		dto.getOtherMarks().setResponsive(true);
		dto.setTheoryPassMarks(TextFieldWrapper.builder().caption("Required passing marks ~ Theory").required(false)
				.visible(false).icon(VaadinIcons.ADJUST).build().textField());
		dto.getTheoryPassMarks().setResponsive(true);
		dto.setPracticalPassMarks(TextFieldWrapper.builder().caption("Required passing marks ~ Practical")
				.required(false).visible(false).icon(VaadinIcons.ADJUST).build().textField());
		dto.getPracticalPassMarks().setResponsive(true);
		dto.setInternalPassMarks(TextFieldWrapper.builder().caption("Required passing marks ~ Internal").required(false)
				.visible(false).build().textField());
		dto.getInternalPassMarks().setResponsive(true);
		dto.setOtherPassMarks(TextFieldWrapper.builder().caption("Required passing marks ~ Others").required(false)
				.visible(false).build().textField());
		dto.getOtherPassMarks().setResponsive(true);
		ListSelect<String> subjects = new ListSelect<>();
		subjects.setResponsive(true);
		subjects.setCaption("Subjects added: ");
		dto.setAddedSubjects(subjects);
		dto.getAddedSubjects().setResponsive(true);

		CheckBox optional = new CheckBox();
		optional.setCaption("Is subject optional?");
		dto.setOptional(optional);
		dto.setSaveCourse(ButtonWrapper.builder().caption("Save & Next").build().button());
		dto.setReset(ButtonWrapper.builder().caption("Reset").build().button());
		dto.getReset().setStyleName(ValoTheme.BUTTON_DANGER);
		dto.getSaveCourse().setStyleName(ValoTheme.BUTTON_QUIET);
		dto.getOptional().setResponsive(true);
		dto.getSaveCourse().setResponsive(true);
		dto.getReset().setResponsive(true);
		return dto;
	}

	public VerticalLayout buildCoursePageTwo(CourseDTO dto) {
		// screen will be divided into two parts,
		// first part will contain adding subjects and second part will contain
		// all the subjects it has till now.
		HorizontalLayout mainLayout = new HorizontalLayout();
		Panel firstPart = new Panel();
		Panel secondPart = new Panel();
		mainLayout.setResponsive(true);
		firstPart.setResponsive(true);
		secondPart.setResponsive(true);

		VerticalLayout firstPanelLayout = new VerticalLayout();
		firstPanelLayout.setResponsive(true);
		HorizontalLayout theoryLayout = new HorizontalLayout();
		theoryLayout.setResponsive(true);
		theoryLayout.addComponents(dto.getTheoryMarks(), dto.getTheoryPassMarks());
		HorizontalLayout pLayout = new HorizontalLayout();
		pLayout.setResponsive(true);
		pLayout.addComponents(dto.getPracticalMarks(), dto.getPracticalPassMarks());
		HorizontalLayout iLayout = new HorizontalLayout();
		iLayout.setResponsive(true);
		iLayout.addComponents(dto.getInternalMarks(), dto.getInternalPassMarks());
		HorizontalLayout oLayout = new HorizontalLayout();
		oLayout.setResponsive(true);
		oLayout.addComponents(dto.getOtherMarks(), dto.getOtherPassMarks());

		// we require button to be next to each other on ui
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setResponsive(true);
		buttonLayout.addComponent(dto.getReset());
		buttonLayout.addComponent(dto.getSaveCourse());

		firstPanelLayout.addComponents(dto.getSubjectName(), dto.getSubjectAttributes(), theoryLayout, pLayout, iLayout,
				oLayout, buttonLayout);
		firstPanelLayout.setResponsive(true);
		firstPart.setContent(firstPanelLayout);
		firstPart.setResponsive(true);
		// we require total marks and next to it passing marks on ui
		VerticalLayout subjectAttributes = new VerticalLayout();
		subjectAttributes.setResponsive(true);
		subjectAttributes.addComponent(dto.getAddedSubjects());

		secondPart.setContent(subjectAttributes);
		secondPart.setSizeFull();

		mainLayout.addComponents(firstPart, secondPart);
		VerticalLayout courseLayout = new VerticalLayout();
		courseLayout.addComponents(mainLayout);
		courseLayout.setResponsive(true);
		return courseLayout;
	}
}
