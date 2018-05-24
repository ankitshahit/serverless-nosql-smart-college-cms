package io.college.cms.core.exception;

public class ApplicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3575630720368225202L;

	/**
	 * 
	 */
	public ApplicationException() {
		super();

	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ApplicationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

	/**
	 * @param message
	 * @param cause
	 */
	public ApplicationException(String message, Throwable cause) {
		super(message, cause);

	}

	/**
	 * @param message
	 */
	public ApplicationException(String message) {
		super(message);

	}

	/**
	 * @param cause
	 */
	public ApplicationException(Throwable cause) {
		super(cause);

	}

}
