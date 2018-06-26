package io.college.cms.core.notification.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SeeNotificationView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private TabSheet tabSheet;
	private List<Label> notificationLabels;

	@PostConstruct
	protected void paint() {
		this.tabSheet = new TabSheet();
		this.notificationLabels = new ArrayList<>();
		for (int index = 0; index < 8; index++) {
			Label label = new Label();
			label.setContentMode(ContentMode.HTML);
			label.setCaptionAsHtml(true);
			label.setValue("<hr width=100%/><b><center>No notifications!!!</center></b><br/><hr width=100%/>");
			label.setWidth(100.0f, Unit.PERCENTAGE);
			this.notificationLabels.add(label);
		}
		for (int index = 0; index < this.notificationLabels.size(); index++) {
			this.tabSheet.addTab(this.notificationLabels.get(index), " Tab " + index);
		}

		Panel panel = new Panel();
		this.tabSheet.setHeight(100.0f, Unit.PERCENTAGE);
		this.tabSheet.setSizeFull();
		panel.setSizeFull();
		this.setSizeFull();
		panel.setContent(this.tabSheet);
		this.addComponent(panel);
	}
}
