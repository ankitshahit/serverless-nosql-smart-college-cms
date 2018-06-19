package io.college.cms.core.ui.builder;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class PopupBuilderView extends Window {
	private static final long serialVersionUID = 1L;

	public void show(VerticalLayout layout) {
		setContent(layout);
	}
}
