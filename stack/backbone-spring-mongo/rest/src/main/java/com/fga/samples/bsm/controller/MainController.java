package com.fga.samples.bsm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fga.samples.bsm.model.Content;
import com.fga.samples.bsm.repository.ContentRepository;

@Controller
public class MainController {

	private static final String CONTENT_PATH = "/content";
	@Autowired
	private ContentRepository contentRepository;
	
	@RequestMapping(value=CONTENT_PATH, method=RequestMethod.POST)
	public @ResponseBody Content addContent(@RequestBody Content content){
		return contentRepository.save(content);
	}
	
	@RequestMapping(value=CONTENT_PATH, method=RequestMethod.GET)
	public @ResponseBody List<Content> listContents(){
		return contentRepository.findAll();
	}
}
