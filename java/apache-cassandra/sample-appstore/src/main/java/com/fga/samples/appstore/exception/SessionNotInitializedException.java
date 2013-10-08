package com.fga.samples.appstore.exception;

public class SessionNotInitializedException extends Exception {

	public SessionNotInitializedException() {
		super("Session not initialized.First create a connection to DB");
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3376700007148880706L;

}
