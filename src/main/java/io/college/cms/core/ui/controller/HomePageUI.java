package io.college.cms.core.ui.controller;

import static io.college.cms.core.ui.controller.HomePageUI.MenuConstants.ADD_USER;
import static io.college.cms.core.ui.controller.HomePageUI.MenuConstants.CONFIGURE_ADMISSIONS;
import static io.college.cms.core.ui.controller.HomePageUI.MenuConstants.NEW_COURSE;
import static io.college.cms.core.ui.controller.HomePageUI.MenuConstants.OPEN_ADMISSIONS;
import static io.college.cms.core.ui.controller.HomePageUI.MenuConstants.PUBLISH_ANNOUNCEMENT;
import static io.college.cms.core.ui.controller.HomePageUI.MenuConstants.SCHEDULE_EXAM;
import static io.college.cms.core.ui.controller.HomePageUI.MenuConstants.VIEW_ALL_ANNOUNCEMENT;
import static io.college.cms.core.ui.controller.HomePageUI.MenuConstants.VIEW_ALL_COURSES;
import static io.college.cms.core.ui.controller.HomePageUI.MenuConstants.VIEW_ALL_EXAMS;
import static io.college.cms.core.ui.controller.HomePageUI.MenuConstants.VIEW_RESULTS;

import static io.college.cms.core.ui.controller.HomePageUI.MenuConstants.*;
import java.io.File;
import java.net.URLClassLoader;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.navigator.View;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.admission.controller.ApplyAdmissionView;
import io.college.cms.core.admission.controller.ConfigureAdmissionActionView;
import io.college.cms.core.admission.controller.PublishAdmissionView;
import io.college.cms.core.announcement.ui.PublishAnnouncementView;
import io.college.cms.core.announcement.ui.SeeAnnouncementView;
import io.college.cms.core.application.Utils;
import io.college.cms.core.courses.controller.SeeCoursesView;
import io.college.cms.core.examination.controller.PublishExamView;
import io.college.cms.core.examination.controller.SeeExamsView;
import io.college.cms.core.examination.controller.SeeResultsView;
import io.college.cms.core.faq.controller.ChatFaqView;
import io.college.cms.core.ui.model.ViewConstants;
import io.college.cms.core.user.controller.ConfirmUserView;
import io.college.cms.core.user.controller.LoginUserView;
import io.college.cms.core.user.controller.UserView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@SpringUI(path = "/homepage")
@UIScope
@PushStateNavigation
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
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
	private PublishAdmissionView publishAdmission;
	@Autowired
	private ConfigureAdmissionActionView configureAdmission;
	@Autowired
	private LoginUserView loginView;
	@Autowired
	private ApplyAdmissionView applyAdmission;
	private VerticalLayout cssContainer;

	public HomePageUI() {
		this.cssContainer = new VerticalLayout();
		this.navigator = new Navigator(this, cssContainer);
	}

	@Override
	protected void init(VaadinRequest request) {
		registerViews();
		setContent(sideMenuVaadin8());
		// setContent(sideMenuVaadin8());

	}

	void registerViews() {
		addView("", loginView);
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
		addView(ViewConstants.OPEN_ADMISSIONS_BY_COURSE, publishAdmission);
		addView(ViewConstants.CONFIGURE_ADMISSION_PROPERTIES, configureAdmission);
		addView(ViewConstants.APPLY_ADMISSIONS_BY_COURSE, applyAdmission);
		addView(ViewConstants.LOGIN, loginView);
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
		Button openAdmission = new Button("Open Admissions");
		openAdmission.addStyleNames(ValoTheme.BUTTON_BORDERLESS_COLORED, ValoTheme.MENU_ITEM);
		openAdmission.addClickListener(click -> {
			navigator.navigateTo(ViewConstants.OPEN_ADMISSIONS_BY_COURSE);
		});
		Button applyAdmission = new Button("Configure Admissions");
		applyAdmission.addStyleNames(ValoTheme.BUTTON_BORDERLESS_COLORED, ValoTheme.MENU_ITEM);
		applyAdmission.addClickListener(click -> {
			navigator.navigateTo(ViewConstants.CONFIGURE_ADMISSION_PROPERTIES);
		});
		Image image = new Image("");
		try {

			// user_default_icon.png
			image.setSource(new FileResource(new File(new StringBuilder()
					.append(((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs()[0].getFile())
					.append(File.separator).append("META-INF").append(File.separator).append("resources")
					.append(File.separator).append("images").append(File.separator).append("user_default_icon.png")
					.toString())));
			image.setAlternateText("User profile.");
			image.setIcon(VaadinIcons.USERS);
			image.setHeight("20%");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			Utils.showErrorNotification("Unable to load default profile picture");
		}

		/*
		 * CssLayout menu = new CssLayout(title, image, exams, publishExams,
		 * courses, seeCourses, faqBot, user, confirmUser, publishAnnouncement,
		 * seeResults, openAdmission, applyAdmission);
		 */
		Panel menuPanel = new Panel();
		menuPanel.addStyleNames(ValoTheme.PANEL_SCROLL_INDICATOR, ValoTheme.PANEL_BORDERLESS);
		menuPanel.setSizeFull();
		menuPanel.setContent(buildTreeMenu());

		CssLayout menu = new CssLayout(title, menuPanel, image);
		menu.addStyleNames(ValoTheme.MENU_ROOT);
		menu.setResponsive(true);
		menu.setSizeFull();
		cssContainer.setResponsive(true);
		cssContainer.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

		// cssContainer.setWidth("90%");

		HorizontalSplitPanel panel = new HorizontalSplitPanel(menu, cssContainer);

		panel.setSplitPosition(20.0f, Unit.PERCENTAGE, false);
		HorizontalLayout menuLayout = new HorizontalLayout();

		menuLayout.addComponents(panel);
		menuLayout.setComponentAlignment(panel, Alignment.MIDDLE_RIGHT);
		menuLayout.setSizeFull();
		menuLayout.setResponsive(true);
		return menuLayout;
	}

	protected Tree<String> buildTreeMenu() {
		Tree<String> tree = new Tree<>();
		tree.addStyleNames(ValoTheme.PANEL_SCROLL_INDICATOR);
		TreeData<String> treeData = new TreeData<>();

		treeData(treeData, null, "Courses");
		treeData(treeData, "Courses", "View all courses");
		treeData(treeData, "Courses", "New course");
		treeData(treeData, null, "Exams");
		treeData(treeData, "Exams", "View all exams");
		treeData(treeData, "Exams", "Schedule Exam");
		treeData(treeData, "Exams", "View exam timetable");
		treeData(treeData, "Exams", "Download qr for exams");
		treeData(treeData, null, "Admission");
		treeData(treeData, "Admission", "Open Admissions");
		treeData(treeData, "Admission", "Apply Admissions");
		treeData(treeData, "Admission", "Configure Admissions");
		treeData(treeData, "Admission", "Admission request queue");
		treeData(treeData, "Admission", "Fees");
		treeData(treeData, "Admission", "fees queue");
		treeData(treeData, null, "User");
		treeData(treeData, "User", "View all user");
		treeData(treeData, "User", "Add user");
		treeData(treeData, "User", "My profile");
		treeData(treeData, "User", "My documents");
		treeData(treeData, "User", "My notifications");
		treeData(treeData, null, "Results");
		treeData(treeData, "Results", "View results");
		
		treeData(treeData, null, "Announcement");
		treeData(treeData, "Announcement", "Publish Announcement");
		treeData(treeData, "Announcement", "View All Announcements");
		treeData(treeData, null, "FAQ Bot");
		TreeDataProvider<String> dataProvider = new TreeDataProvider<String>(treeData);
		tree.setDataProvider(dataProvider);
		tree.addItemClickListener(item -> {
			String value = item.getItem();
			if (PUBLISH_ANNOUNCEMENT.equalsIgnoreCase(value)) {
				navigator.navigateTo(ViewConstants.PUBLISH_ANNOUNCEMENT);
			} else if (VIEW_ALL_ANNOUNCEMENT.equalsIgnoreCase(value)) {
				navigator.navigateTo(ViewConstants.SEE_ANNOUNCEMENT);
			} else if (ADD_USER.equalsIgnoreCase(value)) {
				navigator.navigateTo(ViewConstants.DEAL_WITH_USER);
			} else if (OPEN_ADMISSIONS.equalsIgnoreCase(value)) {
				navigator.navigateTo(ViewConstants.OPEN_ADMISSIONS_BY_COURSE);
			} else if (CONFIGURE_ADMISSIONS.equalsIgnoreCase(value)) {
				navigator.navigateTo(ViewConstants.CONFIGURE_ADMISSION_PROPERTIES);
			} else if (NEW_COURSE.equalsIgnoreCase(value)) {
				navigator.navigateTo(ViewConstants.COURSES_CREATE);
			} else if (VIEW_ALL_COURSES.equalsIgnoreCase(value)) {
				navigator.navigateTo(ViewConstants.COURSES_VIEW_ALL);
			} else if (SCHEDULE_EXAM.equalsIgnoreCase(value)) {
				navigator.navigateTo(ViewConstants.EXAM_CREATE);
			} else if (VIEW_ALL_EXAMS.equalsIgnoreCase(value)) {
				navigator.navigateTo(ViewConstants.EXAM_VIEW_ALL);
			} else if (VIEW_RESULTS.equalsIgnoreCase(value)) {
				navigator.navigateTo(ViewConstants.SEE_RESULTS);
			} else if (APPLY_ADMISSIONS.equalsIgnoreCase(value)) {
				navigator.navigateTo(ViewConstants.APPLY_ADMISSIONS_BY_COURSE);
			} else if (FAQ_BOT.equalsIgnoreCase(value)) {
				navigator.navigateTo(ViewConstants.CHAT_FAQ_VIEW);
			} else if (PUBLISH_ANNOUNCEMENT.equalsIgnoreCase(value)) {
				navigator.navigateTo(ViewConstants.PUBLISH_ANNOUNCEMENT);
			} else if (PUBLISH_ANNOUNCEMENT.equalsIgnoreCase(value)) {
				navigator.navigateTo(ViewConstants.PUBLISH_ANNOUNCEMENT);
			}
		});
		return tree;
	}

	protected void treeData(TreeData<String> treeData, String parent, String child) {
		treeData.addItem(parent, child);
	}

	public static class MenuConstants {
		static final String ANNOUNCEMENT = "Announcement";
		static final String PUBLISH_ANNOUNCEMENT = "Publish Announcement";
		static final String VIEW_ALL_ANNOUNCEMENT = "View All Announcements";
		static final String USER = "User";
		static final String ADD_USER = "Add user";
		static final String MY_PROFILE = "My profile";
		static final String MY_DOCUMENTS = "My documents";
		static final String MY_NOTIFICATIONS = "My notifications";
		static final String RESULTS = "Results";
		static final String VIEW_RESULTS = "View results";
		static final String FAQ_BOT = "FAQ Bot";
		static final String ADMISSION = "Admission";
		static final String OPEN_ADMISSIONS = "Open Admissions";
		static final String CONFIGURE_ADMISSIONS = "Configure Admissions";
		static final String ADMISSION_REQUEST_QUEUE = "Admission request queue";
		static final String APPLY_ADMISSIONS = "Apply Admissions";
		static final String FEES = "Fees";
		static final String FEES_QUEUE = "fees queue";
		static final String EXAMS = "Exams";
		static final String VIEW_ALL_EXAMS = "View all exams";
		static final String SCHEDULE_EXAM = "Schedule Exam";
		static final String VIEW_EXAM_TIMETABLE = "View exam timetable";
		static final String DOWNLOAD_QR_FOR_EXAMS = "Download qr for exams";
		static final String COURSES = "Courses";
		static final String VIEW_ALL_COURSES = "View all courses";
		static final String NEW_COURSE = "New course";
	}
}
