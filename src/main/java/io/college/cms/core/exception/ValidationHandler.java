package io.college.cms.core.exception;

public class ValidationHandler {
	public static final String EMPTY_STRING = "";

	public static void throwExceptionIfNull(Object val, String msg,
			ExceptionType type) throws NoSuchRecord, ValidationException {
		if (val != null || type == null || ExceptionType.SILENT == type) {
			return;
		}
		if (ExceptionType.NO_RECORD_AVAILABLE == type) {
			throw new NoSuchRecord(sourceIfNotNull(msg,
					"No such record available."));
		} else if (ExceptionType.VALIDATION_EXCEPTION == type) {
			throw new ValidationException(sourceIfNotNull(msg,
					"Resource not found, validation error."));
		}
	}

	public static String sourceIfNotNull(String source, String defaultMsg) {
		return source == null || source.isEmpty() ? defaultMsg : source;
	}
}
