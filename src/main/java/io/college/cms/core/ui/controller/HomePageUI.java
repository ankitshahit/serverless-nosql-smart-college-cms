package io.college.cms.core.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.teemusa.sidemenu.SideMenu;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.admission.controller.PublishAdmissionView;
import io.college.cms.core.announcement.ui.PublishAnnouncementView;
import io.college.cms.core.announcement.ui.SeeAnnouncementView;
import io.college.cms.core.courses.controller.SeeCoursesView;
import io.college.cms.core.examination.controller.PublishExamView;
import io.college.cms.core.examination.controller.SeeExamsView;
import io.college.cms.core.examination.controller.SeeResultsView;
import io.college.cms.core.faq.controller.ChatFaqView;
import io.college.cms.core.ui.model.ViewConstants;
import io.college.cms.core.user.controller.ConfirmUserView;
import io.college.cms.core.user.controller.UserView;
import lombok.Data;
import lombok.EqualsAndHashCode;

@SpringUI(path = "/homepage")
@UIScope
@PushStateNavigation
@Data
@EqualsAndHashCode(callSuper = false)
public class HomePageUI extends UI {

	private static final long serialVersionUID = 1L;
	@Autowired
	private UserView userView;
	@Deprecated
	@Autowired
	private ViewAllCoursesUI viewCourses;
	private Navigator navigator;
	@Autowired
	private PublishCourseView createCourse;
	@Autowired
	private PublishAnnouncementView publishAnnouncement;
	@Autowired
	private SeeAnnouncementView seeAnnouncement;
	@Autowired
	private SeeResultsView seeResults;
	@Autowired
	private SeeCoursesView seeCourses;
	@Autowired
	private PublishExamView publishExam;
	@Autowired
	private SeeExamsView seeExam;
	@Autowired
	private ChatFaqView chatFaq;
	@Autowired
	private ConfirmUserView confirmUser;
	@Autowired
	private PublishAdmissionView applyAdmission;
	private VerticalLayout cssContainer;

	public HomePageUI() {
		this.cssContainer = new VerticalLayout();
		this.navigator = new Navigator(this, cssContainer);
	}

	@Override
	protected void init(VaadinRequest request) {
		registerViews();
		sideMenus();

		setContent(sideMenuVaadin8());
		// setContent(sideMenuVaadin8());

	}

	void registerViews() {
		addView("", applyAdmission);
		addView(ViewConstants.COURSES_CREATE, createCourse);
		addView(ViewConstants.COURSES_VIEW_ALL, seeCourses);
		addView(ViewConstants.PUBLISH_ANNOUNCEMENT, publishAnnouncement);
		addView(ViewConstants.SEE_ANNOUNCEMENT, seeAnnouncement);
		addView(ViewConstants.SEE_RESULTS, seeResults);
		addView(ViewConstants.EXAM_CREATE, publishExam);
		addView(ViewConstants.EXAM_VIEW_ALL, seeExam);
		addView(ViewConstants.CHAT_FAQ_VIEW, chatFaq);
		addView(ViewConstants.DEAL_WITH_USER, userView);
		addView(ViewConstants.CONFIRM_USER_VIEW, confirmUser);
		addView(ViewConstants.APPLY_ADMISSION, applyAdmission);
	}

	void addView(String viewName, View view) {
		this.navigator.addView(viewName, view);
	}

