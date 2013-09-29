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

public class RestLoginFilter extends AbstractAuthenticationProcessingFilter{

	private String loginUrl;
	private boolean loginRequest;
	public RestLoginFilter() {
		super("/login");
		this.loginUrl = "/login";
	}
	protected RestLoginFilter(String defaultFilterProcessesUrl, String loginUrl) {
		super(defaultFilterProcessesUrl);
		this.loginUrl = loginUrl;
	}

	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {
		logger.info("In the attempt authentication");
		Authentication authRequest = null;
		if (isLoginRequest(request, response)){
			logger.info("Request for login");
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			authRequest = new UsernamePasswordAuthenticationToken(username, password);
		}else{
			logger.info("Request with token");
			authRequest = getTokenFromRequest(request, response);
		}
		
		return this.getAuthenticationManager().authenticate(authRequest);
	}
	
	private RestfulToken getTokenFromRequest(HttpServletRequest request,
			HttpServletResponse response) {
		
		String token = request.getHeader("token");
		
		
		return new RestfulToken(token);
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
		//getRememberMeServices().loginSuccess(request, response, authResult);

		// Fire event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
        }
        
        if (!loginRequest){
        	logger.info("Not login request");
        	getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
        }else{
        	logger.info("Login request");
        	RestfulToken tokenInfo = (RestfulToken)authResult;
        	response.setHeader("token", tokenInfo.getTokenId());
        	response.setStatus(HttpServletResponse.SC_OK);
        	
        }
        
	}
	
	/**
	 * Since service will not have any state every request needs to go 
	 * through authentication
	 */
//	@Override
//	protected boolean requiresAuthentication(HttpServletRequest request,
//			HttpServletResponse response) {
//		return true;
//	}
	
	public String getLoginUrl() {
		return loginUrl;
	}
	
	protected boolean isLoginRequest(HttpServletRequest request, HttpServletResponse response){
		
		  String uri = request.getRequestURI();
	        int pathParamIndex = uri.indexOf(';');

	        if (pathParamIndex > 0) {
	            // strip everything after the first semi-colon
	            uri = uri.substring(0, pathParamIndex);
	        }

	        if ("".equals(request.getContextPath())) {
	            return uri.endsWith(loginUrl);
	        }

	        loginRequest = uri.endsWith(request.getContextPath() + loginUrl);
	        return this.loginRequest;
	}

}
