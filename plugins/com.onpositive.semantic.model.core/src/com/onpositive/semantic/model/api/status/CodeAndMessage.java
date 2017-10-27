package com.onpositive.semantic.model.api.status;

import java.io.Serializable;
import java.text.MessageFormat;



public class CodeAndMessage implements Comparable<CodeAndMessage>,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int IN_PROGRESS_SOME_DATA = -2;
	public static final int IN_PROGRESS_NO_DATA = -1;
	/** Status severity constant (value 0) indicating this status represents the nominal case.
	 * This constant is also used as the status code representing the nominal case.
	 * @see #getSeverity()
	 * @see #isOK()
	 */
	public static final int OK = 0;

	/** Status type severity (bit mask, value 1) indicating this status is informational only.
	 * @see #getSeverity()
	 * @see #matches(int)
	 */
	public static final int INFO = 0x01;

	/** Status type severity (bit mask, value 2) indicating this status represents a warning.
	 * @see #getSeverity()
	 * @see #matches(int)
	 */
	public static final int WARNING = 0x02;

	/** Status type severity (bit mask, value 4) indicating this status represents an error.
	 * @see #getSeverity()
	 * @see #matches(int)
	 */
	public static final int ERROR = 0x04;

	public static final CodeAndMessage IN_PROGRESS_MESSAGE = new CodeAndMessage(IN_PROGRESS_NO_DATA, "");

	/** Status type severity (bit mask, value 8) indicating this status represents a
	 * cancelation
	 * @see #getSeverity()
	 * @see #matches(int)
	 * @since 3.0
	 */
	
	private final int code;
	private final String message;
	private Throwable exception;

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

	public static CodeAndMessage OK_MESSAGE = new CodeAndMessage(OK,
			null);
	public static CodeAndMessage MORE_WORK_NEEDED = new CodeAndMessage(IN_PROGRESS_SOME_DATA,
			null);
	
	public final boolean isError(){
		return code>=ERROR;
	}

	public CodeAndMessage(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return this.code;
	}

	public String getMessage() {
		return this.message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.code;
		result = prime * result + ((this.message == null) ? 0 : this.message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final CodeAndMessage other = (CodeAndMessage) obj;
		if (this.code != other.code) {
			return false;
		}
		if (this.message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!this.message.equals(other.message)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(CodeAndMessage o) {
		return o.code - this.code;
	}

	public static CodeAndMessage errorMessage(String string) {
		return new CodeAndMessage(ERROR, string);
	}
	public static CodeAndMessage errorMessage(String string,Object...objects ) {
		return new CodeAndMessage(ERROR, MessageFormat.format(string, objects));
	}
	
	public static CodeAndMessage warningMessage(String string) {
		return new CodeAndMessage(WARNING, string);
	}

	public CodeAndMessage max(CodeAndMessage validate) {
		if (validate.code>this.code){
			return validate;
		}
		return this;
	}

	public boolean isInInitialProgress() {
		return code==IN_PROGRESS_NO_DATA;
	}
	public boolean isInProgress() {
		return code==IN_PROGRESS_SOME_DATA;
	}

	public static CodeAndMessage errorMessage(Exception e) {
		CodeAndMessage errorMessage = CodeAndMessage.errorMessage(e.getMessage());
		errorMessage.setException(e);
		return errorMessage;
	}
}
