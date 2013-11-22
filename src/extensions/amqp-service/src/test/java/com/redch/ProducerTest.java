package com.redch;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import static org.mockito.Mockito.*;

import com.rabbitmq.client.Channel;
import com.redch.exception.AMQPServiceException;


public class ProducerTest {
	
	String host = "127.0.0.1";
	String message = "Test message";
	String exchangeName = "samples";

	@Test(expected = AMQPServiceException.class)
	public void testConstructorWithEmptyHost() throws AMQPServiceException, IOException {
		assertNotNull(new Producer("", exchangeName));
	}
	
	@Test(expected = AMQPServiceException.class)
	public void testConstructorWithEmptyExchange() throws AMQPServiceException, IOException {
		assertNotNull(new Producer(host, ""));
	}
	
	@Test
	public void testSendMessage() throws IOException, AMQPServiceException {
		Channel mockChannel = mock(Channel.class);
		Producer producer = new Producer(host, exchangeName);
		producer.setChannel(mockChannel);
		producer.sendMessage(message);
		verify(mockChannel).basicPublish(exchangeName, "", null, message.getBytes());
	}

}
