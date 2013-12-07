package com.redch;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import static org.mockito.Mockito.*;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.redch.exception.AMQPServiceException;


public class ProducerTest {

	private static String host = "127.0.0.1";
	private static String message = "Test message";
	private static String exchangeName = "samples";

	@Test(expected = AMQPServiceException.class)
	public void testConstructorWithEmptyHost() throws AMQPServiceException, IOException {
		assertNotNull(new Producer("", exchangeName));
	}

	@Test(expected = AMQPServiceException.class)
	public void testConstructorWithEmptyExchange() throws AMQPServiceException, IOException {
		assertNotNull(new Producer(host, ""));
	}

	@Test
	public void testConstructorWithHostAndExchangeName() throws IOException, AMQPServiceException {
		Producer producer = new Producer(host, exchangeName);
		assertNotNull(producer.getHost());
		assertNotNull(producer.getExchangeName());
		assertNotNull(producer.getConnectionFactory());
	}

	@Test
	public void testConnect() throws IOException, AMQPServiceException {
		ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);
		Connection mockConnection = mock(Connection.class);
		Channel mockChannel = mock(Channel.class);

		when(mockConnectionFactory.newConnection()).thenReturn(mockConnection);
		when(mockConnection.createChannel()).thenReturn(mockChannel);

		Producer producer = new Producer(host, exchangeName);
		producer.setConnectionFactory(mockConnectionFactory);

		producer.connect();
		assertNotNull(producer.getChannel());
		assertNotNull(producer.getConnection());
	}

	@Test
	public void testIsConnectedToSendMessage() throws IOException, AMQPServiceException {
		ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);
		Connection mockConnection = mock(Connection.class);
		Channel mockChannel = mock(Channel.class);

		when(mockConnectionFactory.newConnection()).thenReturn(mockConnection);
		when(mockConnection.createChannel()).thenReturn(mockChannel);

		Producer producer = new Producer(host, exchangeName);
		producer.setConnectionFactory(mockConnectionFactory);

		producer.sendMessage(message);
		assertNotNull(producer.getConnection());
	}

	@Test
	public void testSendMessageToChannel() throws IOException, AMQPServiceException {
		ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);
		Connection mockConnection = mock(Connection.class);
		Channel mockChannel = mock(Channel.class);

		when(mockConnectionFactory.newConnection()).thenReturn(mockConnection);
		when(mockConnection.createChannel()).thenReturn(mockChannel);

		Producer producer = new Producer(host, exchangeName);
		producer.setConnectionFactory(mockConnectionFactory);

		producer.sendMessage(message);
		assertNotNull(producer.getChannel());
	}

	@Test
	public void testSendMessage() throws IOException, AMQPServiceException {
		ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);
		Connection mockConnection = mock(Connection.class);
		Channel mockChannel = mock(Channel.class);

		when(mockConnectionFactory.newConnection()).thenReturn(mockConnection);
		when(mockConnection.createChannel()).thenReturn(mockChannel);

		Producer producer = new Producer(host, exchangeName);
		producer.setConnectionFactory(mockConnectionFactory);

		producer.sendMessage(message);
		verify(mockChannel).basicPublish(exchangeName, "", null, message.getBytes());
	}

}
