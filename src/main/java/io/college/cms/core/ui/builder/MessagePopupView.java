package io.college.cms.core.ui.builder;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

import io.college.cms.core.ui.util.ListenerUtility;

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
	private HorizontalSplitPanel splitPanel;

	protected void initUI(String caption, String description, float splitPosition) {
		setResizable(false);
		setClosable(true);
		center();

		this.headerVerticalLayout = new VerticalLayout();
		this.bodyVerticalLayout = new VerticalLayout();
		this.verticalSplitPanel = new VerticalSplitPanel();

		this.headerCaption = new Label();
		this.headerCaption.setCaptionAsHtml(true);
		this.headerCaption.setContentMode(ContentMode.HTML);
		this.bodyDescription = new Label();
		this.bodyDescription.setCaptionAsHtml(true);
		this.bodyDescription.setContentMode(ContentMode.HTML);
		this.headerCaption
				.setValue(new StringBuilder().append("<p><h2>").append(caption).append("</h2></p>").toString());
		this.bodyDescription.setValue(new StringBuilder().append("<p style=word-wrap:break-word>").append(description)
				.append("</p>").toString());
		this.headerVerticalLayout.addComponent(this.headerCaption);
		this.bodyVerticalLayout.addComponent(this.bodyDescription);

		this.verticalSplitPanel.addComponents(this.headerVerticalLayout, this.bodyVerticalLayout);
		splitPanel = new HorizontalSplitPanel(this.headerVerticalLayout, this.bodyVerticalLayout);
		splitPanel.setSplitPosition(splitPosition);

		VerticalLayout rootLayout = new VerticalLayout();
		rootLayout.addComponents(splitPanel);
		setContent(rootLayout);

		addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), this)) {
				return;
			}
			close();
		});
	}

	public MessagePopupView(String caption, String description) {
		super();
		this.splitPanel = new HorizontalSplitPanel();
		initUI(caption, description, splitPanel.getWidth() - 75.0f);
	}

	public MessagePopupView(String caption, String description, float split) {
		super();
		initUI(caption, description, split);
	}

}
