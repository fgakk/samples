package com.fga.examples.restful_token_login.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table
public class Token implements Serializable{

	

	@Id
	private String token;
	
	@OneToOne(targetEntity=Member.class)
	@JoinColumn(name="member_id")
	private Member member;
	
	
	@ManyToMany(targetEntity=TokenGrantedAuthority.class)
	@JoinColumn(name="role_id")
	private List<TokenGrantedAuthority> authorities;
	
	public Token() {
		
	}
	
	public Token(String token){
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public Member getMember() {
		return member;
	}
	
	public void setMember(Member member) {
		this.member = member;
	}
	
	public void setAuthorities(List<TokenGrantedAuthority> authorities) {
		this.authorities = authorities;
	}
	
	public List<TokenGrantedAuthority> getAuthorities() {
		return authorities;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -5254049518157312910L;
}
