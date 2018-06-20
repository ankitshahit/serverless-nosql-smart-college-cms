package io.college.cms.core.ui.model;

import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
	private TextField courseName;
	private TextField maxStudents;
	private TextField enrolledStudents;
	private TwinColSelect<String> students;
	private TextField subjectName;
	private CheckBox isArchive;
	private Button saveCourse;
	private Button reset;
	private RichTextArea courseDescription;
	private Label attributesSeperator;
	private CheckBoxGroup<String> subjectAttributes;
	private TextField theoryMarks;
	private TextField theoryPassMarks;
	private TextField practicalMarks;
	private TextField practicalPassMarks;
	private TextField internalMarks;
	private TextField internalPassMarks;
	private TextField otherMarks;
	private TextField otherPassMarks;
	private CheckBox optional;
	private ListSelect<String> addedSubjects;
	private TextField totalSem;
	private HorizontalLayout stepOneGroupSeatsAndSem;
}
