package io.college.cms.core.notification.model;

public enum NotificationType {
	ADMISSION, ANNOUNCEMENT, COURSE_GROUP, EXAMS, MY_DOCUMENTS, JOBS, RESULTS, OTHERS;
	public static enum NotificationMode {
		EMAIL, MY_NOTIFICATIONS;
	}
}
