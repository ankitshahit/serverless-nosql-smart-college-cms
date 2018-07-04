package io.college.cms.core.ui.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;

import io.college.cms.core.admission.controller.ApplyAdmissionView;
import io.college.cms.core.admission.controller.ApproveRejectAdmissionView;
import io.college.cms.core.admission.controller.ApproveRejectFeesView;
import io.college.cms.core.admission.controller.ConfigureAdmissionView;
import io.college.cms.core.admission.controller.ConfigureFeesView;
import io.college.cms.core.admission.controller.PublishAdmissionView;
import io.college.cms.core.announcement.ui.PublishAnnouncementView;
import io.college.cms.core.announcement.ui.SeeAnnouncementView;
import io.college.cms.core.attendance.controller.SeeAttendanceView;
import io.college.cms.core.attendance.controller.TagAttendanceView;
import io.college.cms.core.courses.controller.SeeCoursesView;
import io.college.cms.core.documents.controller.UploadDocumentsView;
import io.college.cms.core.examination.controller.DownloadQrExamView;
import io.college.cms.core.examination.controller.PublishExamView;
import io.college.cms.core.examination.controller.PublishFeesView;
import io.college.cms.core.examination.controller.ScheduleExamSubjectView;
import io.college.cms.core.examination.controller.SeeExamsView;
import io.college.cms.core.examination.controller.SeeResultsView;
import io.college.cms.core.examination.controller.SeeSubjectScheduleView;
import io.college.cms.core.examination.controller.UpdateResultsView;
import io.college.cms.core.faq.controller.ChatFaqView;
import io.college.cms.core.job.controller.PublishJobView;
import io.college.cms.core.job.controller.SeeJobsView;
import io.college.cms.core.notification.controller.ConfigureNotificationView;
import io.college.cms.core.notification.controller.SeeNotificationView;
import io.college.cms.core.ui.controller.PublishCourseView;
import io.college.cms.core.ui.controller.ViewAllCoursesUI;
import io.college.cms.core.ui.model.ViewConstants;
import io.college.cms.core.user.controller.AddToGroupsView;
import io.college.cms.core.user.controller.ConfirmPasswordForgetView;
import io.college.cms.core.user.controller.ConfirmUserView;
import io.college.cms.core.user.controller.ForgotPasswordView;
import io.college.cms.core.user.controller.ListUsersView;
import io.college.cms.core.user.controller.LoginUserView;
import io.college.cms.core.user.controller.MyDocumentsView;
import io.college.cms.core.user.controller.MyProfileView;
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
	private ConfigureAdmissionView configureAdmission;
	@Autowired
	private LoginUserView loginView;
	@Autowired
	private ApplyAdmissionView applyAdmission;
	@Autowired
	private UploadDocumentsView uploadDocuments;
	@Autowired
	private ConfigureNotificationView configureNotification;
	@Autowired
	private PublishJobView publishJob;
	@Autowired
	private SeeJobsView viewJobs;
	@Autowired
	private ConfigureFeesView configureFeesView;
	@Autowired
	private DownloadQrExamView downloadQr;
	@Autowired
	private PublishFeesView publishFees;
	@Autowired
	private AddToGroupsView addToGroup;
	@Autowired
	private ScheduleExamSubjectView scheduleExamSubject;
	@Autowired
	private TagAttendanceView tagAttendance;
	@Autowired
	private SeeAttendanceView viewAttendance;
	@Autowired
	private ApproveRejectAdmissionView approveRejectAdmission;
	@Autowired
	private SeeNotificationView seeNotifications;
	@Autowired
	private ListUsersView listUsers;
	@Autowired
	private MyProfileView myProfile;
	@Autowired
	private MyDocumentsView myDocuments;
	@Autowired
	private ForgotPasswordView forgotPassword;
	@Autowired
	private ConfirmPasswordForgetView confirmForgotPassword;
	@Autowired
	private ApproveRejectFeesView approveRejectApproveFees;
	@Autowired
	private UpdateResultsView updateResults;
	@Autowired
	private SeeSubjectScheduleView seeSubject;
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
		addView(ViewConstants.SUBJECT_VIEW_TIME_TABLE, seeSubject);
		addView(ViewConstants.EXAM_QR_DOWNLOAD, downloadQr);
		addView(ViewConstants.EXAM_SUBJECT_SCHEDULE_TIME_TABLE, scheduleExamSubject);
		addView(ViewConstants.PUBLISH_FEES, publishFees);
		addView(ViewConstants.CONFIGURE_FEES, configureFeesView);
		addView(ViewConstants.APPROVE_REJECT_FEES, approveRejectApproveFees);
		addView(ViewConstants.SEE_RESULTS, seeResults);
		addView(ViewConstants.UPDATE_MARKS_RESULTS, updateResults);
		addView(ViewConstants.EXAM_CREATE, publishExam);
		addView(ViewConstants.EXAM_VIEW_ALL, seeExam);
		addView(ViewConstants.CHAT_FAQ_VIEW, chatFaq);
		addView(ViewConstants.DEAL_WITH_USER, userView);
		addView(ViewConstants.USER_VIEW_ALL, listUsers);
		addView(ViewConstants.CONFIRM_USER_VIEW, confirmUser);
		addView(ViewConstants.USER_PROFILE_VIEW, myProfile);
		addView(ViewConstants.OPEN_ADMISSIONS_BY_COURSE, publishAdmission);
		addView(ViewConstants.CONFIGURE_ADMISSION_PROPERTIES, configureAdmission);
		addView(ViewConstants.APPLY_ADMISSIONS_BY_COURSE, applyAdmission);
		addView(ViewConstants.LOGIN, loginView);
		addView(ViewConstants.UPLOAD_DOCUMENTS, uploadDocuments);
		addView(ViewConstants.MY_DOCUMENTS, myDocuments); // TODO: VIEW my
															// documents
		// addView(ViewConstants.VIEW_ALL_DOCUMENTS, null); // TODO: VIEW ALL
		// DOCUMENTS
		addView(ViewConstants.CONFIGURE_NOTIFICATION, configureNotification);
		addView(ViewConstants.PUBLISH_JOB, publishJob);
		addView(ViewConstants.VIEW_ALL_JOB, viewJobs);
		addView(ViewConstants.ADD_TO_GROUP, addToGroup);
		addView(ViewConstants.TAG_ATTENDANCE, tagAttendance);
		addView(ViewConstants.VIEW_ATTENDANCE, viewAttendance);
		addView(ViewConstants.APPROVE_REJECT_ADMISSION, approveRejectAdmission);
		addView(ViewConstants.SEE_NOTIFICATIONS, seeNotifications);
		addView(ViewConstants.FORGOT_PASSWORD, forgotPassword);
		addView(ViewConstants.CONFIRM_FORGOT_PASSWORD, confirmForgotPassword);
	}

}
