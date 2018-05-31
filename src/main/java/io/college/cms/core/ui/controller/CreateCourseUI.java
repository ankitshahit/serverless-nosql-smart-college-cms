package io.college.cms.core.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.VerticalLayout;

import io.college.cms.core.courses.service.CourseResponseService;
import io.college.cms.core.ui.model.CourseDTO;
import io.college.cms.core.ui.model.TextFieldBuilder;
import io.college.cms.core.ui.services.ICoursesService;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class CreateCourseUI extends VerticalLayout implements View, ICoursesService {

	private static final long serialVersionUID = 1L;

	private Navigator navigator;
	private CourseDTO courseDTO;
	private CourseResponseService courseResp;

	@Autowired
	public void setCourseDTO(CourseDTO courseDTO) {
		this.courseDTO = courseDTO;
	}

	@Autowired
	public void setCourseResp(CourseResponseService courseResp) {
		this.courseResp = courseResp;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			this.navigator = event.getNavigator();

			/*
			 * courseDTO =
			 * CourseDTO.builder().withTextField(TextFieldBuilder.builder().
			 * caption("Course name")
			 * .placeholder("Example : MSC IT").requiredVisible(true).build()).
			 * build(); addComponents(courseDTO.getTextFields().get(0));
			 */
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

	private static class DrawUI {

	}
}
