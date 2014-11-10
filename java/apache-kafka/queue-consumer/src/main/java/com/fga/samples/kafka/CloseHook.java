package com.fga.samples.kafka;

import com.fga.samples.kafka.consumer.ConsumerManager;

public class CloseHook extends Thread {

	private ConsumerManager consumerManager;
	public CloseHook(ConsumerManager consumerManager) {
		this.consumerManager = consumerManager;
	}
	
	@Override
	public synchronized void start() {
		consumerManager.cleanup();
	}
}
