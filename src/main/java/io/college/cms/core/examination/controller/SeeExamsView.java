package io.college.cms.core.examination.controller;

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
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.examination.model.ExaminationModel;
import io.college.cms.core.examination.service.ExamResponseService;
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

	@Autowired
	public SeeExamsView(ExamResponseService service) {
		this.examResponseService = service;
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void paint() {
		Panel panel = new Panel();
		Grid<ExaminationModel> grid = new Grid<>();
		VerticalLayout layout = new VerticalLayout();
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
				getUI().addWindow(window);
			}
		});
		layout.addComponent(grid);
		panel.setContent(layout);
		addComponent(panel);
	}

	@Override
	public void enter(ViewChangeEvent event) {
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

}
