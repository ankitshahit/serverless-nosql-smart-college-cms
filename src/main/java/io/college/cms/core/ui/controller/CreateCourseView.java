package io.college.cms.core.ui.controller;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.service.CourseResponseService;
import io.college.cms.core.courses.service.CourseVaadinService;
import io.college.cms.core.ui.model.CourseDTO;
import io.college.cms.core.ui.services.ICoursesService;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class CreateCourseView extends VerticalLayout implements View, ICoursesService {

	private static final long serialVersionUID = 1L;

	private CourseResponseService courseResp;

	private CourseVaadinService courseUIService;

	@Autowired
	public void setCourseUIService(CourseVaadinService courseUIService) {
		this.courseUIService = courseUIService;
	}

	@Autowired
	public void setCourseResp(CourseResponseService courseResp) {
		this.courseResp = courseResp;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {

			Accordion accordin = new Accordion();
			accordin.setTabsVisible(true);

			VerticalLayout verticalLayout = new VerticalLayout();
			verticalLayout.addComponent(new Label("Hello World"));
			// we are tab 1 that is coursecreatestep1 in a different method to
			// increase readbility
			courseStepOne(accordin, verticalLayout);

			accordin.addTab(verticalLayout, "Create course (2/3)");
			accordin.setHeight("80%");
			accordin.setWidth("80%");
			addComponent(accordin);
			setComponentAlignment(accordin, Alignment.MIDDLE_CENTER);

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	private void courseStepOne(Accordion accordin, Component step2) {
		CourseDTO courseDTO = courseUIService.courseMetaDataStep1();
		accordin.addTab(courseUIService.buildCoursePageOne(courseDTO), "Create course			(1/3)");
		courseDTO.getSaveCourse().setEnabled(false);
		courseUIService.attachEmptyValueListenerCourseStep1(courseDTO);

		courseDTO.getSaveCourse().addClickListener(click -> {
			if (click.getSource() != courseDTO.getSaveCourse()) {
				return;
			}
			if (!courseDTO.getMaxStudents().getOptionalValue().isPresent()
					|| !StringUtils.isNumber(courseDTO.getMaxStudents().getOptionalValue().get())) {
				Notification.show("Max seats available is not provided or is not a number", Type.ERROR_MESSAGE);
			}
			FactoryResponse data = courseResp.saveCourseMetadata(CourseModel.builder()
					.courseName(courseDTO.getCourseName().getOptionalValue().get())
					.description(courseDTO.getCourseDescription().getOptionalValue().get())
					.isArchive(courseDTO.getIsArchive().getValue())
					.maxStudentsAllowed(Long.valueOf(courseDTO.getMaxStudents().getOptionalValue().get())).build());

			if (io.college.cms.core.application.SummaryMessageEnum.SUCCESS != data.getSummaryMessage()
					&& data.getResponse() instanceof String) {
				Notification.show("Validation error: " + data.getResponse(), Type.ERROR_MESSAGE);
			} else {
				accordin.setSelectedTab(step2);

				Notification notifi = Notification.show("Course created", Type.HUMANIZED_MESSAGE);
				notifi.setIcon(VaadinIcons.CHECK);
				notifi.setDelayMsec(Notification.DELAY_FOREVER);
				notifi.setHtmlContentAllowed(true);
				notifi.setDescription(
						"A course has been created with metadata of coursename, description, max seats available and archive?\n Click here to dismiss and move to <b>step2</b>");
			}

		});
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}
}
