package io.college.cms.core.ui.controller;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
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

	private Navigator navigator;
	private CourseDTO courseDTO;
	private CourseResponseService courseResp;

	private CourseVaadinService courseUIService;
	private ApplicationContext app;

	@Autowired
	public void setCourseUIService(CourseVaadinService courseUIService) {
		this.courseUIService = courseUIService;
	}

	@Autowired
	public void setCourseResp(CourseResponseService courseResp) {
		this.courseResp = courseResp;
	}

	@Autowired
	public void setApp(ApplicationContext app) {
		this.app = app;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			this.navigator = event.getNavigator();

			Accordion accordin = new Accordion();
			accordin.setTabsVisible(true);
			CourseDTO courseDTO = courseUIService.courseName();

			courseDTO.getSaveCourse().addClickListener(click -> {
				if (click.getSource() != courseDTO.getSaveCourse()) {
					return;
				}
				if (!courseDTO.getMaxStudents().getOptionalValue().isPresent()
						|| !StringUtils.isNumber(courseDTO.getMaxStudents().getOptionalValue().get())) {
					Notification.show("Max seats available is not provided or is not a number", Type.ERROR_MESSAGE);
				}
				FactoryResponse data = courseResp.saveUpdateCourseMetaData(CourseModel.builder()
						.courseName(courseDTO.getCourseName().getOptionalValue().get())
						.description(courseDTO.getCourseDescription().getOptionalValue().get())
						.isArchive(courseDTO.getIsArchive().getValue())
						.maxStudentsAllowed(Long.valueOf(courseDTO.getMaxStudents().getOptionalValue().get())).build());

				if (io.college.cms.core.application.SummaryMessageEnum.SUCCESS != data.getSummaryMessage()
						&& data.getResponse() instanceof String) {
					Notification.show("Validation error: " + data.getResponse(), Type.ERROR_MESSAGE);
				} else {
					Notification notifi = Notification.show("Succesfully saved moving to step 2");
					notifi.setDelayMsec(Notification.DELAY_FOREVER);
					notifi.addCloseListener(close -> {
						accordin.setSelectedTab(2);
					});
				}

			});

			accordin.addTab(courseUIService.buildCoursePageOne(courseDTO), "Create course			(1/3)");
			accordin.setHeight("80%");
			accordin.setWidth("80%");
			addComponent(accordin);
			setComponentAlignment(accordin, Alignment.MIDDLE_CENTER);

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
