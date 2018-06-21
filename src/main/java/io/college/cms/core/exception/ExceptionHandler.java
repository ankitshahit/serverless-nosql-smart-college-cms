package io.college.cms.core.exception;

public class ExceptionHandler {
	public static String beautifyStackTrace(Exception ex) {
		return ex.getLocalizedMessage();
	}

	public static String beautifyStackTrace(Throwable ex) {
		return "";
	}
}
