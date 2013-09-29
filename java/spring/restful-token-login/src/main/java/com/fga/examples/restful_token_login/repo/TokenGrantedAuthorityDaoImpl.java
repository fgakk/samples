package com.fga.examples.restful_token_login.repo;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fga.examples.restful_token_login.domain.TokenGrantedAuthority;

@Repository
@Transactional
public class TokenGrantedAuthorityDaoImpl implements TokenGrantedAuthorityDao {

	@Autowired
	private EntityManager em;
	
	@Override
	public TokenGrantedAuthority getAuthority(String role) {
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<TokenGrantedAuthority> criteria = builder.createQuery(TokenGrantedAuthority.class);
        Root<TokenGrantedAuthority> authority = criteria.from(TokenGrantedAuthority.class);
        criteria.select(authority).where(builder.equal(authority.get("role"), role));
        return em.createQuery(criteria).getSingleResult();
	}

}
