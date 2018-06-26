package io.college.cms.core.faq.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.faq.service.FaqBotService;
import io.college.cms.core.ui.builder.VaadinWrapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ChatFaqView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;

	private Button send;
	private TextField typeMessage;
	private Panel chatBoxLayout;
	private HorizontalLayout horizontalLayout;
	private FaqBotService botService;

	@Autowired
	public ChatFaqView(FaqBotService botService) {
		this.botService = botService;
	}

	@PostConstruct
	protected void paint() {
		Label richText = new Label();
		richText.setContentMode(ContentMode.HTML);

		send = new Button();
		typeMessage = new TextField();
		chatBoxLayout = new Panel();
		horizontalLayout = new HorizontalLayout();

		send.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		send.setIcon(VaadinIcons.ARROW_RIGHT);
		send.setClickShortcut(KeyCode.ENTER);

		typeMessage.setPlaceholder("Type message...");
		horizontalLayout.addComponents(typeMessage, send);
		horizontalLayout.setComponentAlignment(typeMessage, Alignment.MIDDLE_RIGHT);
		horizontalLayout.setSizeFull();

		/*
		 * chatBoxLayout.setWidth("50%"); chatBoxLayout.setHeight("60%");
		 */
		addComponents(chatBoxLayout);

		// setComponentAlignment(chatBoxLayout, Alignment.MIDDLE_CENTER);

		richText.setValue(" <p><b style=color:red>&nbsp;Bot</b>: Connected to server. Ask queries below :) </p>");
		richText.setSizeFull();
		typeMessage.setSizeFull();
		horizontalLayout.setSizeFull();
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.addComponents(richText, horizontalLayout);
		// verticalLayout.setComponentAlignment(richText, Alignment.TOP_CENTER);
		// verticalLayout.setComponentAlignment(horizontalLayout,
		// Alignment.MIDDLE_CENTER);
		Label message = VaadinWrapper.builder().build().label();
		message.setValue("<h2>Bot</h2>");

		verticalLayout.setSizeFull();
		verticalLayout.setMargin(true);
		typeMessage.setSizeFull();
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(message, verticalLayout);
		splitPanel.setSplitPosition(30.0f);
		splitPanel.setSizeFull();
		VerticalLayout rootLayout = new VerticalLayout();
		rootLayout.addComponents(splitPanel);
		rootLayout.setSizeFull();
		chatBoxLayout.setContent(rootLayout);

		send.addClickListener(click -> {

			if (!typeMessage.getOptionalValue().isPresent()) {
				return;
			}
			String escapedValue = HtmlUtils.htmlEscape(typeMessage.getValue());
			typeMessage.setValue("");
			richText.setValue(new StringBuilder().append(" ").append(richText.getValue()).append(" <p>")
					.append("<b>&nbsp;You:</b> ").append(escapedValue).append("</p>").toString());
			richText.setCaption("Connect with bot for FAQ");
			String updateBotResponse = "";
			try {
				updateBotResponse = botService.getFirstAnswer(escapedValue);
			} catch (ApplicationException e) {
				LOGGER.error(e.getMessage());
				updateBotResponse = new StringBuilder().append(" We couldn't get a response from bot.").toString();
			}
			richText.setValue(
					new StringBuilder().append(richText.getValue()).append("<p><b style=color:red>&nbsp;Bot</b>:")
							.append(updateBotResponse).append("</p>").toString());

		});

	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {

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
