package com.fga.samples.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.log4j.Logger;



public class CloseHook extends Thread {

	private static final Logger logger = Logger.getLogger(CloseHook.class);
	
	private KafkaProducer kafkaProducer;

	
	
	public CloseHook(KafkaProducer kafkaProducer) {
		this.kafkaProducer = kafkaProducer;
	}


	@Override
	public void run() {
		try {
			kafkaProducer.close();
		} catch (Exception e) {
			logger.error(e);
		}
		logger.info("Producer closed");
	}
}
