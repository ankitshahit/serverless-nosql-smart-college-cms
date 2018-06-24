package io.college.cms.core.ui.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;

import io.college.cms.core.admission.controller.ApplyAdmissionView;
import io.college.cms.core.admission.controller.ConfigureAdmissionActionView;
import io.college.cms.core.admission.controller.PublishAdmissionView;
import io.college.cms.core.announcement.ui.PublishAnnouncementView;
import io.college.cms.core.announcement.ui.SeeAnnouncementView;
import io.college.cms.core.courses.controller.SeeCoursesView;
import io.college.cms.core.documents.controller.MyDocumentsView;
import io.college.cms.core.examination.controller.PublishExamView;
import io.college.cms.core.examination.controller.SeeExamsView;
import io.college.cms.core.examination.controller.SeeResultsView;
import io.college.cms.core.faq.controller.ChatFaqView;
import io.college.cms.core.notification.controller.ConfigureNotificationView;
import io.college.cms.core.ui.controller.PublishCourseView;
import io.college.cms.core.ui.controller.ViewAllCoursesUI;
import io.college.cms.core.ui.model.ViewConstants;
import io.college.cms.core.user.controller.ConfirmUserView;
import io.college.cms.core.user.controller.LoginUserView;
import io.college.cms.core.user.controller.UserView;

/**
 * @author Ankit
 *
 */
@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ViewManagerService {

	private Navigator navigator;

	@Autowired
	private UserView userView;
	@Deprecated
	@Autowired
	private ViewAllCoursesUI viewCourses;
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
	@Autowired
	private MyDocumentsView myDocuments;
	@Autowired
	private ConfigureNotificationView configureNotification;

	public void setNavigator(Navigator navigator) {
		this.navigator = navigator;
	}

	public void addView(String viewName, View view) {
		this.navigator.addView(viewName, view);
	}

	public void registerViews() {
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
		addView(ViewConstants.MY_DOCUMENTS, myDocuments);
		addView(ViewConstants.CONFIGURE_NOTIFICATION, configureNotification);
	}

}
