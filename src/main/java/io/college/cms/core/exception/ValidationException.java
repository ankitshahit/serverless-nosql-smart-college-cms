package io.college.cms.core.exception;

public class ValidationException extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidationException() {
		super();

	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ValidationException(String arg0, Throwable arg1) {
		super(arg0, arg1);

	}

	/**
	 * @param arg0
	 */
	public ValidationException(String arg0) {
		super(arg0);

	}

	/**
	 * @param arg0
	 */
	public ValidationException(Throwable arg0) {
		super(arg0.getMessage());
	}

}
