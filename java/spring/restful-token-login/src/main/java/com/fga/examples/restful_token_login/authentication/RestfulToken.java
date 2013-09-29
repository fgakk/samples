package com.fga.examples.restful_token_login.authentication;

import java.io.Serializable;
import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import com.fga.examples.restful_token_login.domain.Member;
import com.fga.examples.restful_token_login.domain.TokenGrantedAuthority;


public class RestfulToken extends AbstractAuthenticationToken implements Serializable{

	
	private Object credentials;
	private Object principal;
	
	private String tokenId;
	
	public RestfulToken(List<TokenGrantedAuthority> authorities, final String tokenId, Member member) {
		super(authorities);
		this.tokenId = tokenId;
		//this.principal = member;
		setAuthenticated(true);
	}

	public RestfulToken(String tokenId) {
		super(null);
		this.tokenId = tokenId;
		setAuthenticated(false);
	}

	@Override
	public Object getCredentials() {
		
		return this.credentials;
	}

	@Override
	public Object getPrincipal() {
		
		return this.principal;
	}
	
	
	public String getTokenId() {
		return tokenId;
	}
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7387968508541715234L;

}
