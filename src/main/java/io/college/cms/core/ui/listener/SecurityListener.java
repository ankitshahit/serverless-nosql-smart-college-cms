package io.college.cms.core.ui.listener;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.navigator.ViewChangeListener;

import io.college.cms.core.admission.controller.ApproveRejectAdmissionView;
import io.college.cms.core.admission.controller.ApproveRejectFeesView;
import io.college.cms.core.admission.controller.ConfigureAdmissionView;
import io.college.cms.core.admission.controller.ConfigureFeesView;
import io.college.cms.core.announcement.ui.PublishAnnouncementView;
import io.college.cms.core.attendance.controller.SeeAttendanceView;
import io.college.cms.core.attendance.controller.TagAttendanceView;
import io.college.cms.core.courses.controller.SeeCoursesView;
import io.college.cms.core.examination.controller.PublishExamView;
import io.college.cms.core.examination.controller.ScheduleExamSubjectView;
import io.college.cms.core.job.controller.PublishJobView;
import io.college.cms.core.ui.controller.PublishCourseView;
import io.college.cms.core.ui.controller.ViewAllCoursesUI;
import io.college.cms.core.ui.model.ViewConstants;
import io.college.cms.core.user.constants.UserGroups;
import io.college.cms.core.user.controller.AddToGroupsView;
import io.college.cms.core.user.controller.ListUsersView;
import io.college.cms.core.user.controller.MyProfileView;
import io.college.cms.core.user.service.SecurityService;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SecurityListener implements ViewChangeListener {
	public static final UserGroups[] ALL_ACCESS = new UserGroups[] { UserGroups.STUDENT, UserGroups.STAFF,
			UserGroups.ADMIN };
	public static final UserGroups[] STAFF_ACCESS = new UserGroups[] { UserGroups.STAFF, UserGroups.ADMIN };
	private static final long serialVersionUID = 1L;
	private String errorMsg;
	private SecurityService securityUtils;
	private Map<String, String> params;

	@Autowired
	public void setSecurityUtils(SecurityService securityUtils) {
		this.securityUtils = securityUtils;
	}

	@Override
	public boolean beforeViewChange(ViewChangeListener.ViewChangeEvent event) {

		return true;
	}

	@Override
	public void afterViewChange(ViewChangeEvent event) {
		UserGroups[] roles = null;
		if (event.getNewView() instanceof PublishJobView || event.getNewView() instanceof PublishAnnouncementView
				|| event.getNewView() instanceof ListUsersView || event.getNewView() instanceof TagAttendanceView
				|| event.getNewView() instanceof SeeAttendanceView
				|| event.getNewView() instanceof ApproveRejectAdmissionView
				|| event.getNewView() instanceof ScheduleExamSubjectView
				|| event.getNewView() instanceof AddToGroupsView || event.getNewView() instanceof ConfigureFeesView
				|| event.getNewView() instanceof ConfigureAdmissionView || event.getNewView() instanceof PublishExamView
				|| event.getNewView() instanceof SeeCoursesView || event.getNewView() instanceof PublishCourseView
				|| event.getNewView() instanceof ViewAllCoursesUI
				|| event.getNewView() instanceof ApproveRejectAdmissionView
				|| event.getNewView() instanceof ApproveRejectFeesView) {
			roles = STAFF_ACCESS;
		} else if (event.getNewView() instanceof MyProfileView) {
			roles = new UserGroups[] {};
		} else {
			roles = ALL_ACCESS;
		}
		// null meaning, we don't have an access level defined yet. Default
		// access is to accept all requests.
		if (roles == null) {
			return;
		}
		// in-case we want this to be accessible even for anonymous user set the
		// array to empty
		if (roles.length == 0) {
			return;
		}
		boolean result = authorizeRequest(event, roles);
		if (!result) {
			// event.getNavigator().navigateTo(ViewConstants.LOGIN);
		}
		return;
	}

	private boolean authorizeRequest(ViewChangeEvent event, UserGroups[] roles) {
		try {
			// we need to make myprofile free from authentication, as it will be
			// used by user to validate and create their account.
			if (event.getNewView() instanceof MyProfileView
					&& SecurityService.ANONYMOUS_USER.equalsIgnoreCase(securityUtils.getPrincipal())) {
				return true;
			}
			securityUtils.authorize(roles);
			event.getNewView().enter(event);
		} catch (Exception ex) {
			System.out.println(ex);
			// Notification notifi = Notification.show(ex.getMessage(),
			// Notification.Type.ERROR_MESSAGE);
			// notifi.addCloseListener(close ->
			// event.getNavigator().navigateTo(ViewConstants.LOGIN));
			event.getNavigator().navigateTo(ViewConstants.LOGIN);
			return false;
		}
		return true;
	}
}
