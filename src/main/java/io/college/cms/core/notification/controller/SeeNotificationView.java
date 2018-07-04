package io.college.cms.core.notification.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import io.college.cms.core.notification.services.NotificationResponseService;
import io.college.cms.core.ui.services.CoreUiService;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SeeNotificationView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private TabSheet tabSheet;
	private List<Label> notificationLabels;
	private NotificationResponseService notificationResponseService;
	private CoreUiService uiService;

	@Autowired
	public SeeNotificationView(NotificationResponseService notificationResponseService, CoreUiService uiService) {
		super();
		this.notificationResponseService = notificationResponseService;
		this.uiService = uiService;
	}

	@PostConstruct
	protected void paint() {
		this.tabSheet = new TabSheet();
		this.notificationLabels = new ArrayList<>();
		for (int index = 0; index < 7; index++) {
			Label label = new Label();
			label.setContentMode(ContentMode.HTML);
			label.setCaptionAsHtml(true);
			label.setValue("<hr width=100%/><b><center>No notifications!!!</center></b><br/><hr width=100%/>");
			label.setWidth(100.0f, Unit.PERCENTAGE);
			this.notificationLabels.add(label);
		}
		int index = 0;
		this.tabSheet.addTab(this.notificationLabels.get(index++), " Admission");
		this.tabSheet.addTab(this.notificationLabels.get(index++), " Announcement");
		this.tabSheet.addTab(this.notificationLabels.get(index++), " Exams");
		this.tabSheet.addTab(this.notificationLabels.get(index++), " My documents");
		this.tabSheet.addTab(this.notificationLabels.get(index++), " Jobs");
		this.tabSheet.addTab(this.notificationLabels.get(index++), " Results");
		this.tabSheet.addTab(this.notificationLabels.get(index++), " Others");

		Panel panel = new Panel();
		this.tabSheet.setHeight(100.0f, Unit.PERCENTAGE);
		this.tabSheet.setSizeFull();
		panel.setSizeFull();
		this.setSizeFull();
		panel.setContent(this.tabSheet);
		this.addComponent(panel);
	}

}
