package com.fga.samples.kafka.consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.javaapi.consumer.ConsumerConnector;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;

import org.apache.log4j.Logger;

public class ConsumerManager {

	private static final Logger logger = Logger
			.getLogger(ConsumerManager.class);
	private final ConsumerConnector consumerConnector;
	private ExecutorService executorService;

	Map<String, Integer> topicCountMap = new HashMap<String, Integer>();

	/**
	 * 
	 * @param topic
	 * @param groupId
	 * @param consumerCount
	 * @param serverUrl
	 */
	public ConsumerManager(String topic, String groupId, int consumerCount,
			String serverUrl) {
		logger.info("ConsumerManager is initialized with consumerCount"
				+ consumerCount);
		consumerConnector = kafka.consumer.Consumer
				.createJavaConsumerConnector(createConsumerConfig(serverUrl,
						groupId));
		executorService = Executors.newFixedThreadPool(consumerCount);
		topicCountMap.put(topic, new Integer(consumerCount));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector
				.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

		// now launch all the threads
		//
		executorService = Executors.newFixedThreadPool(consumerCount);

		// now create an object to consume the messages
		//
		int threadNumber = 0;
		for (final KafkaStream stream : streams) {
			executorService.submit(new Consumer(stream, threadNumber));
			threadNumber++;
		}
	}

	public void cleanup() {
		if (consumerConnector != null)
			consumerConnector.shutdown();
		if (executorService != null)
			executorService.shutdown();
	}

	private  ConsumerConfig createConsumerConfig(String a_zookeeper,
			String a_groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", a_zookeeper);
		props.put("group.id", a_groupId);
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");

		return new ConsumerConfig(props);
	}

}
