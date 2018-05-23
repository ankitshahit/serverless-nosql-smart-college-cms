package io.college.cms.core.exception;

public class ResourceExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9026889010693800037L;

	public ResourceExistsException() {
		super();

	}

	public ResourceExistsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

	public ResourceExistsException(String message, Throwable cause) {
		super(message, cause);

	}

	public ResourceExistsException(String message) {
		super(message);

	}

	public ResourceExistsException(Throwable cause) {
		super(cause.getMessage());

	}

}
