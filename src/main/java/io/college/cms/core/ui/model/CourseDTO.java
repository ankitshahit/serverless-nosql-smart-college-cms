package io.college.cms.core.ui.model;

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
	
}
