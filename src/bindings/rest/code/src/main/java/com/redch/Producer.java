package com.redch;

import java.io.IOException;

/**
 * The producer endpoint that writes to the queue.
 *
 */
public class Producer extends EndPoint {
	
	public Producer(String host, String exchangeName) throws IOException{
		super(host, exchangeName);
	}

	public void sendMessage(String message) throws IOException {
		channel.basicPublish(exchangeName, "", null, message.getBytes());
		System.out.println(" [x] Sent '" + message + "'");
	}	
}