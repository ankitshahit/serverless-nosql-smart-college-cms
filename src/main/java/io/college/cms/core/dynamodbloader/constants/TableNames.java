package io.college.cms.core.dynamodbloader.constants;

public class TableNames {
	public static final String COURSES_TABLE = "t_courses";
	/**
	 * Subject properties is no longer has a seperate space to breath. It has
	 * been moved to live under {@link CourseModel}}
	 */
	@Deprecated
	public static final String SUBJECT_TABLE = "t_subjects";
	public static final String PROFILE_TABLE = "t_profiles";
	public static final String EXAMINATION_TABLE = "t_examination";

}
