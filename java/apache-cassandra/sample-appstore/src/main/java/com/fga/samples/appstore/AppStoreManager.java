package com.fga.samples.appstore;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.fga.samples.appstore.data.AppStoreDBClient;

public class AppStoreManager {

	
	private AppStoreDBClient client = new AppStoreDBClient();
	
	
	public void addApplication(String packageName, String name, int versionCode, String tags){
		
		client.connect("127.0.0.1");
		client.insert(packageName, name, versionCode, tags);
		client.close();
	}
	
	public void getApplications(){
		
		client.connect("127.0.0.1");
		ResultSet results = client.list();
		
		for (Row row: results){
			System.out.println(row.getString("packageName") + " " +  row.getInt("versionCode"));
		}
		client.close();
	}
}
