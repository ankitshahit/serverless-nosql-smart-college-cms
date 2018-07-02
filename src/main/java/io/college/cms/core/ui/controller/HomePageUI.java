package io.college.cms.core.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.annotations.Push;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import io.college.cms.core.ui.listener.SecurityListener;
import io.college.cms.core.ui.services.MenuManagerService;
import io.college.cms.core.ui.services.ViewManagerService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@SpringUI(path = "/homepage")
@UIScope
@PushStateNavigation
@Push
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Service()
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class HomePageUI extends UI {

	private static final long serialVersionUID = 1L;

	private Navigator navigator;

	private VerticalLayout cssContainer;
	private MenuManagerService menuManager;
	private ViewManagerService viewManager;
	private SecurityListener securityListener;

	@Autowired
	public HomePageUI(ViewManagerService viewManager, MenuManagerService menuManager) {
		this.cssContainer = new VerticalLayout();
		this.navigator = new Navigator(this, cssContainer);
		this.viewManager = viewManager;
		this.menuManager = menuManager;
		this.viewManager.setNavigator(navigator);
		this.menuManager.setNavigator(navigator);
		this.viewManager.registerViews();
	}

	@Autowired
	public void setSecurityListener(SecurityListener s) {
		this.securityListener = s;
	}

	@Override
	protected void init(VaadinRequest request) {
		try {

			HorizontalSplitPanel panel = new HorizontalSplitPanel(menuManager.menu(), this.cssContainer);

			panel.setSplitPosition(18.05f, Unit.PERCENTAGE, false);
			HorizontalLayout menuLayout = new HorizontalLayout();

			menuLayout.addComponents(panel);
			menuLayout.setComponentAlignment(panel, Alignment.MIDDLE_RIGHT);
			menuLayout.setSizeFull();
			menuLayout.setResponsive(true);
			setContent(menuLayout);
			this.navigator.addViewChangeListener(securityListener);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
			notifi.setCaption("Application error");
			notifi.setIcon(VaadinIcons.STOP_COG);
			notifi.setDescription(
					"We were unable to process request for some reason! Please try again later or contact admin");
			notifi.setDelayMsec(Notification.DELAY_FOREVER);

		}
	}

}
