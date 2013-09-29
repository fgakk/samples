package com.fga.examples.restful_token_login.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fga.examples.restful_token_login.authentication.RestfulToken;
import com.fga.examples.restful_token_login.domain.Member;
import com.fga.examples.restful_token_login.domain.Token;
import com.fga.examples.restful_token_login.domain.TokenGrantedAuthority;
import com.fga.examples.restful_token_login.repo.MemberDao;
import com.fga.examples.restful_token_login.repo.TokenDao;
import com.fga.examples.restful_token_login.repo.TokenGrantedAuthorityDao;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
	
	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private TokenDao tokenDao;
	
	@Autowired
	private TokenGrantedAuthorityDao tokenGrantedAuthorityDao;
	
	@Override
	@Transactional
	public Token getTokenInfo(String id) {
		
		return tokenDao.getToken(id);
	}

	@Override
	@Transactional
	public Token saveToken(String username, String password, String tokenId, List<TokenGrantedAuthority> authorities) {
	
		logger.info("Saving token");
		Token token = new Token();
		Member member = memberDao.findByNameAndPassword(username, password);
		token.setMember(member);
		token.setAuthorities(authorities);
		token.setToken(tokenId);
		tokenDao.addToken(token);
		return token;

	}

	@Override
	@Transactional
	public Authentication saveAuthentication(Authentication authentication) {
		
		
		String tokenId = ((RestfulToken) authentication).getTokenId();
		logger.info("Token info: " + tokenId);
		Token existing = tokenDao.getToken(tokenId);
		if (existing == null){
			logger.warn("Token expired or does not exists login again");
		}
		if (existing.getMember() == null){
			logger.error("Token does not belong to any user");
		}
		
		return new RestfulToken(existing.getAuthorities(),existing.getToken(),existing.getMember());
	}

	@Override
	public GrantedAuthority getAuthority(String role) {
		
		return tokenGrantedAuthorityDao.getAuthority(role);
	}

	@Override
	public RestfulToken getAuthentication(String tokenId) {
		
		RestfulToken authentication = null;
		Token existing = tokenDao.getToken(tokenId);
		if (existing != null){
			authentication = new RestfulToken(existing.getAuthorities(),existing.getToken(),existing.getMember());
		}
		return authentication;
	}
	
	

}
