package com.redch;

import java.io.IOException;

public interface AMQPService {
	
	void publish(String message) throws IOException;
	
	void stop() throws IOException;

	void setProducer(Producer producer);
}
