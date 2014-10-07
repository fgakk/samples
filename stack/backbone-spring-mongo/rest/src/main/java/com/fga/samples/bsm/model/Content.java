package com.fga.samples.bsm.model;

import org.springframework.data.annotation.Id;

public class Content {

	@Id
	private String id;
	
	private String title;
	private String content;
	
	public Content() {
		
	}
	
	public Content(String title, String content) {
		super();
		this.title = title;
		this.content = content;
	}
	
	

	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}



	@Override
	public String toString() {
		return "Content [id=" + id + ", title=" + title + ", content="
				+ content + "]";
	}
	
	
}
