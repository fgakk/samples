package com.fga.examples.restful_token_login.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

/**
 * Class extending SimpleUrlAuthenticationSuccessHandler working the same
 * way as its parent with the difference of returing return value of 200 with
 * token information instead send direct for restful requests
 * @author gucluakkaya
 *
 */
public class RestAuthenticationSuccessHanlder extends
		SimpleUrlAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		
        String targetUrlParam = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl() || 
          (targetUrlParam != null && 
          StringUtils.hasText(request.getParameter(targetUrlParam)))) {
          clearAuthenticationAttributes(request);
          return;
        }
        RestfulToken tokenInfo = (RestfulToken)authentication;
    	response.setHeader("token", tokenInfo.getTokenId());
    	response.setStatus(HttpServletResponse.SC_OK);
        clearAuthenticationAttributes(request);
	}
}
