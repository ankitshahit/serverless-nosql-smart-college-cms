package io.college.cms.core.courses.service;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.courses.controller.constants.SubjectType;
import io.college.cms.core.ui.builder.ButtonWrapper;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.model.CourseDTO;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CourseVaadinService {
	public CourseDTO courseMetaDataStep1() {
		CourseDTO courseDTO = CourseDTO.builder()
				.courseName(VaadinWrapper.builder().caption("Course").placeholder("Provide unique course name")
						.description("Provide unique coursename that represents to actual available streams")
						.icon(VaadinIcons.DIPLOMA).build().textField())
				.build();
		courseDTO.getCourseName().setResponsive(true);
		courseDTO.getCourseName().focus();
		courseDTO.setMaxStudents(VaadinWrapper.builder().caption("Max number of seats")
				.placeholder("Count of max students that can be enrolled in")
				.description("Provide total number of students that can be enrolled into.").build().textField());
		courseDTO.getMaxStudents().setResponsive(true);
		CheckBox archive = new CheckBox();
		archive.setIcon(VaadinIcons.ARCHIVES);
		archive.setCaption("Archive?");
		courseDTO.setIsArchive(archive);
		archive.setResponsive(true);
		RichTextArea area = new RichTextArea();
		area.setCaption("Course Description");
		area.setSizeFull();
		area.setIcon(VaadinIcons.INFO);
		area.setRequiredIndicatorVisible(true);
		area.setResponsive(true);
		courseDTO.setCourseDescription(area);
		courseDTO.getCourseDescription().setResponsive(true);
		Label label = new Label("== Attributes ==");
		courseDTO.setAttributesSeperator(label);
		courseDTO.getAttributesSeperator().setResponsive(true);
		courseDTO.setSaveCourse(ButtonWrapper.builder().caption("Save & Next").build().button());
		courseDTO.getSaveCourse().setStyleName(ValoTheme.BUTTON_PRIMARY);
		courseDTO.getSaveCourse().setResponsive(true);
		courseDTO.setReset(ButtonWrapper.builder().caption("Delete").build().button());
		courseDTO.getReset().setResponsive(true);
		courseDTO.getReset().setStyleName(ValoTheme.BUTTON_DANGER);
		courseDTO.getSaveCourse().setStyleName(ValoTheme.BUTTON_PRIMARY);
		courseDTO.getSaveCourse().setResponsive(true);
		courseDTO.setTotalSem(VaadinWrapper.builder().caption("Total semesters")
				.placeholder("Provide total semesters in course").required(true).build().textField());
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
				.subjectName(VaadinWrapper.builder().caption("Subject name").placeholder("Provide unique subject name")
						.description("Provide unique subject that represents to actual available subjects")
						.icon(VaadinIcons.DIPLOMA).build().textField())
				.build();
		dto.getSubjectName().setResponsive(true);
		dto.setCurrentSemester(
				VaadinWrapper.builder().caption("For semester").placeholder("Sem 1").build().textField());

		CheckBoxGroup<String> cbg = new CheckBoxGroup<String>();
		cbg.setCaption("Attributes: ");
		cbg.setItems(SubjectType.THEORY.toString(), SubjectType.PRACTICAL.toString(), SubjectType.INTERNAL.toString(),
				SubjectType.OTHER.toString());
		dto.setSubjectAttributes(cbg);
		dto.setTheoryMarks(VaadinWrapper.builder().caption("Total available marks ~ Theory").placeholder("Total marks")
				.required(false).visible(false).placeholder("Total marks").icon(VaadinIcons.ADJUST).build()
				.textField());

		cbg.setResponsive(true);
		dto.setPracticalMarks(VaadinWrapper.builder().caption("Total available marks ~ Practical")
				.placeholder("Total marks").required(false).visible(false).icon(VaadinIcons.ADJUST)
				.style(ValoTheme.TEXTFIELD_TINY).build().textField());
		dto.getPracticalMarks().setResponsive(true);
		dto.setInternalMarks(
				VaadinWrapper.builder().caption("Total available marks ~ Internal").placeholder("Total marks")
						.icon(VaadinIcons.ADJUST).required(false).visible(false).build().textField());
		dto.getInternalMarks().setResponsive(true);
		dto.setOtherMarks(VaadinWrapper.builder().caption("Total available marks ~ Others").placeholder("Total marks")
				.icon(VaadinIcons.ADJUST).required(false).visible(false).build().textField());
		dto.getOtherMarks().setResponsive(true);
		dto.setTheoryPassMarks(VaadinWrapper.builder().caption("Required passing marks ~ Theory")
				.placeholder("Pass marks").placeholder("Pass marks").required(false).visible(false)
				.icon(VaadinIcons.ADJUST).build().textField());
		dto.getTheoryPassMarks().setResponsive(true);
		dto.setPracticalPassMarks(VaadinWrapper.builder().caption("Required passing marks ~ Practical")
				.placeholder("Pass marks").required(false).visible(false).icon(VaadinIcons.ADJUST).build().textField());
		dto.getPracticalPassMarks().setResponsive(true);
		dto.setInternalPassMarks(VaadinWrapper.builder().caption("Required passing marks ~ Internal")
				.placeholder("Pass marks").required(false).visible(false).build().textField());
		dto.getInternalPassMarks().setResponsive(true);
		dto.setOtherPassMarks(VaadinWrapper.builder().caption("Required passing marks ~ Others")
				.placeholder("Pass marks").required(false).visible(false).build().textField());
		dto.getOtherPassMarks().setResponsive(true);
		dto.getOtherMarks().setWidthUndefined();
		ListSelect<String> subjects = new ListSelect<>();
		subjects.setResponsive(true);
		subjects.setSizeFull();
		subjects.setCaption("Subjects added: ");
		dto.setAddedSubjects(subjects);
		dto.getAddedSubjects().setResponsive(true);
		dto.getAddedSubjects().setWidth("100%");
		CheckBox optional = new CheckBox();
		optional.setCaption("Is subject optional?");
		dto.setOptional(optional);
		dto.setSaveCourse(ButtonWrapper.builder().caption("Add subject").build().button());
		dto.setReset(ButtonWrapper.builder().caption("Reset").build().button());
		dto.getReset().setStyleName(ValoTheme.BUTTON_DANGER);
		dto.getSaveCourse().setStyleName(ValoTheme.BUTTON_QUIET);
		dto.getOptional().setResponsive(true);
		dto.getSaveCourse().setResponsive(true);
		dto.getReset().setResponsive(true);

		dto.setRemoveSubject(new Button("Remove"));
		dto.getRemoveSubject().setVisible(false);
		dto.getRemoveSubject().setIcon(VaadinIcons.STOP);
		dto.getRemoveSubject().addStyleNames(ValoTheme.BUTTON_DANGER);
		dto.setCompleteDialog(new Button("Save"));
		dto.getCompleteDialog().addStyleNames(ValoTheme.BUTTON_PRIMARY);
		return dto;
	}

	public VerticalLayout buildCoursePageTwo(CourseDTO dto) {
		// screen will be divided into two parts,
		// first part will contain adding subjects and second part will contain
		// all the subjects it has till now.
		HorizontalLayout mainLayout = new HorizontalLayout();
		Panel firstPart = new Panel();
		Panel secondPart = new Panel();
		/*
		 * mainLayout.setResponsive(false); firstPart.setResponsive(false);
		 * secondPart.setResponsive(true);
		 */

		VerticalLayout firstPanelLayout = new VerticalLayout();
		firstPanelLayout.setResponsive(true);
		VerticalLayout theoryLayout = new VerticalLayout();
		theoryLayout.setResponsive(true);
		theoryLayout.addComponents(dto.getTheoryMarks(), dto.getTheoryPassMarks());
		VerticalLayout pLayout = new VerticalLayout();
		pLayout.setResponsive(true);
		pLayout.addComponents(dto.getPracticalMarks(), dto.getPracticalPassMarks());
		VerticalLayout iLayout = new VerticalLayout();
		iLayout.setResponsive(true);
		iLayout.addComponents(dto.getInternalMarks(), dto.getInternalPassMarks());
		VerticalLayout oLayout = new VerticalLayout();
		oLayout.setResponsive(true);
		oLayout.addComponents(dto.getOtherMarks(), dto.getOtherPassMarks());

		// we require button to be next to each other on ui
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setResponsive(true);
		buttonLayout.addComponent(dto.getReset());
		buttonLayout.addComponent(dto.getSaveCourse());

		firstPanelLayout.addComponents(dto.getSubjectName(), dto.getSemestersCb(), dto.getSubjectAttributes(),
				theoryLayout, pLayout, iLayout, oLayout, dto.getOptional(), buttonLayout);
		firstPanelLayout.setResponsive(true);
		firstPart.setContent(firstPanelLayout);
		firstPart.setResponsive(true);
		// we require total marks and next to it passing marks on ui
		VerticalLayout subjectAttributes = new VerticalLayout();
		subjectAttributes.setResponsive(true);
		subjectAttributes.addComponents(dto.getAddedSubjects(), dto.getRemoveSubject(), dto.getCompleteDialog());
		subjectAttributes.setSizeFull();
		secondPart.setContent(subjectAttributes);
		secondPart.setSizeFull();

		mainLayout.addComponents(firstPart, secondPart);
		mainLayout.setSizeFull();
		VerticalLayout courseLayout = new VerticalLayout();
		courseLayout.setSizeFull();
		courseLayout.addComponents(mainLayout);
		courseLayout.setResponsive(true);
		return courseLayout;
	}
}
