package com.fga.sample.bsm.exception;

public class ContentNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5232950603609388533L;
	
	@Override
	public String getMessage() {
		return "Requested content does not exist";
	}

}
