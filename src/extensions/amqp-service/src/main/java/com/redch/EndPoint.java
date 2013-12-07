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

    protected static String EXCHANGE_TYPE = "fanout";

    protected String host;
    protected String exchangeName;
    protected Channel channel;
    protected Connection connection;
    protected ConnectionFactory connectionFactory;

    public EndPoint(String host, String exchangeName) throws IOException, AMQPServiceException {
        if (host.equals("")) {
            throw new AMQPServiceException("AMQP host must be specified");
        }
        if (exchangeName.equals("")) {
            throw new AMQPServiceException("AMQP exchangeName must be specified");
        }

        this.host = host;
        this.exchangeName = exchangeName;
        this.connectionFactory = new ConnectionFactory();
    }

    public void connect() throws IOException {
        connectionFactory.setHost(host);
        this.connection = connectionFactory.newConnection();
        setChannel(connection.createChannel());

        // Declare a named exchange of type fanout
        channel.exchangeDeclare(exchangeName, EXCHANGE_TYPE);
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setConnectionFactory(ConnectionFactory cf) {
        this.connectionFactory = cf;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Channel getChannel() {
        return channel;
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
