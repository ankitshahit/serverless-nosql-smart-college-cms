package io.college.cms.core.application;

import org.springframework.http.HttpStatus;

public enum SummaryMessageEnum {
	SUCCESS(HttpStatus.OK), FAILURE(HttpStatus.BAD_REQUEST), ACCESS_DENIED(HttpStatus.FORBIDDEN), UNAUTHORIZED(
			HttpStatus.UNAUTHORIZED), VALIDATION_ERROR(HttpStatus.BAD_REQUEST);
	private HttpStatus code;

	SummaryMessageEnum(HttpStatus status) {
		this.code = status;
	}

	public HttpStatus code() {
		return code;
	}
}
