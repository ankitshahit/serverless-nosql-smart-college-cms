package io.college.cms.core.exception;

public class NoSuchRecordException extends ValidationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -314195164596475207L;

	public NoSuchRecordException() {
		super();
	}

	public NoSuchRecordException(String msg) {
		super(msg);
	}

	public NoSuchRecordException(Exception ex) {
		super(ex.getMessage());
	}

}
