package io.college.cms.core.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.teemusa.sidemenu.SideMenu;
import org.vaadin.teemusa.sidemenu.SideMenu.MenuRegistration;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import io.college.cms.core.announcement.ui.PublishAnnouncementView;
import io.college.cms.core.announcement.ui.SeeAnnouncementView;
import io.college.cms.core.courses.controller.SeeCoursesView;
import io.college.cms.core.examination.controller.PublishExamView;
import io.college.cms.core.examination.controller.SeeExamsView;
import io.college.cms.core.examination.controller.SeeResultsView;
import io.college.cms.core.ui.model.ViewConstants;
import lombok.var;

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
	private SeeCoursesView seeCourses;
	private PublishExamView publishExam;
	private SeeExamsView seeExam;
	private SideMenu sideMenu = new SideMenu();
	private boolean logoVisible = true;
	// private ThemeResource logo = new
	// ThemeResource("C:\\Users\\Public\\Pictures\\Sample
	// Pictures\\Desert.jpg");
	private String menuCaption = "SideMenu Add-on";

	private MenuRegistration menuToRemove;
	private MenuRegistration subSubTreeItem;

	public HomePageUI() {
		this.navigator = new Navigator(this, this);
	}

	@Autowired
	public void setSeeExam(SeeExamsView seeExam) {
		this.seeExam = seeExam;
	}

	@Autowired
	public void setPublishExam(PublishExamView publishExam) {
		this.publishExam = publishExam;
	}

	@Autowired
	public void setSeeCourses(SeeCoursesView seeCourses) {
		this.seeCourses = seeCourses;
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
		registerViews();
		sideMenus();
		VerticalLayout rootLayout = new VerticalLayout();
		// rootLayout.addComponent(new UIHelper().getMenuBar(navigator));
		rootLayout.addComponent(sideMenu);
		rootLayout.setComponentAlignment(sideMenu, Alignment.TOP_LEFT);
		rootLayout.setSpacing(true);
		rootLayout.setSizeFull();
		setContent(rootLayout);
	}

	void registerViews() {

		addView(ViewConstants.COURSES_CREATE, createCourse);
		addView(ViewConstants.COURSES_VIEW_ALL, seeCourses);
		addView(ViewConstants.PUBLISH_ANNOUNCEMENT, publishAnnouncement);
		addView(ViewConstants.SEE_ANNOUNCEMENT, seeAnnouncement);
		addView(ViewConstants.SEE_RESULTS, seeResults);
		addView(ViewConstants.EXAM_CREATE, publishExam);
		addView(ViewConstants.EXAM_VIEW_ALL, seeExam);
	}

	void addView(String viewName, View view) {
		this.navigator.addView(viewName, view);
	}

	void sideMenus() {
		
		sideMenu.addMenuItem("Exams", () -> {

		});
		sideMenu.addMenuItem("Schedule an Exam", () -> {
			navigator.navigateTo(ViewConstants.EXAM_CREATE);
		});
		sideMenu.addMenuItem("View/Modify Exams", () -> {
			navigator.navigateTo(ViewConstants.EXAM_VIEW_ALL);
		});

		// User menu controls
		sideMenu.addMenuItem("Show/Hide user menu", VaadinIcons.USER,
				() -> sideMenu.setUserMenuVisible(!sideMenu.isUserMenuVisible()));

		menuToRemove = sideMenu.addMenuItem("Remove this menu item", () -> {
			if (menuToRemove != null) {
				menuToRemove.remove();
				menuToRemove = null;
			}
		});
		sideMenu.setHeight("100%");
		sideMenu.setWidth("100%");
		sideMenu.setSizeFull();

	}

	private void initTreeMenu() {

		/*
		 * sideMenu.addTreeItem("Tree item", () ->
		 * Notification.show("Parent!")); sideMenu.addTreeItem("Tree item",
		 * "sub item", () -> { Notification.show("Sub item!");
		 * sideMenu.addComponent(new Button("Add sub sub item", event ->
		 * subSubTreeItem = sideMenu .addTreeItem("sub item", "sub sub item", ()
		 * -> Notification.show("Inception!")))); sideMenu.addComponent(new
		 * Button("Remove sub sub item", event -> { if (null != subSubTreeItem)
		 * { subSubTreeItem.remove(); subSubTreeItem = null; } })); });
		 */

	}
}
