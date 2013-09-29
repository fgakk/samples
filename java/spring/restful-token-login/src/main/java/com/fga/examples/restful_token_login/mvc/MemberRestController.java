package com.fga.examples.restful_token_login.mvc;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fga.examples.restful_token_login.domain.Member;
import com.fga.examples.restful_token_login.repo.MemberDao;

@Controller
@RequestMapping("/rest/members")
public class MemberRestController
{
	private Logger logger = LoggerFactory.getLogger(MemberRestController.class);
    @Autowired
    private MemberDao memberDao;

    @RequestMapping(method=RequestMethod.GET, produces="application/json")
    public @ResponseBody List<Member> listAllMembers()
    {
    	logger.info("Retrieving members");
        return memberDao.findAllOrderedByName();
    }

    @RequestMapping(value="/{id}", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody Member lookupMemberById(@PathVariable("id") Long id)
    {
        return memberDao.findById(id);
    }
    
   
}
