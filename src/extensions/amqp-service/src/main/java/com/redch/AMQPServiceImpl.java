package com.redch;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redch.exception.AMQPServiceException;

public class AMQPServiceImpl implements AMQPService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AMQPServiceImpl.class);
	
	private Producer producer;
	
	public AMQPServiceImpl(String host, String exchangeName) throws IOException, AMQPServiceException {
		try {
			this.producer = new Producer(host, exchangeName);
		} catch (AMQPServiceException e) {
			LOGGER.debug("AMQP connection failed");
			throw e;
		}
	}

	@Override
	public void publish(String message) throws IOException {
		producer.sendMessage(message);
	}

	@Override
	public void stop() throws IOException {
		producer.close();
	}

	@Override
	public void setProducer(Producer producer) {
		this.producer = producer;
	}

}
