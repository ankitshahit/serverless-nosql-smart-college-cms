package io.college.cms.core.ui.builder;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class DeletePopupView extends PopupBuilderView {
	private static final long serialVersionUID = 1L;
	private Button delete;

	public DeletePopupView() {

		setClosable(true);
		center();
		setResizable(true);
		setWidth("50%");
		center();
		setResizable(false);

		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.addComponent(new Label("You're about to delete content, once deleted it cannot be recovered."));
		HorizontalLayout hLayout = new HorizontalLayout();
		this.delete = new Button("Delete");
		Button close = new Button("Close");
		delete.setStyleName(ValoTheme.BUTTON_DANGER);
		close.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		close.addClickListener(clickUser -> {
			close();
		});
		hLayout.addComponents(close, delete);
		verticalLayout.addComponent(hLayout);
		verticalLayout.setComponentAlignment(hLayout, Alignment.BOTTOM_RIGHT);
		setContent(verticalLayout);
	}

	public Notification getDeleteNotification() {
		Notification notifi = Notification.show("", Type.HUMANIZED_MESSAGE);
		notifi.setDelayMsec(Notification.DELAY_FOREVER);
		notifi.setCaption("Deleted successfully.");
		notifi.setDescription(
				"The content is now deleted, please refresh the page incase you're still seeing the content available");
		return notifi;
	}

	public Notification getUnsuccessfullNotification(String msg) {
		Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
		notifi.setDelayMsec(Notification.DELAY_FOREVER);
		notifi.setCaption("Unable to delete.");
		notifi.setDescription(msg);
		return notifi;
	}

	protected void deleteListener(Button.ClickListener clickEvent) {
		delete.addClickListener(clickEvent);
	}

	/**
	 * 
	 * @param ui
	 * @param clickEvent
	 */
	public void show(UI ui, Button.ClickListener clickEvent) {
		LOGGER.debug("-> popup being shown.");
		deleteListener(clickEvent);
		ui.addWindow(this);
	}
}
