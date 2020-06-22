package com.example.demo.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.example.demo.MessageStreams;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component

public class MessageSender {
	private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);
	@Autowired
	private MessageStreams messageStreams;

	@Autowired
	private ObjectMapper objectMapper;

	public void send(Message<?> m) {
		try {
			// avoid too much magic and transform ourselves
			String jsonMessage = objectMapper.writeValueAsString(m);
			// wrap into a proper message for the transport (Kafka/Rabbit) and send it
			messageStreams.outboundGreetings()
					.send(MessageBuilder.withPayload(jsonMessage).setHeader("type", m.getType()).build());
		} catch (Exception e) {
			throw new RuntimeException("Could not tranform and send message due to: " + e.getMessage(), e);
		}
	}
}
