package com.redch;

import java.io.IOException;

public class AMQPServiceImpl implements AMQPService {
	
	private Producer producer;
	
	public AMQPServiceImpl(String exchangeName) throws IOException {
		this.producer = new Producer(exchangeName);
	}

	@Override
	public void publish(String message) throws IOException {
		producer.sendMessage(message);
	}

	@Override
	public void stop() throws IOException {
		producer.close();
	}

}
