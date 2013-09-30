package com.fga.examples.restful_token_login.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

/**
 * Container token {@link com.fga.examples.restful_token_login.authentication.RestfulToken} Authentication object 
 * @author gucluakkaya
 *
 */
public class TokenSecurityContext implements SecurityContext {

	
	private Authentication authentication;
	public TokenSecurityContext() {
		
	}
	
	public TokenSecurityContext(Authentication authentication){
		this.authentication = authentication;
	}

	@Override
	public Authentication getAuthentication() {
		return this.authentication;
	}

	@Override
	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;

	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6585550908004422352L;

}
