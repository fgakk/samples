package com.fga.examples.restful_token_login.repo;

import java.util.List;

import com.fga.examples.restful_token_login.domain.Member;

public interface MemberDao
{
    public Member findById(Long id);

    public Member findByEmail(String email);

    public List<Member> findAllOrderedByName();
    
    public Member findByNameAndPassword(String username, String password);

    public void register(Member member);
}
