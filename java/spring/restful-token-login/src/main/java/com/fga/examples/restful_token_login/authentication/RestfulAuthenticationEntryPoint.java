package com.fga.examples.restful_token_login.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Custom entry point return 401 Unauthorized for any request without token
 * @author gucluakkaya
 *
 */
public class RestfulAuthenticationEntryPoint implements
		AuthenticationEntryPoint {

	private static final Logger logger = LoggerFactory.getLogger(RestfulAuthenticationEntryPoint.class);
	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		logger.info("In the entry point");
		response.sendError( HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized" );
		
	}

}
