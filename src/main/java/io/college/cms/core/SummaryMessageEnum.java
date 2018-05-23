package io.college.cms.core;

import org.springframework.http.HttpStatus;

public enum SummaryMessageEnum {
	SUCCESS(HttpStatus.ACCEPTED), FAILURE(HttpStatus.BAD_REQUEST), ACCESS_DENIED(
			HttpStatus.FORBIDDEN), UNAUTHORIZED(HttpStatus.UNAUTHORIZED), VALIDATION_ERROR(
			HttpStatus.UNAUTHORIZED);
	private HttpStatus code;

	SummaryMessageEnum(HttpStatus status) {
		this.code = status;
	}

	public HttpStatus code() {
		return code;
	}
}
