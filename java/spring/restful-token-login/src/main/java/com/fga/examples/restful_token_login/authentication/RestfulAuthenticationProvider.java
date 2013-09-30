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

import com.fga.examples.restful_token_login.domain.Member;
import com.fga.examples.restful_token_login.domain.Token;
import com.fga.examples.restful_token_login.domain.TokenGrantedAuthority;
import com.fga.examples.restful_token_login.service.AuthenticationService;

/**
 * Authentication provider for token based request acceptiong usernamepasswordAuthenToken as Authentication
 * and return token on the header in response upon Successful authentication using custom AuthenticationSuccessHanlder
 * {@link com.fga.examples.restful_token_login.authentication.RestAuthenticationSuccessHanlder}
 * @author gucluakkaya
 *
 */
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
		}
		
		return result;
	}
	
	private RestfulToken authenticateForNewToken(final Authentication authentication){
		
		RestfulToken restfulToken = null;
		logger.info("Getting new token");
		Member member = authenticationService.getMember(authentication.getName(), authentication.getCredentials().toString());
		if (member != null){
			
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
