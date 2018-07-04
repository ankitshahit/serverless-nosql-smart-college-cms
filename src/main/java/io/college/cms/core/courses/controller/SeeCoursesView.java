package io.college.cms.core.courses.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.data.HasValue;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.service.CourseResponseService;
import io.college.cms.core.ui.builder.VaadinWrapper;
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
	private TextField searchByCourseNameFld;

	@Autowired
	public SeeCoursesView(CourseResponseService service) {
		this.courseResponseService = service;
	}

	@PostConstruct
	public void paint() {
		Panel panel = new Panel();
		grid = new Grid<>();
		searchByCourseNameFld = VaadinWrapper.builder().caption("search by course").placeholder("type coursename")
				.build().textField();
		searchByCourseNameFld.addValueChangeListener(this::onUsernameFilterTextChange);
		VerticalLayout layout = new VerticalLayout();
		List<CourseModel> models = new ArrayList<>();

		FactoryResponse fr = courseResponseService.findAllCourses(null, 0L, 0L);
		if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
			// Utils.showFactoryResponseOnlyError(fr);
			return;
		}
		models = (List<CourseModel>) fr.getResponse();
		if (CollectionUtils.isEmpty(models)) {
			models.add(CourseModel.builder().courseName("hello").build());
		}
		grid.setItems(models);
		grid.addColumn(CourseModel::getCourseName).setCaption("Course name");
		grid.addColumn(CourseModel::getDescription).setCaption("Description");
		grid.addColumn(CourseModel::getOutOf).setCaption("Enrolled/Out of");
		grid.addColumn(CourseModel::isArchive).setCaption("Archived?");
		grid.addColumn(CourseModel::getTotalSemestersInNumber).setCaption("Total semesters");

		grid.setSizeFull();
		grid.setSelectionMode(SelectionMode.SINGLE);
		this.grid.addStyleNames(ValoTheme.TABLE_NO_STRIPES);
		layout.addComponent(grid);
		layout.setSizeFull();
		panel.setContent(layout);

		grid.setSizeFull();
		grid.setFooterVisible(true);
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
				// getUI().addWindow(window);
				// view.enter(null);
			}
		});
		VerticalLayout rootLayout = new VerticalLayout();
		HorizontalLayout searchLayout = new HorizontalLayout(this.searchByCourseNameFld);
		searchLayout.setSizeFull();
		rootLayout.addComponents(new Panel(new VerticalLayout(searchLayout, panel)));

		Panel designPanel = new Panel();
		designPanel.setContent(rootLayout);
		addComponent(rootLayout);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {

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

	private void onUsernameFilterTextChange(HasValue.ValueChangeEvent<String> event) {
		ListDataProvider<CourseModel> dataProvider = (ListDataProvider<CourseModel>) grid.getDataProvider();
		dataProvider.setFilter(CourseModel::getCourseName, s -> caseInsensitiveContains(s, event.getValue()));
	}

	private Boolean caseInsensitiveContains(String where, String what) {
		return new StringBuilder().append("").append(where).toString().toLowerCase()
				.contains(new StringBuilder().append("").append(what).toString().toLowerCase());
	}
}
