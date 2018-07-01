package io.college.cms.core.job.controller;

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

import io.college.cms.core.job.model.JobModel;
import io.college.cms.core.ui.services.CoreUiService;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class SeeJobsView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private CoreUiService coreUiService;
	private Grid<JobModel> grid;
	private ApplicationContext app;

	@Autowired
	public SeeJobsView(ApplicationContext app, CoreUiService coreUiService) {
		super();
		this.app = app;
		// this.jobResponseService = jobResponseService;
		this.coreUiService = coreUiService;
		this.grid = new Grid<>();
	}

	@PostConstruct
	protected void paint() {

		this.grid.addColumn(JobModel::getTitle).setCaption("Title");
		this.grid.addColumn(JobModel::getInterViewDate).setSortable(true).setCaption("Interview date");
		this.grid.addColumn(JobModel::getSalary).setCaption("Salary");
		this.grid.addColumn(JobModel::getLocation).setCaption("Location");

		VerticalLayout rootLayout = new VerticalLayout();
		Panel rootPanel = new Panel();
		rootPanel.setContent(this.grid);
		rootLayout.addComponents(rootPanel);
		addComponent(rootLayout);
		this.grid.setSelectionMode(Grid.SelectionMode.SINGLE);
		this.grid.addSelectionListener(select -> {
			if (!select.getFirstSelectedItem().isPresent()) {
				return;
			}
			Window window = new Window();
			window.center();
			window.setResizable(false);
			VerticalLayout layout = new VerticalLayout();
			PublishJobView view = app.getBean(PublishJobView.class);
			layout.addComponent(view);
			layout.setSizeUndefined();
			window.setSizeUndefined();
			window.setContent(layout);
			view.setJobModel(select.getFirstSelectedItem().get());
			view.enter(null);
			getUI().addWindow(window);
		});
		this.grid.setSizeFull();
		rootLayout.setSizeFull();
		rootPanel.setSizeFull();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			LOGGER.debug("request received view : {}", event);
			coreUiService.setItemsJob(this.grid);
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
