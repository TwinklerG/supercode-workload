package com.example.demo.component;

import com.rabbitmq.stream.ByteCapacity;
import com.rabbitmq.stream.Consumer;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
    @PostConstruct
    void init() {
        Thread t = new ReceiverThread();
        t.start();
    }
}

class ReceiverThread extends Thread {
    @Override
    public void run() {
        Environment environment = Environment.builder().build();
        String stream = "Runner2Server";
        environment.streamCreator().stream(stream).maxLengthBytes(ByteCapacity.GB(1)).create();
        environment.consumerBuilder()
                .stream(stream)
                .offset(OffsetSpecification.first())
                .messageHandler((unused, message) -> {
                    System.out.println("Received message: " + new String(message.getBodyAsBinary()));
                }).build();
    }
}
