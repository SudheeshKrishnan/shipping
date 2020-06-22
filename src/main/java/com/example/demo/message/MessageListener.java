package com.example.demo.message;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.example.demo.MessageStreams;
import com.example.demo.payload.GoodsShippedEventPayload;
import com.example.demo.payload.ShipGoodsCommandPayload;
import com.example.demo.rest.ShippingService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component

public class MessageListener {
	private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	  private MessageSender messageSender;
	@Autowired
	private ShippingService shippingService;

	@StreamListener(target = MessageStreams.INPUT, condition = "(headers['type']?:'')=='ShipGoodsCommand'")
	public void orderPlacedReceived(@Payload String messageJson)
			throws JsonParseException, JsonMappingException, IOException {
		// read data
		Message<ShipGoodsCommandPayload> message = objectMapper.readValue(messageJson,
				new TypeReference<Message<ShipGoodsCommandPayload>>() {
				});
            
		logger.info( "received ship goods requests "+ "::"+message.getCorrelationid()+"::"+"Trace ID"+"::"+message.getTraceid());

	    String shipmentId = shippingService.createShipment( //
	        message.getData().getPickId(), //
	        message.getData().getRecipientName(), //
	        message.getData().getRecipientAddress(), //
	        message.getData().getLogisticsProvider());
		
		
		
		
	    GoodsShippedEventPayload goodsShippedEventPayload= new GoodsShippedEventPayload();
	    goodsShippedEventPayload.setRefId(message.getTraceid());
	    goodsShippedEventPayload.setShipmentId(shipmentId);
	    
	    
		Message<GoodsShippedEventPayload> payload = new Message<GoodsShippedEventPayload>();
		payload.setCorrelationid("789");
		payload.setData(goodsShippedEventPayload);
		Date date = new Date();
		payload.setTime(date);
		payload.setTraceid("TID123");
		payload.setType("GoodsShippedEvent");
		
		messageSender.send(payload);
		
		
	}

}
