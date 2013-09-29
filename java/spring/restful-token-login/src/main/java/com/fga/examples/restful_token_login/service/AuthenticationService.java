package com.fga.examples.restful_token_login.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.fga.examples.restful_token_login.authentication.RestfulToken;
import com.fga.examples.restful_token_login.domain.Token;
import com.fga.examples.restful_token_login.domain.TokenGrantedAuthority;

public interface AuthenticationService {

	public Token getTokenInfo(String id);
	public Token saveToken(String username, String password, String tokenId, List<TokenGrantedAuthority> authorities);
	public Authentication saveAuthentication(Authentication authentication);
	public GrantedAuthority getAuthority(String role);
	public RestfulToken getAuthentication(String tokenId);

}
