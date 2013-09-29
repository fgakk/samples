package com.fga.examples.restful_token_login.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

@Entity
@Table(name="roles")
public class TokenGrantedAuthority implements GrantedAuthority {

	@Id
	private String role;
	
	public TokenGrantedAuthority() {
		
	}
	
	public TokenGrantedAuthority(String role){
		this.role = role;
	}
	
	@ManyToMany(mappedBy="authorities")
	private List<Token> tokens;

	@Override
	public String getAuthority() {
		 Assert.hasText(role, "A granted authority textual representation is required");
		return this.role;
	}
	
	public List<Token> getTokens() {
		return tokens;
	}
	
	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7749143463653533984L;

	

}
