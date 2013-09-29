package com.fga.examples.restful_token_login.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.fga.examples.restful_token_login.authentication.TokenSecurityContext;
import com.fga.examples.restful_token_login.service.AuthenticationService;

public class TokenManagementFilter extends GenericFilterBean {

	private static final Logger logger = LoggerFactory.getLogger(TokenManagementFilter.class);
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		
		 HttpServletRequest request = (HttpServletRequest) req;
		 HttpServletResponse response = (HttpServletResponse) res;
		 logger.info("In the tokenManagement filter");
		 SecurityContext contextBeforeChainExecution = loadSecurityContext(request);
		    
		    // Set the Security Context for this thread
		    try {
		      SecurityContextHolder.setContext(contextBeforeChainExecution);
		      chain.doFilter(request, response);
		    }
		    finally {
		      // Free the thread of the context
		      SecurityContextHolder.clearContext();
		    }
	}

	private SecurityContext loadSecurityContext(HttpServletRequest request) {
		final String tokenId = request.getHeader("token");
		logger.info("Token is " + tokenId);
		return tokenId != null ? new TokenSecurityContext(
				authenticationService.getAuthentication(tokenId))
				: SecurityContextHolder.createEmptyContext();
	}

}
