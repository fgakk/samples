package com.fga.samples.test;

import org.junit.Before;
import org.junit.Test;

import com.fga.samples.appstore.AppStoreManager;

public class AppStoreManagerTest {

	
	private AppStoreManager appStoreManager;
	
	
	@Before
	public void setup(){
		appStoreManager = new AppStoreManager();
	}
	
	@Test
	public void addApplication(){
		
		String tags = "{'games','entertainment'}";
		for (int i = 0 ; i < 3; i++){
			
			appStoreManager.addApplication("'com.fga.test'", "'TestApp'", i, tags);
		}
		
		appStoreManager.getApplications();
	}
}
