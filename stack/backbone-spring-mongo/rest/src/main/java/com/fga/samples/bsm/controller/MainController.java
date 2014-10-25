package com.fga.samples.bsm.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fga.sample.bsm.exception.ContentNotFoundException;
import com.fga.samples.bsm.model.Content;
import com.fga.samples.bsm.repository.ContentRepository;

@Controller
public class MainController {

	private static final String CONTENT_PATH = "/content";
	private static final String ID = "id";
	private static final String SINGLE_CONTENT_PATH = CONTENT_PATH + "/{id}"; 
	private static final String CONTENT_NOT_FOUND_ERROR = "Requested content does not exist";
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
	
	@RequestMapping(value=SINGLE_CONTENT_PATH, method=RequestMethod.PUT)
	public @ResponseBody Content updateContent(@PathVariable(value=ID) String id, @RequestBody Content content, HttpServletResponse response) throws IOException{
		
		Content result = null;
		try{
			Content existing = getContent(id);
			existing.setContent(content.getContent());
			existing.setTitle(content.getTitle());
			result = contentRepository.save(existing);
		}catch(ContentNotFoundException e){
			response.sendError(HttpStatus.NOT_FOUND.value(), CONTENT_NOT_FOUND_ERROR);
		}
		return result;
		
		
	}
	
	@RequestMapping(value=SINGLE_CONTENT_PATH, method=RequestMethod.GET)
	public @ResponseBody Content getContent(@PathVariable(value=ID) String id, HttpServletResponse response) throws IOException{
		
		Content existing = null;
		try {
			existing = getContent(id);
		} catch (ContentNotFoundException e) {
			response.sendError(HttpStatus.NOT_FOUND.value(), CONTENT_NOT_FOUND_ERROR);
		}
		return existing;
		
	}
	
	@RequestMapping(value=SINGLE_CONTENT_PATH, method=RequestMethod.DELETE)
	public @ResponseBody boolean deleteContent(@PathVariable(value=ID) String id, HttpServletResponse response) throws IOException{
		Content existing = null;
		boolean result = false;
		try {
			existing = getContent(id);
			contentRepository.delete(existing);
			result = true;
		} catch (ContentNotFoundException e) {
			response.sendError(HttpStatus.NOT_FOUND.value(), CONTENT_NOT_FOUND_ERROR);
		}
		return result;
	}
	
	
	private Content getContent(String id) throws ContentNotFoundException{
		Content existing = contentRepository.findOne(id);
		if (null == existing){
			throw new ContentNotFoundException();
		}
		return existing;
	}
	
}
