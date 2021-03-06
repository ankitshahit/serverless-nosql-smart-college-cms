package io.college.cms.core.ui.services;

import static io.college.cms.core.ui.services.MenuConstants.ADD_TO_GROUP;
import static io.college.cms.core.ui.services.MenuConstants.ADD_USER;
import static io.college.cms.core.ui.services.MenuConstants.ADMISSION;
import static io.college.cms.core.ui.services.MenuConstants.ADMISSION_REQUEST_QUEUE;
import static io.college.cms.core.ui.services.MenuConstants.ANNOUNCEMENT;
import static io.college.cms.core.ui.services.MenuConstants.APPLY_ADMISSIONS;
import static io.college.cms.core.ui.services.MenuConstants.ATTENDANCE;
import static io.college.cms.core.ui.services.MenuConstants.CONFIGURE_ADMISSIONS;
import static io.college.cms.core.ui.services.MenuConstants.CONFIGURE_FEES;
import static io.college.cms.core.ui.services.MenuConstants.CONFIGURE_NOTIFICATIONS;
import static io.college.cms.core.ui.services.MenuConstants.COURSES;
import static io.college.cms.core.ui.services.MenuConstants.DOWNLOAD_QR_FOR_EXAMS;
import static io.college.cms.core.ui.services.MenuConstants.EXAMS;
import static io.college.cms.core.ui.services.MenuConstants.FAQ_BOT;
import static io.college.cms.core.ui.services.MenuConstants.FEES_QUEUE;
import static io.college.cms.core.ui.services.MenuConstants.JOB;
import static io.college.cms.core.ui.services.MenuConstants.MY_DOCUMENTS;
import static io.college.cms.core.ui.services.MenuConstants.MY_NOTIFICATIONS;
import static io.college.cms.core.ui.services.MenuConstants.MY_PROFILE;
import static io.college.cms.core.ui.services.MenuConstants.NEW_COURSE;
import static io.college.cms.core.ui.services.MenuConstants.OPEN_ADMISSIONS;
import static io.college.cms.core.ui.services.MenuConstants.PUBLISH_ANNOUNCEMENT;
import static io.college.cms.core.ui.services.MenuConstants.PUBLISH_FEES;
import static io.college.cms.core.ui.services.MenuConstants.PUBLISH_JOB;
import static io.college.cms.core.ui.services.MenuConstants.RESULTS;
import static io.college.cms.core.ui.services.MenuConstants.SCHEDULE_EXAM;
import static io.college.cms.core.ui.services.MenuConstants.SCHEDULE_TIME_SUBJECTS;
import static io.college.cms.core.ui.services.MenuConstants.TAG_ATTENDANCE;
import static io.college.cms.core.ui.services.MenuConstants.UPDATE_RESULTS;
import static io.college.cms.core.ui.services.MenuConstants.UPLOAD_DOCUMENTS;
import static io.college.cms.core.ui.services.MenuConstants.USER;
import static io.college.cms.core.ui.services.MenuConstants.USER_GROUP;
import static io.college.cms.core.ui.services.MenuConstants.VIEW_ALL_ANNOUNCEMENT;
import static io.college.cms.core.ui.services.MenuConstants.VIEW_ALL_COURSES;
import static io.college.cms.core.ui.services.MenuConstants.VIEW_ALL_DOCUMENTS;
import static io.college.cms.core.ui.services.MenuConstants.VIEW_ALL_EXAMS;
import static io.college.cms.core.ui.services.MenuConstants.VIEW_ALL_JOBS;
import static io.college.cms.core.ui.services.MenuConstants.VIEW_ALL_USER;
import static io.college.cms.core.ui.services.MenuConstants.VIEW_ATTENDANCE;
import static io.college.cms.core.ui.services.MenuConstants.VIEW_RESULTS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ItemClick;
import com.vaadin.ui.Tree.ItemClickListener;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.ui.model.ViewConstants;
import io.college.cms.core.user.service.SecurityService;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class MenuManagerService {

	private Navigator navigator;
	private SecurityService securityService;

	/**
	 * @param navigator
	 */
	public MenuManagerService() {
		super();
	}

	public void setNavigator(Navigator navigator) {
		this.navigator = navigator;
	}

	@Autowired
	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
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

	public void onlyAdminTree(TreeData<String> treeData) {
		treeData(treeData, ADMISSION, CONFIGURE_FEES);
		treeData(treeData, ADMISSION, PUBLISH_FEES);
		treeData(treeData, ADMISSION, FEES_QUEUE);
		treeData(treeData, ADMISSION, "Open Admissions");
		treeData(treeData, ADMISSION, "Configure Admissions");
		treeData(treeData, ADMISSION, ADMISSION_REQUEST_QUEUE);
		treeData(treeData, null, ATTENDANCE);
		treeData(treeData, ATTENDANCE, TAG_ATTENDANCE);
		treeData(treeData, ATTENDANCE, VIEW_ATTENDANCE);
		treeData(treeData, ANNOUNCEMENT, "Publish Announcement");

		treeData(treeData, null, COURSES);
		treeData(treeData, COURSES, "View all courses");
		treeData(treeData, COURSES, "New course");
		treeData(treeData, EXAMS, "Schedule Exam");
		treeData(treeData, EXAMS, SCHEDULE_TIME_SUBJECTS);
		treeData(treeData, JOB, PUBLISH_JOB);
		treeData(treeData, RESULTS, UPDATE_RESULTS);
		treeData(treeData, USER, VIEW_ALL_USER);
	}

	public Tree<String> buildTreeMenu() {
		Tree<String> tree = new Tree<>();
		tree.addStyleNames(ValoTheme.PANEL_SCROLL_INDICATOR);
		TreeData<String> treeData = new TreeData<>();

		treeData(treeData, null, ADMISSION);
		treeData(treeData, ADMISSION, "Apply Admissions");

		treeData(treeData, null, ANNOUNCEMENT);

		treeData(treeData, ANNOUNCEMENT, "View All Announcements");
		treeData(treeData, null, EXAMS);

		treeData(treeData, EXAMS, "View all exams");

		treeData(treeData, EXAMS, "View exam timetable");
		treeData(treeData, EXAMS, "Download qr for exams");

		treeData(treeData, null, JOB);
		treeData(treeData, JOB, VIEW_ALL_JOBS);
		treeData(treeData, null, RESULTS);
		treeData(treeData, RESULTS, VIEW_RESULTS);

		treeData(treeData, null, USER);
		// treeData(treeData, USER, "Add user");
		treeData(treeData, USER, MY_PROFILE);

		treeData(treeData, null, "Documents");
		treeData(treeData, "Documents", UPLOAD_DOCUMENTS);
		treeData(treeData, "Documents", MY_DOCUMENTS);
		// treeData(treeData, "DOCUMENTS", VIEW_ALL_DOCUMENTS);

		// treeData(treeData, USER, MY_DOCUMENTS);
		//treeData(treeData, USER, MY_NOTIFICATIONS);
		// treeData(treeData, USER, "Configure Notifications");

		treeData(treeData, null, USER_GROUP);
		treeData(treeData, USER_GROUP, ADD_TO_GROUP);
		onlyAdminTree(treeData);
		treeData(treeData, null, "FAQ Bot");

		treeData(treeData, null, "Logout");

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
				} else if (SCHEDULE_TIME_SUBJECTS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.EXAM_SUBJECT_SCHEDULE_TIME_TABLE);
				} else if (VIEW_ALL_EXAMS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.EXAM_VIEW_ALL);
				} else if (VIEW_RESULTS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.SEE_RESULTS);
				} else if (UPDATE_RESULTS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.UPDATE_MARKS_RESULTS);
				} else if (APPLY_ADMISSIONS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.APPLY_ADMISSIONS_BY_COURSE);
				} else if (ADMISSION_REQUEST_QUEUE.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.APPROVE_REJECT_ADMISSION);
				} else if (FAQ_BOT.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.CHAT_FAQ_VIEW);
				} else if (CONFIGURE_NOTIFICATIONS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.CONFIGURE_NOTIFICATION);
				} else if (MY_DOCUMENTS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.MY_DOCUMENTS);
				} else if (UPLOAD_DOCUMENTS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.UPLOAD_DOCUMENTS);
				} else if (VIEW_ALL_DOCUMENTS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.VIEW_ALL_DOCUMENTS);
				} else if (PUBLISH_JOB.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.PUBLISH_JOB);
				} else if (VIEW_ALL_JOBS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.VIEW_ALL_JOB);
				} else if (CONFIGURE_FEES.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.CONFIGURE_FEES);
				} else if (DOWNLOAD_QR_FOR_EXAMS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.EXAM_QR_DOWNLOAD);
				} else if (PUBLISH_FEES.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.PUBLISH_FEES);
				} else if (ADD_TO_GROUP.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.ADD_TO_GROUP);
				} else if (TAG_ATTENDANCE.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.TAG_ATTENDANCE);
				} else if (VIEW_ATTENDANCE.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.VIEW_ATTENDANCE);
				} else if (MY_NOTIFICATIONS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.SEE_NOTIFICATIONS);
				} else if (VIEW_ALL_USER.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.USER_VIEW_ALL);
				} else if (MY_PROFILE.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.USER_PROFILE_VIEW);
				} else if (FEES_QUEUE.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.APPROVE_REJECT_FEES);
				} else if ("LOGOUT".equalsIgnoreCase(value)) {
					SecurityContextHolder.getContext().setAuthentication(null);
					navigator.navigateTo(ViewConstants.LOGIN);
				} else if ("View exam timetable".equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.SUBJECT_VIEW_TIME_TABLE);
				}
			}
		};
	}

}
