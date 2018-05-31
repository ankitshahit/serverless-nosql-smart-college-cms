package io.college.cms.core.courses.service;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.ui.builder.ButtonWrapper;
import io.college.cms.core.ui.builder.TextFieldWrapper;
import io.college.cms.core.ui.model.CourseDTO;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CourseVaadinService {
	public CourseDTO courseName() {
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
		courseDTO.setReset(ButtonWrapper.builder().caption("Reset?").build().button());
		courseDTO.getReset().setStyleName(ValoTheme.BUTTON_DANGER);
		courseDTO.getSaveCourse().setStyleName(ValoTheme.BUTTON_QUIET);
		return courseDTO;
	}

	public HorizontalLayout buildHorizontalLayoutSaveAndNextBtn(CourseDTO dto) {
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.addComponent(dto.getReset());
		hLayout.addComponent(dto.getSaveCourse());
		return hLayout;
	}

	public VerticalLayout buildCoursePageOne(CourseDTO courseDTO) {
		HorizontalLayout hLayout = buildHorizontalLayoutSaveAndNextBtn(courseDTO);
		VerticalLayout courseLayout = new VerticalLayout();
		courseLayout.addComponents(courseDTO.getCourseName(), courseDTO.getCourseDescription(),
				courseDTO.getAttributesSeperator(), courseDTO.getMaxStudents(), courseDTO.getIsArchive(), hLayout);
		courseLayout.setComponentAlignment(hLayout, Alignment.BOTTOM_RIGHT);
		courseLayout.setComponentAlignment(courseDTO.getAttributesSeperator(), Alignment.MIDDLE_CENTER);
		return courseLayout;
	}
}
