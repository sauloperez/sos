package com.redch;

import java.io.IOException;

import com.redch.exception.AMQPServiceException;

/**
 * The producer endpoint that writes to the queue.
 *
 */
public class Producer extends EndPoint {

	public Producer(String host, String exchangeName) throws IOException, AMQPServiceException{
		super(host, exchangeName);
	}

	public void sendMessage(String message) throws IOException {
	    if (connection == null) {
	      connect();
	    }
		channel.basicPublish(exchangeName, "", null, message.getBytes());
		System.out.println(" [x] Sent '" + message + "'");
	}
}
