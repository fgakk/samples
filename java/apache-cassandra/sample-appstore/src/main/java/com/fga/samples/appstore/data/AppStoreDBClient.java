package com.fga.samples.appstore.data;

import java.util.UUID;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.fga.samples.appstore.exception.SessionNotInitializedException;

public class AppStoreDBClient {

	private Cluster cluster;
	private Session session;
	
	public AppStoreDBClient() {
		
		connect("127.0.0.1");
		try {
			createSchema();
		} catch (SessionNotInitializedException e) {
			e.printStackTrace();
		}
		close();
	}
	
	public void insert(String packageName, String name, int versionCode, String tags){
		
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO AppStore.Applications (id, packageName, name, versionCode) values")
		.append("(")
		.append(UUID.randomUUID().toString()).append(",")
		.append(packageName).append(",")
		.append(name).append(",")
		.append(versionCode)//.append(",")
//		.append(tags)
		.append(");");
		System.out.println(queryBuilder.toString());
		session.execute(queryBuilder.toString());
	}
	
	public ResultSet list(){
		
		return session.execute("SELECT * FROM AppStore.Applications");
	}
	
	public void createSchema() throws SessionNotInitializedException{
		//Check if schema exists
		if (session == null){
			throw new SessionNotInitializedException();
		}
		//Replication factor is 1 since this sample uses a single node cassandra
		session.execute("CREATE KEYSPACE IF NOT EXISTS AppStore  WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};");
		session.execute("CREATE TABLE IF NOT EXISTS AppStore.Applications (" +
				"id uuid," +
				"packageName text," +
				"name text," +
				"versionCode int," +
				"tags set<text>," +
				"PRIMARY KEY(id, packageName, versionCode)" +
				");");
	}

	public void connect(String node) {
		cluster = Cluster.builder().addContactPoint(node)
		// .withSSL() // Uncomment if using client to node encryption
				.build();
		Metadata metadata = cluster.getMetadata();
		System.out.printf("Connected to cluster: %s\n",
				metadata.getClusterName());
		for (Host host : metadata.getAllHosts()) {
			System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
					host.getDatacenter(), host.getAddress(), host.getRack());
		}
		session = cluster.connect();
	}

	public void close() {
		cluster.shutdown();
	}

}
