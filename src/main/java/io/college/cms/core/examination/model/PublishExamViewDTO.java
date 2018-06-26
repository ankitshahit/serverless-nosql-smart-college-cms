package io.college.cms.core.examination.model;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishExamViewDTO {
	private ComboBox<String> courses;
	private ComboBox<String> semesters;
	private ComboBox<String> subjects;
	private ComboBox<String> subjectTypes;
	private DateField examStartDateTime;
	private DateField examEndDateTime;
}
