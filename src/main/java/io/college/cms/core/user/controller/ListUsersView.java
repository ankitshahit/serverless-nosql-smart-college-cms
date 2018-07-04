package io.college.cms.core.user.controller;

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
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Notification.Type;

import io.college.cms.core.job.controller.PublishJobView;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.services.CoreUiService;
import io.college.cms.core.upload.model.UploadModel;
import io.college.cms.core.user.model.UserModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ListUsersView extends VerticalLayout implements View {
	private static final long serialVersionUID = 1L;
	private ApplicationContext app;
	private CoreUiService uiService;
	private Grid<UserModel> grid;
	private TextField filterByUsername;
	private TextField filterByEmail;
	private TextField filterByFirstName;

	/**
	 * @param uiService
	 */
	@Autowired
	public ListUsersView(ApplicationContext app, CoreUiService uiService) {
		super();
		this.app = app;
		this.uiService = uiService;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			this.uiService.setItemsUser(this.grid);
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

	@PostConstruct
	protected void paint() {
		this.grid = new Grid<>();
		this.grid.addColumn(UserModel::getFirstName).setCaption("First name");
		this.grid.addColumn(UserModel::getLastName).setCaption("Lastname");
		this.grid.addColumn(UserModel::getUsername).setCaption("username");
		this.grid.addColumn(UserModel::getEmail).setCaption("email");
		this.grid.addColumn(UserModel::getGender).setCaption("gender");

		this.filterByUsername = VaadinWrapper.builder().caption("Filter by username").placeholder("type username")
				.icon(VaadinIcons.SEARCH).build().textField();
		this.filterByEmail = VaadinWrapper.builder().caption("Filter by email").placeholder("type email")
				.icon(VaadinIcons.SEARCH).build().textField();
		this.filterByFirstName = VaadinWrapper.builder().caption("Filter by first name").placeholder("type first name")
				.icon(VaadinIcons.SEARCH).build().textField();
		filterByUsername.addValueChangeListener(this::onUsernameFilterTextChange);
		filterByEmail.addValueChangeListener(this::onEmailFilterTextChange);
		filterByFirstName.addValueChangeListener(this::onFirstNameFilterTextChange);

		this.grid.setSelectionMode(Grid.SelectionMode.SINGLE);
		this.grid.addSelectionListener(select -> {
			if (!select.getFirstSelectedItem().isPresent()) {
				return;
			}
			Window window = new Window();
			window.center();
			window.setResizable(false);

			Panel windowRPanel = new Panel();
			VerticalLayout layout = new VerticalLayout();
			windowRPanel.setContent(layout);

			Label usernameLb = VaadinWrapper.builder().caption("Username").build().label();
			usernameLb.setValue(select.getFirstSelectedItem().get().getUsername());
			Label userStatusLb = VaadinWrapper.builder().caption("User Status").build().label();
			userStatusLb.setValue(select.getFirstSelectedItem().get().getUserStatus());
			Label emailLb = VaadinWrapper.builder().caption("Email").build().label();
			emailLb.setValue(select.getFirstSelectedItem().get().getEmail());
			Label genderLb = VaadinWrapper.builder().caption("Gender").build().label();
			genderLb.setValue(select.getFirstSelectedItem().get().getGender());
			Label dateOfBirthLb = VaadinWrapper.builder().caption("Date of birth").build().label();
			dateOfBirthLb.setValue(String.valueOf(select.getFirstSelectedItem().get().getDateOfBirth()));
			Label phoneLb = VaadinWrapper.builder().caption("Phone").build().label();
			phoneLb.setValue(String.valueOf(select.getFirstSelectedItem().get().getPhone()));
			Label groupLb = VaadinWrapper.builder().caption("Group").build().label();
			groupLb.setValue(String.valueOf(select.getFirstSelectedItem().get().getGroup()));
			layout.addComponents(usernameLb, userStatusLb, emailLb, genderLb, dateOfBirthLb, phoneLb, groupLb);
			layout.setSizeFull();
			window.setSizeFull();
			windowRPanel.setSizeFull();
			window.setContent(windowRPanel);
			getUI().addWindow(window);
		});
		VerticalLayout rootLayout = new VerticalLayout();
		Panel rootPanel = new Panel();
		rootPanel.setContent(this.grid);
		HorizontalLayout searchLayout = new HorizontalLayout(this.filterByUsername, this.filterByEmail,
				this.filterByFirstName);
		searchLayout.setSizeFull();
		rootLayout.addComponents(new Panel(new VerticalLayout(searchLayout, rootPanel)));

		Panel designPanel = new Panel();
		designPanel.setContent(rootLayout);
		addComponent(rootLayout);
		this.grid.setSizeFull();
		rootLayout.setSizeFull();
		rootPanel.setSizeFull();

	}

	private void onUsernameFilterTextChange(HasValue.ValueChangeEvent<String> event) {
		ListDataProvider<UserModel> dataProvider = (ListDataProvider<UserModel>) grid.getDataProvider();
		dataProvider.setFilter(UserModel::getUsername, s -> caseInsensitiveContains(s, event.getValue()));
	}

	private void onEmailFilterTextChange(HasValue.ValueChangeEvent<String> event) {
		ListDataProvider<UserModel> dataProvider = (ListDataProvider<UserModel>) grid.getDataProvider();
		dataProvider.setFilter(UserModel::getEmail, s -> caseInsensitiveContains(s, event.getValue()));
	}

	private void onFirstNameFilterTextChange(HasValue.ValueChangeEvent<String> event) {
		ListDataProvider<UserModel> dataProvider = (ListDataProvider<UserModel>) grid.getDataProvider();
		dataProvider.setFilter(UserModel::getFirstName, s -> caseInsensitiveContains(s, event.getValue()));
	}

	private Boolean caseInsensitiveContains(String where, String what) {
		return new StringBuilder().append("").append(where).toString().toLowerCase()
				.contains(new StringBuilder().append("").append(what).toString().toLowerCase());
	}

}
