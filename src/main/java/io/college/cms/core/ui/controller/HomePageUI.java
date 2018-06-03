package io.college.cms.core.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import io.college.cms.core.announcement.ui.PublishAnnouncementView;
import io.college.cms.core.announcement.ui.SeeAnnouncementView;
import io.college.cms.core.examination.controller.SeeResultsView;
import io.college.cms.core.ui.model.ViewConstants;
import io.college.cms.core.ui.util.UIHelper;

@SpringUI(path = "/homepage")
@UIScope
public class HomePageUI extends UI {

	private static final long serialVersionUID = 1L;
	private ViewAllCoursesUI viewCourses;
	private Navigator navigator;
	private CreateCourseView createCourse;
	private PublishAnnouncementView publishAnnouncement;
	private SeeAnnouncementView seeAnnouncement;
	private SeeResultsView seeResults;

	public HomePageUI() {
		this.navigator = new Navigator(this, this);
	}

	@Autowired
	public void setSeeResults(SeeResultsView seeResults) {
		this.seeResults = seeResults;
	}

	@Autowired
	public void setSeeAnnouncement(SeeAnnouncementView seeAnnouncement) {
		this.seeAnnouncement = seeAnnouncement;
	}

	@Autowired
	public void setViewCourses(ViewAllCoursesUI viewCourses) {
		this.viewCourses = viewCourses;
	}

	@Autowired
	public void setCreateCourse(CreateCourseView createCourse) {
		this.createCourse = createCourse;
	}

	@Autowired
	public void setPublishAnnouncement(PublishAnnouncementView publishAnnouncement) {
		this.publishAnnouncement = publishAnnouncement;
	}

	@Override
	protected void init(VaadinRequest request) {
		this.navigator.addView("", seeResults);
		this.navigator.addView(ViewConstants.COURSES_CREATE, createCourse);
		this.navigator.addView(ViewConstants.PUBLISH_ANNOUNCEMENT, publishAnnouncement);
		this.navigator.addView(ViewConstants.SEE_ANNOUNCEMENT, seeAnnouncement);
		this.navigator.addView(ViewConstants.SEE_RESULTS, seeResults);
		UIHelper uiHelper = new UIHelper();
		VerticalLayout rootLayout = new VerticalLayout();
		rootLayout.setSpacing(true);
		rootLayout.addComponent(uiHelper.getMenuBar(navigator));
		setContent(rootLayout);
	}

}
