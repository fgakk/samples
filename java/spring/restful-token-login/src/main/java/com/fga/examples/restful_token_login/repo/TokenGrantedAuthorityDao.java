package com.fga.examples.restful_token_login.repo;

import com.fga.examples.restful_token_login.domain.TokenGrantedAuthority;

public interface TokenGrantedAuthorityDao {

	public TokenGrantedAuthority getAuthority(String role);
}
