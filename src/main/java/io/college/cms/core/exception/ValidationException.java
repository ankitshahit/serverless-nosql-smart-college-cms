package io.college.cms.core.exception;

public class ValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public ValidationException() {
		super();

	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public ValidationException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);

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
