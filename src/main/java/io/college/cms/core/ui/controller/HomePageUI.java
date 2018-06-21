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
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

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
	private HorizontalLayout cssContainer;

	public HomePageUI() {
		this.cssContainer = new HorizontalLayout();
		this.navigator = new Navigator(this, cssContainer);
	}

	/*
	 * @Autowired public void setConfirmUser(ConfirmUserView confirmUser) {
	 * this.confirmUser = confirmUser; }
	 * 
	 * @Autowired public void setUserView(UserView userView) { this.userView =
	 * userView; }
	 * 
	 * @Autowired public void setChatFaq(ChatFaqView chatFaq) { this.chatFaq =
	 * chatFaq; }
	 * 
	 * @Autowired public void setSeeExam(SeeExamsView seeExam) { this.seeExam =
	 * seeExam; }
	 * 
	 * @Autowired public void setPublishExam(PublishExamView publishExam) {
	 * this.publishExam = publishExam; }
	 * 
	 * @Autowired public void setSeeCourses(SeeCoursesView seeCourses) {
	 * this.seeCourses = seeCourses; }
	 * 
	 * @Autowired public void setSeeResults(SeeResultsView seeResults) {
	 * this.seeResults = seeResults; }
	 * 
	 * @Autowired public void setSeeAnnouncement(SeeAnnouncementView
	 * seeAnnouncement) { this.seeAnnouncement = seeAnnouncement; }
	 * 
	 * @Autowired public void setViewCourses(ViewAllCoursesUI viewCourses) {
	 * this.viewCourses = viewCourses; }
	 * 
	 * @Autowired public void setCreateCourse(CreateCourseView createCourse) {
	 * this.createCourse = createCourse; }
	 * 
	 * @Autowired public void setPublishAnnouncement(PublishAnnouncementView
	 * publishAnnouncement) { this.publishAnnouncement = publishAnnouncement; }
	 */
	@Override
	protected void init(VaadinRequest request) {
		registerViews();
		sideMenus();

		setContent(sideMenuVaadin8());
		// setContent(sideMenuVaadin8());

	}

	void registerViews() {
		addView("", createCourse);
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
	}

	void addView(String viewName, View view) {
		this.navigator.addView(viewName, view);
	}

	HorizontalLayout sideMenuVaadin8() {

		Label title = new Label("College CMS");
		title.setStyleName(ValoTheme.MENU_TITLE);

		Button exams = new Button("Exams");
		exams.addStyleNames(ValoTheme.BUTTON_BORDERLESS, ValoTheme.MENU_ITEM);
		exams.addClickListener(click -> {
			navigator.navigateTo(ViewConstants.EXAM_VIEW_ALL);
		});
		Button courses = new Button("Courses");
		courses.addStyleNames(ValoTheme.BUTTON_BORDERLESS, ValoTheme.MENU_ITEM);
		courses.addClickListener(click -> {
			// setContent(createCourse);
			navigator.navigateTo(ViewConstants.COURSES_CREATE);
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
		Button publishExams = new Button("Exams");
		publishExams.addStyleNames(ValoTheme.BUTTON_BORDERLESS_COLORED, ValoTheme.MENU_ITEM);
		publishExams.addClickListener(click -> {
			navigator.navigateTo(ViewConstants.EXAM_CREATE);
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
		CssLayout menu = new CssLayout(title, exams, courses, faqBot, user, confirmUser, publishExams,
				publishAnnouncement, seeResults);
		menu.addStyleNames(ValoTheme.MENU_ROOT);
		menu.setResponsive(true);
		menu.setSizeFull();
		cssContainer.setResponsive(true);
		// cssContainer.setWidth("90%");

		HorizontalSplitPanel panel = new HorizontalSplitPanel(menu, cssContainer);

		panel.setSplitPosition(9.05F, Unit.PERCENTAGE, false);
		HorizontalLayout menuLayout = new HorizontalLayout();
		menuLayout.addComponents(panel);
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
