package io.college.cms.core.dynamodb.constants;

public class Table {
	public static final String COURSES_TABLE = "t_courses";
	/**
	 * Subject properties is no longer has a seperate space to breath. It has
	 * been moved to live under {@link CourseModel}}
	 */
	@Deprecated
	public static final String SUBJECT_TABLE = "t_subjects";
	/**
	 * Cognito user pool is being used to manage user
	 */
	@Deprecated
	public static final String PROFILE_TABLE = "t_profiles";
	public static final String EXAMINATION_TABLE = "t_examination";
	public static final String UPLOAD_TABLE = "t_upload";
	public static final String NOTIFICATION_TABLE = "t_notification";
	public static final String APPLY_ADMISSION_TABLE = "t_admission_apply";
	public static final String RESULTS = "t_result";
}
