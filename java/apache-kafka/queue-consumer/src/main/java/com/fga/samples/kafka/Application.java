package com.fga.samples.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.fga.samples.kafka.consumer.ConsumerManager;

public class Application {

	private final static String CONSUMER_COUNT_KEY = "consumerCount";
	private final static String SERVER_URL_KEY = "serverUrl";
	private final static String GROUPID_KEY = "groupId";
	private final static String TOPIC_KEY = "topic";
	private static int consumerCount;

	private static String serverUrl;

	private static String groupId;

	private static String topic;

	static {
		InputStream is = Application.class.getClassLoader()
				.getResourceAsStream("application.properties");
		Properties properties = new Properties();
		try {
			properties.load(is);
		} catch (IOException e) {
			throw new RuntimeException();
		}
		consumerCount = Integer.parseInt(properties.getProperty(CONSUMER_COUNT_KEY,
				"1"));
		serverUrl = properties.getProperty(SERVER_URL_KEY, "localhost");
		groupId = properties.getProperty(GROUPID_KEY);
		topic = properties.getProperty(TOPIC_KEY);
	}

	public static void main(String[] args) {
		ConsumerManager consumerManager = new ConsumerManager(topic, groupId,
				consumerCount, serverUrl);
		Runtime.getRuntime().addShutdownHook(new CloseHook(consumerManager));
	}

}
