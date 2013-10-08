package com.fga.samples.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fga.samples.appstore.data.AppStoreDBClient;
import com.fga.samples.appstore.exception.SessionNotInitializedException;

public class AppStoreDBClientTest {

	private AppStoreDBClient dbClient;
	@Before
	public void setup(){
		dbClient = new AppStoreDBClient();
		dbClient.connect("127.0.0.1");
	}
	
	@Test
	public void createSchema() throws SessionNotInitializedException{
		
		dbClient.createSchema();
		dbClient.createSchema();
	}
	
	@After
	public void tearDown(){
		dbClient.close();
	}
}
