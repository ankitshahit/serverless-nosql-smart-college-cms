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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
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

	public Tree<String> buildTreeMenu() {
		Tree<String> tree = new Tree<>();
		tree.addStyleNames(ValoTheme.PANEL_SCROLL_INDICATOR);
		TreeData<String> treeData = new TreeData<>();
		treeData(treeData, null, ADMISSION);
		treeData(treeData, ADMISSION, CONFIGURE_FEES);
		treeData(treeData, ADMISSION, PUBLISH_FEES);
		treeData(treeData, ADMISSION, "Open Admissions");
		treeData(treeData, ADMISSION, "Apply Admissions");
		treeData(treeData, ADMISSION, "Configure Admissions");
		treeData(treeData, ADMISSION, ADMISSION_REQUEST_QUEUE);

		treeData(treeData, null, ATTENDANCE);
		treeData(treeData, ATTENDANCE, TAG_ATTENDANCE);
		treeData(treeData, ATTENDANCE, VIEW_ATTENDANCE);

		treeData(treeData, null, ANNOUNCEMENT);
		treeData(treeData, ANNOUNCEMENT, "Publish Announcement");
		treeData(treeData, ANNOUNCEMENT, "View All Announcements");
		treeData(treeData, null, EXAMS);

		treeData(treeData, null, COURSES);
		treeData(treeData, COURSES, "View all courses");
		treeData(treeData, COURSES, "New course");

		treeData(treeData, EXAMS, "View all exams");
		treeData(treeData, EXAMS, "Schedule Exam");
		treeData(treeData, EXAMS, SCHEDULE_TIME_SUBJECTS);
		treeData(treeData, EXAMS, "View exam timetable");
		treeData(treeData, EXAMS, "Download qr for exams");

		treeData(treeData, null, JOB);
		treeData(treeData, JOB, PUBLISH_JOB);
		treeData(treeData, JOB, VIEW_ALL_JOBS);
		treeData(treeData, null, RESULTS);
		treeData(treeData, RESULTS, VIEW_RESULTS);

		treeData(treeData, null, USER);
		treeData(treeData, USER, "View all user");
		treeData(treeData, USER, "Add user");
		treeData(treeData, USER, "My profile");
		treeData(treeData, USER, "My documents");
		treeData(treeData, USER, MY_NOTIFICATIONS);
		treeData(treeData, USER, "Configure Notifications");

		treeData(treeData, null, USER_GROUP);
		treeData(treeData, USER_GROUP, ADD_TO_GROUP);

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
				} else if (SCHEDULE_TIME_SUBJECTS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.EXAM_SUBJECT_SCHEDULE_TIME_TABLE);
				} else if (VIEW_ALL_EXAMS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.EXAM_VIEW_ALL);
				} else if (VIEW_RESULTS.equalsIgnoreCase(value)) {
					navigator.navigateTo(ViewConstants.SEE_RESULTS);
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
				}
			}
		};
	}

}
