package com.redch;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.redch.exception.AMQPServiceException;

public class AMQPServiceImplTest {

	String host = "127.0.0.1";
	String message = "Test message";
	String exchangeName = "samples";

	@Test(expected = AMQPServiceException.class)
	public void testConstructorWithEmptyHost() throws AMQPServiceException, IOException {
		assertNotNull(new AMQPServiceImpl("", exchangeName));
	}

	@Test(expected = AMQPServiceException.class)
	public void testConstructorWithEmptyExchange() throws AMQPServiceException, IOException {
		assertNotNull(new AMQPServiceImpl(host, ""));
	}

	@Test
	public void testPublish() throws IOException, AMQPServiceException {
		ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);
		Connection mockConnection = mock(Connection.class);
		Channel mockChannel = mock(Channel.class);

		when(mockConnectionFactory.newConnection()).thenReturn(mockConnection);
		when(mockConnection.createChannel()).thenReturn(mockChannel);

		Producer mockProducer = mock(Producer.class);
		mockProducer.setConnectionFactory(mockConnectionFactory);

		AMQPService service = new AMQPServiceImpl(host, exchangeName);
		service.setProducer(mockProducer);

		service.publish(message);

		verify(mockProducer, times(1)).sendMessage(message);
	}

	@Test
	public void testStop() throws IOException, AMQPServiceException {
		Producer producerMock = mock(Producer.class);
		AMQPService service = new AMQPServiceImpl(host, exchangeName);
		service.setProducer(producerMock);

		service.stop();

		verify(producerMock, times(1)).close();
	}

}
