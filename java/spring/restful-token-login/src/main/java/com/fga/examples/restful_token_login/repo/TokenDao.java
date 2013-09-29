package com.fga.examples.restful_token_login.repo;

import com.fga.examples.restful_token_login.domain.Token;

public interface TokenDao {

	public void addToken(Token token);
	
	public Token getToken(String id);
}
