package io.college.cms.core.job.controller;

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

import io.college.cms.core.job.model.JobModel;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.services.CoreUiService;
import io.college.cms.core.upload.model.UploadModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class SeeJobsView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private CoreUiService coreUiService;
	private Grid<JobModel> grid;
	private ApplicationContext app;
	private TextField filterByName;
	private TextField filterByTag;

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
		this.filterByName = VaadinWrapper.builder().caption("Filter by location").placeholder("type location")
				.icon(VaadinIcons.SEARCH).build().textField();
		this.filterByTag = VaadinWrapper.builder().caption("Filter by title").placeholder("type title")
				.icon(VaadinIcons.SEARCH).build().textField();
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
		this.filterByName.addValueChangeListener(this::onUsernameFilterTextChange);
		this.filterByTag.addValueChangeListener(this::onTagFilterTextChange);
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

	private void onUsernameFilterTextChange(HasValue.ValueChangeEvent<String> event) {
		ListDataProvider<JobModel> dataProvider = (ListDataProvider<JobModel>) grid.getDataProvider();
		dataProvider.setFilter(JobModel::getLocation, s -> caseInsensitiveContains(s, event.getValue()));
	}

	private void onTagFilterTextChange(HasValue.ValueChangeEvent<String> event) {
		ListDataProvider<JobModel> dataProvider = (ListDataProvider<JobModel>) grid.getDataProvider();
		dataProvider.setFilter(JobModel::getTitle, s -> caseInsensitiveContains(s, event.getValue()));
	}

	private Boolean caseInsensitiveContains(String where, String what) {
		return new StringBuilder().append("").append(where).toString().toLowerCase()
				.contains(new StringBuilder().append("").append(what).toString().toLowerCase());
	}
}
