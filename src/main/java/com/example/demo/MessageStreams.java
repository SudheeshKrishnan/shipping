package com.example.demo;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Service; 
@Service
public interface MessageStreams {
    String INPUT = "greetings-in";
    String OUTPUT = "greetings-out";
    @Input(INPUT)
    SubscribableChannel inboundGreetings();
    @Output(OUTPUT)
    MessageChannel outboundGreetings();
   
}