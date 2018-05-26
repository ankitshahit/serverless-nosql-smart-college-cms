package io.college.cms.core.courses.controller.constants;

import org.apache.commons.lang3.StringUtils;

import io.college.cms.core.exception.ExceptionType;
import io.college.cms.core.exception.ValidationException;
import io.college.cms.core.exception.ValidationHandler;

public enum SubjectType {
	THEORY, PRACTICAL, INTERNAL, OTHER;

	public static SubjectType findByType(String val) throws ValidationException {
		SubjectType subjectType = null;
		for (SubjectType type : values()) {
			if (!type.toString().equalsIgnoreCase(StringUtils.trimToEmpty(val))) {
				continue;
			}
			subjectType = type;
			break;
		}
		ValidationHandler.throwExceptionIfNull(subjectType, "", ExceptionType.NO_RECORD_AVAILABLE);
		return subjectType;
	}
}
