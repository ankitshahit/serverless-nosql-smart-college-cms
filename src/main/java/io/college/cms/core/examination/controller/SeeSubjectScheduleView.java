package io.college.cms.core.examination.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.examination.model.ExaminationModel;
import io.college.cms.core.examination.model.TimeTableModel;
import io.college.cms.core.examination.service.ExamResponseService;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.model.ViewConstants;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class SeeSubjectScheduleView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	private ApplicationContext app;
	private ExamResponseService examResponseService;
	private Grid<TimeTableModel> grid;
	private TextField filterByName;
	private TextField filterByTag;
	private TextField filterBySubject;

	@Autowired
	public SeeSubjectScheduleView(ExamResponseService service) {
		this.examResponseService = service;
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void paint() {
		this.filterByName = VaadinWrapper.builder().caption("Filter by course name").placeholder("type course name")
				.icon(VaadinIcons.SEARCH).build().textField();
		this.filterByTag = VaadinWrapper.builder().caption("Filter by exam").placeholder("type exam")
				.icon(VaadinIcons.SEARCH).build().textField();
		this.filterBySubject = VaadinWrapper.builder().caption("Filter by subject").placeholder("type subject")
				.icon(VaadinIcons.SEARCH).build().textField();
		grid = new Grid<>();

		grid.addColumn(TimeTableModel::getExamName).setCaption("Exam name");
		grid.addColumn(TimeTableModel::getCourseName).setCaption("Course name");
		grid.addColumn(TimeTableModel::getSubject).setCaption("Subject name");
		grid.addColumn(TimeTableModel::getSubjectType).setCaption("Subject type");
		grid.addColumn(TimeTableModel::getStartTime).setCaption("Exam completes on");
		grid.addColumn(TimeTableModel::getEndTime).setCaption("Exam completes on");

		grid.setSizeFull();

		VerticalLayout rootLayout = new VerticalLayout();
		Panel rootPanel = new Panel();
		rootPanel.setContent(this.grid);
		HorizontalLayout searchLayout = new HorizontalLayout(this.filterByName, this.filterByTag, filterBySubject);
		searchLayout.setSizeFull();
		rootLayout.addComponents(new Panel(new VerticalLayout(searchLayout, rootPanel)));

		Panel designPanel = new Panel();
		designPanel.setContent(rootLayout);
		addComponent(rootLayout);
		this.grid.setSizeFull();
		rootLayout.setSizeFull();
		rootPanel.setSizeFull();
		filterByTag.addValueChangeListener(this::onExamNameFilterTextChange);
		filterByName.addValueChangeListener(this::onCourseNameFilterTextChange);
		filterBySubject.addValueChangeListener(this::onSubjectNameFilterTextChange);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		try {
			LOGGER.debug("request received view : {}", event);

			List<TimeTableModel> models = new ArrayList<>();
			FactoryResponse fr = examResponseService.findAllTimeTable();
			if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
				Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
				notifi.setIcon(VaadinIcons.STOP);
				notifi.setCaption("Error");
				notifi.setDescription("We couldn't load course data");
				notifi.setDelayMsec(Notification.DELAY_FOREVER);
				return;
			}
			models = (List<TimeTableModel>) fr.getResponse();
			grid.setItems(models);
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

	private void onCourseNameFilterTextChange(HasValue.ValueChangeEvent<String> event) {
		ListDataProvider<TimeTableModel> dataProvider = (ListDataProvider<TimeTableModel>) grid.getDataProvider();
		dataProvider.setFilter(TimeTableModel::getCourseName, s -> caseInsensitiveContains(s, event.getValue()));
	}

	private void onExamNameFilterTextChange(HasValue.ValueChangeEvent<String> event) {
		ListDataProvider<TimeTableModel> dataProvider = (ListDataProvider<TimeTableModel>) grid.getDataProvider();
		dataProvider.setFilter(TimeTableModel::getExamName, s -> caseInsensitiveContains(s, event.getValue()));
	}

	private void onSubjectNameFilterTextChange(HasValue.ValueChangeEvent<String> event) {
		ListDataProvider<TimeTableModel> dataProvider = (ListDataProvider<TimeTableModel>) grid.getDataProvider();
		dataProvider.setFilter(TimeTableModel::getSubject, s -> caseInsensitiveContains(s, event.getValue()));
	}

	private Boolean caseInsensitiveContains(String where, String what) {
		return new StringBuilder().append("").append(where).toString().toLowerCase()
				.contains(new StringBuilder().append("").append(what).toString().toLowerCase());
	}
}