	HorizontalLayout sideMenuVaadin8() {

		Label title = new Label("College CMS");
		title.setStyleName(ValoTheme.MENU_TITLE);

		Button exams = new Button("Schedule Exams");
		exams.addStyleNames(ValoTheme.BUTTON_BORDERLESS, ValoTheme.MENU_ITEM);
		exams.addClickListener(click -> {
			navigator.navigateTo(ViewConstants.EXAM_VIEW_ALL);
		});
		Button publishExams = new Button("Publish Exams");
		publishExams.addStyleNames(ValoTheme.BUTTON_BORDERLESS_COLORED, ValoTheme.MENU_ITEM);
		publishExams.addClickListener(click -> {
			navigator.navigateTo(ViewConstants.EXAM_CREATE);
		});
		Button courses = new Button("Create Courses");
		courses.addStyleNames(ValoTheme.BUTTON_BORDERLESS, ValoTheme.MENU_ITEM);
		courses.addClickListener(click -> {
			// setContent(createCourse);
			navigator.navigateTo(ViewConstants.COURSES_CREATE);
		});
		Button seeCourses = new Button("See Courses");
		seeCourses.addStyleNames(ValoTheme.BUTTON_BORDERLESS, ValoTheme.MENU_ITEM);
		seeCourses.addClickListener(click -> {
			// setContent(createCourse);
			navigator.navigateTo(ViewConstants.COURSES_VIEW_ALL);
		});

		Button faqBot = new Button("FAQ Bot");
		faqBot.addStyleNames(ValoTheme.BUTTON_BORDERLESS_COLORED, ValoTheme.MENU_ITEM);
		faqBot.addClickListener(click -> {
			navigator.navigateTo(ViewConstants.CHAT_FAQ_VIEW);
		});
		Button user = new Button("User");
		user.addStyleNames(ValoTheme.BUTTON_BORDERLESS_COLORED, ValoTheme.MENU_ITEM);
		user.addClickListener(click -> {
			navigator.navigateTo(ViewConstants.DEAL_WITH_USER);
		});
		Button confirmUser = new Button("Confirm user");
		confirmUser.addStyleNames(ValoTheme.BUTTON_BORDERLESS_COLORED, ValoTheme.MENU_ITEM);
		confirmUser.addClickListener(click -> {
			navigator.navigateTo(ViewConstants.CONFIRM_USER_VIEW);
		});

		Button publishAnnouncement = new Button("Announcement");
		publishAnnouncement.addStyleNames(ValoTheme.BUTTON_BORDERLESS_COLORED, ValoTheme.MENU_ITEM);
		publishAnnouncement.addClickListener(click -> {
			navigator.navigateTo(ViewConstants.PUBLISH_ANNOUNCEMENT);
		});
		Button seeResults = new Button("Results");
		seeResults.addStyleNames(ValoTheme.BUTTON_BORDERLESS_COLORED, ValoTheme.MENU_ITEM);
		seeResults.addClickListener(click -> {
			navigator.navigateTo(ViewConstants.SEE_RESULTS);
		});
		Button applyAdmission = new Button("Open Admissions");
		applyAdmission.addStyleNames(ValoTheme.BUTTON_BORDERLESS_COLORED, ValoTheme.MENU_ITEM);
		applyAdmission.addClickListener(click -> {
			navigator.navigateTo(ViewConstants.APPLY_ADMISSION);
		});
		CssLayout menu = new CssLayout(title, exams, publishExams, courses, seeCourses, faqBot, user, confirmUser,
				publishAnnouncement, seeResults, applyAdmission);
		menu.addStyleNames(ValoTheme.MENU_ROOT);
		menu.setResponsive(true);
		menu.setSizeFull();
		cssContainer.setResponsive(true);
		cssContainer.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

		// cssContainer.setWidth("90%");

		HorizontalSplitPanel panel = new HorizontalSplitPanel(menu, cssContainer);

		panel.setSplitPosition(15.0f, Unit.PERCENTAGE, false);
		HorizontalLayout menuLayout = new HorizontalLayout();

		menuLayout.addComponents(panel);
		menuLayout.setComponentAlignment(panel, Alignment.MIDDLE_RIGHT);
		menuLayout.setSizeFull();
		menuLayout.setResponsive(true);
		return menuLayout;
	}

	@Deprecated
	void sideMenus() {
		SideMenu sideMenu = new SideMenu();
		sideMenu.addMenuItem("Help!", VaadinIcons.COG, () -> {

		});

		sideMenu.addMenuItem("Ask FAQ Bot", VaadinIcons.CLIPBOARD_USER, () -> {
			sideMenu.setContent(chatFaq);
		});
		sideMenu.addMenuItem("Chat with others!", () -> {

		});
		sideMenu.addMenuItem("Exams", () -> {

		});
		sideMenu.addMenuItem("Schedule an Exam", () -> {
			sideMenu.setContent(publishExam);
			// navigator.navigateTo(ViewConstants.EXAM_CREATE);
		});
		sideMenu.addMenuItem("View/Modify Exams", () -> {
			sideMenu.setContent(seeExam);
			// navigator.navigateTo(ViewConstants.EXAM_VIEW_ALL);
		});

		// User menu controls
		sideMenu.addMenuItem("Show/Hide user menu", VaadinIcons.USER,
				() -> sideMenu.setUserMenuVisible(!sideMenu.isUserMenuVisible()));

	}

}
