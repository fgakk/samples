package com.fga.samples.bsm.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fga.samples.bsm.model.Content;

public interface ContentRepository extends MongoRepository<Content, String>{

	public List<Content> findByTitle(String title);
}
