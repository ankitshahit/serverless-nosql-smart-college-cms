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
import io.college.cms.core.examination.service.ExamResponseService;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.model.ViewConstants;
import io.college.cms.core.upload.model.UploadModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class SeeExamsView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	private ApplicationContext app;
	private ExamResponseService examResponseService;
	private Grid<ExaminationModel> grid;
	private TextField filterByName;
	private TextField filterByTag;

	@Autowired
	public SeeExamsView(ExamResponseService service) {
		this.examResponseService = service;
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void paint() {
		this.filterByName = VaadinWrapper.builder().caption("Filter by course name").placeholder("type course name")
				.icon(VaadinIcons.SEARCH).build().textField();
		this.filterByTag = VaadinWrapper.builder().caption("Filter by exam").placeholder("type exam")
				.icon(VaadinIcons.SEARCH).build().textField();

		grid = new Grid<>();

		grid.addColumn(ExaminationModel::getExamName).setCaption("Exam name");
		grid.addColumn(ExaminationModel::getCourseName).setCaption("Course name");
		grid.addColumn(ExaminationModel::getExamStartDate).setCaption("Exam Starts on");
		grid.addColumn(ExaminationModel::getExamEndDate).setCaption("Exam completes on");

		grid.setSizeFull();
		grid.addSelectionListener(selection -> {
			LOGGER.debug("Selection listener fired.");
			if (selection.isUserOriginated() && selection.getFirstSelectedItem().isPresent()) {
				ExaminationModel examinationModel = selection.getFirstSelectedItem().get();
				LOGGER.debug("course name : {}", examinationModel.getCourseName());
				Window window = new Window();
				window.setClosable(true);
				VerticalLayout verticalLayout = new VerticalLayout();

				PublishExamView view = app.getBean(PublishExamView.class);
				view.setModel(examinationModel);
				verticalLayout.addComponent(view);
				window.setContent(verticalLayout);
				window.setSizeFull();

				getUI().getNavigator().navigateTo(ViewConstants.EXAM_CREATE + "/" + examinationModel.getExamName());
				// getUI().addWindow(window);
			}
		});
		VerticalLayout rootLayout = new VerticalLayout();
		Panel rootPanel = new Panel();
		rootPanel.setContent(this.grid);
		HorizontalLayout searchLayout = new HorizontalLayout(this.filterByName, this.filterByTag);
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
	}

	@Override
	public void enter(ViewChangeEvent event) {
		try {
			LOGGER.debug("request received view : {}", event);

			List<ExaminationModel> models = new ArrayList<>();
			FactoryResponse fr = examResponseService.getExamsScheduled(null);
			if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
				Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
				notifi.setIcon(VaadinIcons.STOP);
				notifi.setCaption("Error");
				notifi.setDescription("We couldn't load course data");
				notifi.setDelayMsec(Notification.DELAY_FOREVER);
				return;
			}
			models = (List<ExaminationModel>) fr.getResponse();
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
		ListDataProvider<ExaminationModel> dataProvider = (ListDataProvider<ExaminationModel>) grid.getDataProvider();
		dataProvider.setFilter(ExaminationModel::getCourseName, s -> caseInsensitiveContains(s, event.getValue()));
	}

	private void onExamNameFilterTextChange(HasValue.ValueChangeEvent<String> event) {
		ListDataProvider<ExaminationModel> dataProvider = (ListDataProvider<ExaminationModel>) grid.getDataProvider();
		dataProvider.setFilter(ExaminationModel::getExamName, s -> caseInsensitiveContains(s, event.getValue()));
	}

	private Boolean caseInsensitiveContains(String where, String what) {
		return new StringBuilder().append("").append(where).toString().toLowerCase()
				.contains(new StringBuilder().append("").append(what).toString().toLowerCase());
	}
}
