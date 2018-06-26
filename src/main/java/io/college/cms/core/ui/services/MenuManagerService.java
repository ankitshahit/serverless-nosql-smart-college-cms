package io.college.cms.core.ui.services;

import static io.college.cms.core.ui.services.MenuConstants.ADD_USER;
import static io.college.cms.core.ui.services.MenuConstants.APPLY_ADMISSIONS;
import static io.college.cms.core.ui.services.MenuConstants.CONFIGURE_ADMISSIONS;
import static io.college.cms.core.ui.services.MenuConstants.FAQ_BOT;
import static io.college.cms.core.ui.services.MenuConstants.NEW_COURSE;
import static io.college.cms.core.ui.services.MenuConstants.OPEN_ADMISSIONS;
import static io.college.cms.core.ui.services.MenuConstants.PUBLISH_ANNOUNCEMENT;
import static io.college.cms.core.ui.services.MenuConstants.SCHEDULE_EXAM;
import static io.college.cms.core.ui.services.MenuConstants.VIEW_ALL_ANNOUNCEMENT;
import static io.college.cms.core.ui.services.MenuConstants.VIEW_ALL_COURSES;
import static io.college.cms.core.ui.services.MenuConstants.VIEW_ALL_EXAMS;
import static io.college.cms.core.ui.services.MenuConstants.VIEW_RESULTS;
import static io.college.cms.core.ui.services.MenuConstants.*;
import java.io.File;
import java.net.URLClassLoader;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ItemClick;
import com.vaadin.ui.Tree.ItemClickListener;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.Utils;
import io.college.cms.core.ui.model.ViewConstants;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class MenuManagerService {

	private Navigator navigator;

	/**
	 * @param navigator
	 */
	public MenuManagerService() {
		super();
	}

	public void setNavigator(Navigator navigator) {
		this.navigator = navigator;
	}

	public CssLayout menu() {
		Label title = new Label("College CMS");
		title.setStyleName(ValoTheme.MENU_TITLE);

		Panel menuPanel = new Panel();
		menuPanel.addStyleNames(ValoTheme.PANEL_SCROLL_INDICATOR, ValoTheme.PANEL_BORDERLESS);
		menuPanel.setSizeFull();
		menuPanel.setContent(buildTreeMenu());

		CssLayout menu = new CssLayout(title, menuPanel);
		menu.addStyleNames(ValoTheme.MENU_ROOT);
		menu.setResponsive(true);
		menu.setSizeFull();

		return menu;
	}

	@Deprecated
	protected HorizontalLayout sideMenuVaadin8() {

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
		/*
		 * cssContainer.setResponsive(true);
		 * cssContainer.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		 * 
		 * // cssContainer.setWidth("90%");
		 * 
		 * HorizontalSplitPanel panel = new HorizontalSplitPanel(menu,
		 * cssContainer);
		 * 
		 * panel.setSplitPosition(20.0f, Unit.PERCENTAGE, false);
		 */
		HorizontalLayout menuLayout = new HorizontalLayout();

		/*
		 * menuLayout.addComponents(panel);
		 * menuLayout.setComponentAlignment(panel, Alignment.MIDDLE_RIGHT);
		 */ menuLayout.setSizeFull();
		menuLayout.setResponsive(true);
		return menuLayout;
	}

	public Tree<String> buildTreeMenu() {
		Tree<String> tree = new Tree<>();
		tree.addStyleNames(ValoTheme.PANEL_SCROLL_INDICATOR);
		TreeData<String> treeData = new TreeData<>();

		treeData(treeData, null, COURSES);
		treeData(treeData, COURSES, "View all courses");
		treeData(treeData, COURSES, "New course");
		treeData(treeData, null, EXAMS);
		treeData(treeData, EXAMS, "View all exams");
		treeData(treeData, EXAMS, "Schedule Exam");
		treeData(treeData, EXAMS, "View exam timetable");
		treeData(treeData, EXAMS, "Download qr for exams");

		treeData(treeData, null, ADMISSION);
		treeData(treeData, ADMISSION, "Open Admissions");
		treeData(treeData, ADMISSION, "Apply Admissions");
		treeData(treeData, ADMISSION, "Configure Admissions");
		treeData(treeData, ADMISSION, "Admission request queue");
		treeData(treeData, ADMISSION, FEES);
		treeData(treeData, ADMISSION, CONFIGURE_FEES);
		treeData(treeData, ADMISSION, PUBLISH_FEES);

		treeData(treeData, null, USER);
		treeData(treeData, USER, "View all user");
		treeData(treeData, USER, "Add user");
		treeData(treeData, USER, "My profile");
		treeData(treeData, USER, "My documents");
		treeData(treeData, USER, "My notifications");
		treeData(treeData, USER, "Configure Notifications");
		treeData(treeData, null, RESULTS);
		treeData(treeData, RESULTS, "View results");

		treeData(treeData, null, ANNOUNCEMENT);
		treeData(treeData, ANNOUNCEMENT, "Publish Announcement");
		treeData(treeData, ANNOUNCEMENT, "View All Announcements");
		treeData(treeData, null, JOB);
		treeData(treeData, JOB, "Publish Job");
		treeData(treeData, null, "FAQ Bot");

		TreeDataProvider<String> dataProvider = new TreeDataProvider<String>(treeData);
		tree.setDataProvider(dataProvider);
		tree.addItemClickListener(manageItemClickListener());
		return tree;
	}

	protected void treeData(TreeData<String> treeData, String parent, String child) {
		treeData.addItem(parent, child);
	}

	public ItemClickListener manageItemClickListener() {
		return new ItemClickListener<String>() {

			@Override
			public void itemClick(ItemClick<String> event) {
				String value = event.getItem();
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
				} else if (CONFIGURE_NOTIFICATIONS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.CONFIGURE_NOTIFICATION);
				} else if (MY_DOCUMENTS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.MY_DOCUMENTS);
				} else if (PUBLISH_JOB.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.PUBLISH_JOB);
				} else if (CONFIGURE_FEES.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.CONFIGURE_FEES);
				} else if (DOWNLOAD_QR_FOR_EXAMS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.EXAM_QR_DOWNLOAD);
				} else if (PUBLISH_FEES.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.PUBLISH_FEES);
				}

			}
		};
	}

}
