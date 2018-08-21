/**
 * 
 */
package com.actian.nevernote.modal;

/**
 * @author sugan
 * This class is used to add custom Messages to the Client
 */
public class MessageDetails {

	private String message;
	private String errorCode;
	
	/**
	 * Parameterized Constructor
	 * @param message
	 */
	public MessageDetails(String message) {
		
		super();
		this.errorCode = message.split(":")[0];
		this.message = message;
	}

	/**
	 * Method to Get The Message
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Setter Method
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
