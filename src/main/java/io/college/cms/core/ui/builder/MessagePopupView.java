package io.college.cms.core.ui.builder;

import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

public class MessagePopupView extends Window {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VerticalSplitPanel verticalSplitPanel;
	private VerticalLayout headerVerticalLayout;
	private VerticalLayout bodyVerticalLayout;
	private Label headerCaption;
	private Label bodyDescription;

	public MessagePopupView(String caption, String description) {
		super();
		setResizable(false);
		setClosable(true);
		center();

		this.headerVerticalLayout = new VerticalLayout();
		this.bodyVerticalLayout = new VerticalLayout();
		this.verticalSplitPanel = new VerticalSplitPanel();

		this.headerCaption = new Label();
		this.headerCaption.setCaptionAsHtml(true);
		this.bodyDescription = new Label();
		this.bodyDescription.setCaptionAsHtml(true);
		this.headerCaption
				.setCaption(new StringBuilder().append("<p><h2>").append(caption).append("</h2></p>").toString());
		this.bodyDescription.setCaption(new StringBuilder().append("<p style=word-wrap:break-word>").append(description)
				.append("</p>").toString());
		this.headerVerticalLayout.addComponent(this.headerCaption);
		this.bodyVerticalLayout.addComponent(this.bodyDescription);

		this.verticalSplitPanel.addComponents(this.headerVerticalLayout, this.bodyVerticalLayout);
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(this.headerVerticalLayout, this.bodyVerticalLayout);
		splitPanel.setSplitPosition(splitPanel.getWidth() - 75.0f);

		VerticalLayout rootLayout = new VerticalLayout();
		rootLayout.addComponents(splitPanel);
		setContent(rootLayout);
	}
}
