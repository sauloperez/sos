package com.redch;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Test;

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
		Producer producerMock = mock(Producer.class);
		AMQPService service = new AMQPServiceImpl(host, exchangeName);
		service.setProducer(producerMock);
		
		service.publish(message);
		
		verify(producerMock, times(1)).sendMessage(message);
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
