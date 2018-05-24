package io.college.cms.core.exception;

import org.apache.commons.lang3.StringUtils;

public class ValidationHandler {
	public static final String EMPTY_STRING = "";

	public static void throwExceptionIfNull(Object val, String msg, ExceptionType type)
			throws NoSuchRecordException, ValidationException {
		if (val != null || type == null || ExceptionType.SILENT == type) {
			return;
		}
		if (ExceptionType.NO_RECORD_AVAILABLE == type) {
			throw new NoSuchRecordException(sourceIfNotNull(msg, "No such record available."));
		} else if (ExceptionType.VALIDATION_EXCEPTION == type) {
			throw new ValidationException(sourceIfNotNull(msg, "Resource not found, validation error."));
		}
	}

	public static void throwExceptionIfTrue(boolean val, String msg, ExceptionType type)
			throws NoSuchRecordException, ValidationException {

		if (!val) {
			return;
		}
		if (StringUtils.isEmpty(msg)) {
			msg = "An error was thrown.";
		}
		if (ExceptionType.NO_RECORD_AVAILABLE == type) {
			throw new NoSuchRecordException(sourceIfNotNull(msg, "No such record available."));
		} else if (ExceptionType.VALIDATION_EXCEPTION == type) {
			throw new ValidationException(sourceIfNotNull(msg, "Resource not found, validation error."));
		}
	}

	public static String sourceIfNotNull(String source, String defaultMsg) {
		return source == null || source.isEmpty() ? defaultMsg : source;
	}
}
