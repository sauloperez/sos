package com.redch;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Represents a connection with a queue
 *
 */
public abstract class EndPoint {
	
    protected Channel channel;
    protected Connection connection;
    protected String exchangeName;
	
    public EndPoint(String host, String exchangeName) throws IOException {
		this.exchangeName = exchangeName;
		
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();
		
        // Declare a named exchange of type fanout
		channel.exchangeDeclare(exchangeName, "fanout");
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
