package com.redch;

import java.io.IOException;

/**
 * The producer endpoint that writes to the queue.
 *
 */
public class Producer extends EndPoint {
	
	public Producer(String exchangeName) throws IOException{
		super(exchangeName);
	}

	public void sendMessage(String message) throws IOException {
		channel.basicPublish(exchangeName, "", null, message.getBytes());
		System.out.println(" [x] Sent '" + message + "'");
	}	
}