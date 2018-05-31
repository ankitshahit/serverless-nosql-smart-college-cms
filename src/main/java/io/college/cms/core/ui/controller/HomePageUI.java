package io.college.cms.core.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import io.college.cms.core.ui.model.ViewConstants;
import io.college.cms.core.ui.util.UIHelper;

@SpringUI(path = "/homepage")
@UIScope
public class HomePageUI extends UI {

	private static final long serialVersionUID = 1L;
	private ViewAllCoursesUI viewCourses;
	private Navigator navigator;

	public HomePageUI() {
		this.navigator = new Navigator(this, this);
	}

	@Autowired
	public void setViewCourses(ViewAllCoursesUI viewCourses) {
		this.viewCourses = viewCourses;
	}

	@Override
	protected void init(VaadinRequest request) {
		this.navigator.addView(ViewConstants.COURSES_CREATE, CreateCourseUI.class);

		UIHelper uiHelper = new UIHelper();
		VerticalLayout rootLayout = new VerticalLayout();
		rootLayout.setSpacing(true);
		rootLayout.addComponent(uiHelper.getMenuBar(navigator));
		setContent(rootLayout);
	}

}
