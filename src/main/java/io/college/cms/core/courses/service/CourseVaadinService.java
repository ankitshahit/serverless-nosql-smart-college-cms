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
import com.vaadin.ui.Notification;
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
						.icon(VaadinIcons.DIPLOMA).build()

						.textField())
				.build();
		courseDTO.getCourseName().setSizeFull();
		courseDTO.getCourseName().focus();
		courseDTO.setMaxStudents(TextFieldWrapper.builder().caption("Max number of seats")
				.description("Provide total number of students that can be enrolled into.").build().textField());

		CheckBox archive = new CheckBox();
		archive.setIcon(VaadinIcons.ARCHIVES);
		archive.setCaption("Archive?");
		courseDTO.setIsArchive(archive);

		RichTextArea area = new RichTextArea();
		area.setCaption("Course Description");
		area.setIcon(VaadinIcons.INFO);
		area.setRequiredIndicatorVisible(true);
		area.setSizeFull();
		courseDTO.setCourseDescription(area);
		Label label = new Label(
				"Attributes \n ______________________________________________________________________________________________________________________________________");
		label.setWidth("100%");
		label.setSizeFull();
		courseDTO.setAttributesSeperator(label);
		courseDTO.setSaveCourse(ButtonWrapper.builder().caption("Save & Next").build().button());
		courseDTO.setReset(ButtonWrapper.builder().caption("Delete").build().button());
		courseDTO.getReset().setStyleName(ValoTheme.BUTTON_DANGER);
		courseDTO.getSaveCourse().setStyleName(ValoTheme.BUTTON_QUIET);
		return courseDTO;
	}

	public VerticalLayout buildCoursePageOne(CourseDTO courseDTO) {
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.addComponent(courseDTO.getReset());
		hLayout.addComponent(courseDTO.getSaveCourse());
		VerticalLayout courseLayout = new VerticalLayout();
		courseLayout.addComponents(courseDTO.getCourseName(), courseDTO.getCourseDescription(),
				courseDTO.getAttributesSeperator(), courseDTO.getMaxStudents(), courseDTO.getIsArchive(), hLayout);
		courseLayout.setComponentAlignment(hLayout, Alignment.BOTTOM_RIGHT);
		courseLayout.setComponentAlignment(courseDTO.getAttributesSeperator(), Alignment.MIDDLE_CENTER);
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
		CheckBoxGroup<String> cbg = new CheckBoxGroup<String>();
		cbg.setCaption("Attributes: ");
		cbg.setItems("Theory", "Practical", "Internal", "Others");
		dto.setSubjectAttributes(cbg);
		dto.setTheoryMarks(TextFieldWrapper.builder().caption("Total available marks ~ Theory").required(false)
				.visible(false).icon(VaadinIcons.ADJUST).build().textField());
		dto.setPracticalMarks(TextFieldWrapper.builder().caption("Total available marks ~ Practical").required(false)
				.visible(false).icon(VaadinIcons.ADJUST).build().textField());
		dto.setInternalMarks(TextFieldWrapper.builder().caption("Total available marks ~ Internal").required(false)
				.visible(false).build().textField());
		dto.setOtherMarks(TextFieldWrapper.builder().caption("Total available marks ~ Others").required(false)
				.visible(false).build().textField());
		dto.setTheoryPassMarks(TextFieldWrapper.builder().caption("Required passing marks ~ Theory").required(false)
				.visible(false).icon(VaadinIcons.ADJUST).build().textField());
		dto.setPracticalPassMarks(TextFieldWrapper.builder().caption("Required passing marks ~ Practical")
				.required(false).visible(false).icon(VaadinIcons.ADJUST).build().textField());
		dto.setInternalPassMarks(TextFieldWrapper.builder().caption("Required passing marks ~ Internal").required(false)
				.visible(false).build().textField());
		dto.setOtherPassMarks(TextFieldWrapper.builder().caption("Required passing marks ~ Others").required(false)
				.visible(false).build().textField());
		ListSelect<String> subjects = new ListSelect<>();
		subjects.setCaption("Subjects added: ");
		dto.setAddedSubjects(subjects);
		subjects.setSizeFull();
		CheckBox optional = new CheckBox();
		optional.setCaption("Is subject optional?");
		dto.setOptional(optional);
		dto.setSaveCourse(ButtonWrapper.builder().caption("Save & Next").build().button());
		dto.setReset(ButtonWrapper.builder().caption("Reset").build().button());
		dto.getReset().setStyleName(ValoTheme.BUTTON_DANGER);
		dto.getSaveCourse().setStyleName(ValoTheme.BUTTON_QUIET);
		return dto;
	}

	public VerticalLayout buildCoursePageTwo(CourseDTO dto) {
		// screen will be divided into two parts,
		// first part will contain adding subjects and second part will contain
		// all the subjects it has till now.
		HorizontalLayout mainLayout = new HorizontalLayout();
		Panel firstPart = new Panel();
		Panel secondPart = new Panel();

		VerticalLayout firstPanelLayout = new VerticalLayout();

		HorizontalLayout theoryLayout = new HorizontalLayout();
		theoryLayout.addComponents(dto.getTheoryMarks(), dto.getTheoryPassMarks());
		HorizontalLayout pLayout = new HorizontalLayout();
		pLayout.addComponents(dto.getPracticalMarks(), dto.getPracticalPassMarks());
		HorizontalLayout iLayout = new HorizontalLayout();
		iLayout.addComponents(dto.getInternalMarks(), dto.getInternalPassMarks());
		HorizontalLayout oLayout = new HorizontalLayout();
		oLayout.addComponents(dto.getOtherMarks(), dto.getOtherPassMarks());

		// we require button to be next to each other on ui
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.addComponent(dto.getReset());
		buttonLayout.addComponent(dto.getSaveCourse());

		firstPanelLayout.addComponents(dto.getSubjectName(), dto.getSubjectAttributes(), theoryLayout, pLayout, iLayout,
				oLayout, buttonLayout);

		firstPart.setContent(firstPanelLayout);

		// we require total marks and next to it passing marks on ui
		VerticalLayout subjectAttributes = new VerticalLayout();
		subjectAttributes.addComponent(dto.getAddedSubjects());

		secondPart.setContent(subjectAttributes);
		secondPart.setSizeFull();

		mainLayout.addComponents(firstPart, secondPart);
		VerticalLayout courseLayout = new VerticalLayout();
		courseLayout.addComponents(mainLayout);
		return courseLayout;
	}
}
