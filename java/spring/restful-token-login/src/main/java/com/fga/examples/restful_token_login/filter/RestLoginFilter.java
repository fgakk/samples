package com.fga.examples.restful_token_login.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.fga.examples.restful_token_login.authentication.RestfulToken;

/**
 * Filter for accepting login requests replacing form login filter
 * @author gucluakkaya
 *
 */
public class RestLoginFilter extends AbstractAuthenticationProcessingFilter{

	
	
	public RestLoginFilter() {
		super("/login");
		
	}
	protected RestLoginFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
		
	}

	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {
		logger.info("In the attempt authentication");
		Authentication authRequest = null;
		logger.info("Request for login");
		//Need to save password not in plain text but encrypted.Again for simplicity it is left as plain
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		authRequest = new UsernamePasswordAuthenticationToken(username, password);
		
		return this.getAuthenticationManager().authenticate(authRequest);
	}
	
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		logger.info("In do filter");
		super.doFilter(req, res, chain);
		
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);
		logger.info("Authentication success. Auth result: " + authResult);
		if (logger.isDebugEnabled()) {
            logger.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
        }
		
		SecurityContextHolder.getContext().setAuthentication(authResult);
		

		// Fire event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
        }
        
       getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
        
        
	}
	
	/**
	 * Since service will not have any state every request needs to go 
	 * through authentication
	 */

	
	

}
