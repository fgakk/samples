package com.fga.examples.restful_token_login.authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fga.examples.restful_token_login.domain.Token;
import com.fga.examples.restful_token_login.domain.TokenGrantedAuthority;
import com.fga.examples.restful_token_login.service.AuthenticationService;


public class RestfulAuthenticationProvider implements AuthenticationProvider{

	private static final Logger logger = LoggerFactory.getLogger(RestfulAuthenticationProvider.class);
	
	@Autowired
	public AuthenticationService authenticationService;
	
	
	
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		Authentication result = null;
		logger.info("In the authenticate method");
		if (!supports(authentication.getClass())){
			return result;
		}
		
		if (authentication instanceof UsernamePasswordAuthenticationToken){
			result = this.authenticateForNewToken(authentication);
		}else if (authentication instanceof RestfulToken){
			logger.info("Token already exists");
			result = authenticationService.saveAuthentication(authentication);
		}
		
		return result;
	}
	
	private RestfulToken authenticateForNewToken(final Authentication authentication){
		
		RestfulToken restfulToken = null;
		logger.info("Getting new token");
		if (authentication.getName().equals("testuser") && authentication.getCredentials().equals("a12345")){
			
			String tokenId = UUID.randomUUID().toString();
			List<TokenGrantedAuthority> authorities = new ArrayList<TokenGrantedAuthority>();
			authorities.add((TokenGrantedAuthority) authenticationService.getAuthority("ROLE_USER"));
			Token token = authenticationService.saveToken(authentication.getName(), authentication.getCredentials().toString(), tokenId, authorities);
			restfulToken = new RestfulToken(authorities,tokenId, token.getMember());
			logger.info("Authentication granted");
		}
		return restfulToken;
	}
	

	@Override
	public boolean supports(final Class<? extends Object> authentication) {
		return RestfulToken.class.isAssignableFrom(authentication) || UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
