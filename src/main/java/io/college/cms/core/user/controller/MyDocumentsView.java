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
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.services.CoreUiService;
import io.college.cms.core.upload.model.UploadModel;
import io.college.cms.core.user.service.IUserService;
import io.college.cms.core.user.service.SecurityService;
import io.college.cms.core.user.service.UserCognitoService;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class MyDocumentsView extends VerticalLayout implements View {
	private static final long serialVersionUID = 1L;
	private ApplicationContext app;
	private CoreUiService uiService;
	private Grid<UploadModel> grid;
	private SecurityService securityService;
	private IUserService userService;
	private TextField filterByName;
	private TextField filterByTag;

	/**
	 * @param uiService
	 */
	@Autowired
	public MyDocumentsView(ApplicationContext app, CoreUiService uiService) {
		super();
		this.app = app;
		this.uiService = uiService;
		securityService = this.app.getBean(SecurityService.class);
		userService = this.app.getBean(UserCognitoService.class);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			if (SecurityService.ANONYMOUS_USER.equalsIgnoreCase(securityService.getPrincipal())) {
				return;
			}
			this.uiService.setItemsUpload(this.grid, securityService.getPrincipal(),
					userService.findByUsername(securityService.getPrincipal()).getGroup());
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
		this.filterByName = VaadinWrapper.builder().caption("Filter by username").placeholder("type username").build()
				.textField();
		this.filterByTag = VaadinWrapper.builder().caption("Filter by tag").placeholder("type tag").build().textField();

		this.grid.addColumn(UploadModel::getUsername).setCaption("Username");
		this.grid.addColumn(UploadModel::getTag).setCaption("Tag");
		this.grid.addColumn(UploadModel::getUploadedOn).setCaption("username");
		this.filterByName.addValueChangeListener(this::onUsernameFilterTextChange);
		this.filterByTag.addValueChangeListener(this::onTagFilterTextChange);
		VerticalLayout rootLayout = new VerticalLayout();
		Panel rootPanel = new Panel();
		rootPanel.setContent(this.grid);
		rootLayout.addComponents(new HorizontalLayout(this.filterByName, this.filterByTag), rootPanel);

		Panel designPanel = new Panel();
		designPanel.setContent(rootLayout);
		addComponent(rootLayout);
		this.grid.setSelectionMode(Grid.SelectionMode.SINGLE);
		this.grid.addSelectionListener(select -> {
			if (!select.getFirstSelectedItem().isPresent()) {
				return;
			}
			UploadModel model = select.getFirstSelectedItem().get();
			Window window = new Window();
			window.center();
			window.setResizable(false);

			VerticalLayout layout = new VerticalLayout();
			Panel panel = new Panel();
			panel.setContent(layout);
			Label usernameLbl = VaadinWrapper.builder().caption("Username").build().label();
			usernameLbl.setValue(model.getUsername());
			Label tagLbl = VaadinWrapper.builder().caption("Tag").build().label();
			tagLbl.setValue(model.getTag());
			Label s3DownloadLink = VaadinWrapper.builder().caption("Download file").build().label();
			s3DownloadLink.setValue(model.getS3BucketLink());
			Label uploadedLbl = VaadinWrapper.builder().caption("Uploaded on").build().label();
			uploadedLbl.setValue(String.valueOf(model.getUploadedOn()));
			layout.addComponents(usernameLbl, tagLbl, s3DownloadLink, uploadedLbl);

			layout.setSizeUndefined();
			window.setSizeUndefined();
			window.setContent(panel);
			getUI().addWindow(window);
		});
		this.grid.setSizeFull();
		rootLayout.setSizeFull();
		rootPanel.setSizeFull();

	}

	private void onUsernameFilterTextChange(HasValue.ValueChangeEvent<String> event) {
		ListDataProvider<UploadModel> dataProvider = (ListDataProvider<UploadModel>) grid.getDataProvider();
		dataProvider.setFilter(UploadModel::getUsername, s -> caseInsensitiveContains(s, event.getValue()));
	}

	private void onTagFilterTextChange(HasValue.ValueChangeEvent<String> event) {
		ListDataProvider<UploadModel> dataProvider = (ListDataProvider<UploadModel>) grid.getDataProvider();
		dataProvider.setFilter(UploadModel::getTag, s -> caseInsensitiveContains(s, event.getValue()));
	}

	private Boolean caseInsensitiveContains(String where, String what) {
		return where.toLowerCase().contains(what.toLowerCase());
	}

}
