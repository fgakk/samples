package com.fga.samples.kafka.consumer;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

import org.apache.log4j.Logger;

public class Consumer implements Runnable {

	private static final Logger logger = Logger.getLogger(Consumer.class);

	private KafkaStream m_stream;
	private int m_threadNumber;

	public Consumer(KafkaStream a_stream, int a_threadNumber) {
		m_threadNumber = a_threadNumber;
		m_stream = a_stream;
	}

	public void run() {
		ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
		while (it.hasNext())
			logger.info("Thread " + m_threadNumber + ": "
					+ new String(it.next().message()));
		logger.info("Shutting down Thread: " + m_threadNumber);
	}

}
