package com.fga.examples.restful_token_login.repo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fga.examples.restful_token_login.domain.Token;


@Repository
@Transactional
public class TokenDaoImpl implements TokenDao {

	@Autowired
    private EntityManager em;

	
	@Override
	public void addToken(Token token) {
		em.persist(token);
	}

	@Override
	public Token getToken(String id) {
		
		Token token = null;
		CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Token> criteria = builder.createQuery(Token.class);
        Root<Token> qToken = criteria.from(Token.class);
        criteria.select(qToken).where(builder.equal(qToken.get("token"), id));
        List<Token> resultList =  em.createQuery(criteria).getResultList();
        if (resultList.size() > 0){
        	 token = resultList.get(0);
        }
        return token;
	}

}
