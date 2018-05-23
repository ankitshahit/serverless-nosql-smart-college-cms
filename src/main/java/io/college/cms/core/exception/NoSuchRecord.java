package io.college.cms.core.exception;

public class NoSuchRecord extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -314195164596475207L;

	public NoSuchRecord() {
		super();
	}

	public NoSuchRecord(String msg) {
		super(msg);
	}

	public NoSuchRecord(Exception ex) {
		super(ex.getMessage());
	}

}
