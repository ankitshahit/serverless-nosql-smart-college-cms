package io.college.cms.core.courses.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.service.CourseResponseService;
import io.college.cms.core.ui.controller.PublishCourseView;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class SeeCoursesView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	@Autowired
	private ApplicationContext app;
	private CourseResponseService courseResponseService;
	private Grid<CourseModel> grid;

	@Autowired
	public SeeCoursesView(CourseResponseService service) {
		this.courseResponseService = service;
	}

	@PostConstruct
	public void paint() {
		Panel panel = new Panel();
		grid = new Grid<>();
		VerticalLayout layout = new VerticalLayout();
		List<CourseModel> models = new ArrayList<>();
		models.add(CourseModel.builder().courseName("hello").build());
		FactoryResponse fr = courseResponseService.findAllCourses(null, 0L, 0L);
		if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
			Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
			notifi.setIcon(VaadinIcons.STOP);
			notifi.setCaption("Error");
			notifi.setDescription("We couldn't load course data");
			notifi.setDelayMsec(Notification.DELAY_FOREVER);
			return;
		}
		models = (List<CourseModel>) fr.getResponse();
		grid.setItems(models);
		grid.addColumn(CourseModel::getCourseName).setCaption("Course name");
		grid.addColumn(CourseModel::getOutOf).setCaption("Enrolled/Out of");
		grid.addColumn(CourseModel::isArchive).setCaption("Archived?");
		grid.setSizeFull();
		grid.setSelectionMode(SelectionMode.SINGLE);

		layout.addComponent(grid);
		layout.setSizeFull();
		panel.setContent(layout);
		addComponent(panel);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			grid.addSelectionListener(selection -> {
				LOGGER.debug("Selection listener fired.");
				if (selection.getFirstSelectedItem().isPresent()) {
					CourseModel courseModel = selection.getFirstSelectedItem().get();
					LOGGER.debug("course name : {}", courseModel.getCourseName());
					PublishCourseView view = app.getBean(PublishCourseView.class);
					view.setCourseModel(courseModel);
					Window window = new Window();
					window.setContent(view);
					window.center();
					window.setResizable(false);
					window.addCloseListener(close -> {

					});
					getUI().addWindow(window);
					view.enter(null);
				}
			});
			LOGGER.debug("request received view : {}", event);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
			notifi.setCaption("Application error");
			notifi.setIcon(VaadinIcons.STOP_COG);
			notifi.setDescription(
					"We were unable to process request for some reason! Please try again later or contact admin");
			notifi.setDelayMsec(Notification.DELAY_FOREVER);
		}
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

}
