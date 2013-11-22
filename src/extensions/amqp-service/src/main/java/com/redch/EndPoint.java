package com.redch;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.redch.exception.AMQPServiceException;

/**
 * Represents a connection with a queue
 *
 */
public abstract class EndPoint {
	
    protected Channel channel;
    protected Connection connection;
    protected String exchangeName;
	
    public EndPoint(String host, String exchangeName) throws IOException, AMQPServiceException {
    	if (host.equals("")) {
    		throw new AMQPServiceException("AMQP host must be specified");
    	}
    	if (exchangeName.equals("")) {
    		throw new AMQPServiceException("AMQP exchangeName must be specified");
    	}
    	
		this.exchangeName = exchangeName;
		
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();
		
        // Declare a named exchange of type fanout
		channel.exchangeDeclare(exchangeName, "fanout");
    }	
	
    public void setChannel(Channel channel) {
    	this.channel = channel;
    }
    
    /**
     * Close channel and connection. Not necessary as it happens implicitly any way. 
     * @throws IOException
     */
     public void close() throws IOException{
        this.channel.close();
        this.connection.close();
     }
}
