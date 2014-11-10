package com.fga.samples.kafka.producer;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

public class Application {

	private static final String SERVER_URL_KEY = "serverUrl";
	private static final String TOPIC_KEY = "topic";
	private static final Logger logger = Logger.getLogger(Application.class);
	
	private static String serverUrl;

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
		
		serverUrl = properties.getProperty(SERVER_URL_KEY, "localhost:9092");
		topic = properties.getProperty(TOPIC_KEY);
	}
	
	
	public static void main(String[] args) throws Exception {
		
		KafkaProducer kafkaProducer = new KafkaProducer(createConfig());
		Runtime.getRuntime().addShutdownHook(new CloseHook(kafkaProducer));
		
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy:hh-mm-ss.SSS");
		StringBuilder sb = new StringBuilder();
		while(true){
			sb.append("sample message ").append("time:").append(df.format(new Date(System.currentTimeMillis())));
			logger.info("Publishing message: "  + sb.toString());
			ProducerRecord record = new ProducerRecord(topic, sb.toString().getBytes());
			kafkaProducer.send(record);
		}
	}
	
	private static Map<String, Object> createConfig() {
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverUrl);
		return props;
	}

	
}
