package ru.larnerweb.wikisselistener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.time.LocalTime;

@Service
public class SSEListener {
    private Logger logger = LoggerFactory.getLogger(SSEListener.class);

    @Autowired
    private KafkaWriter kafkaWriter;

    @PostConstruct
    public void consumeServerSentEvent() {
        WebClient client = WebClient.create("https://stream.wikimedia.org/v2/stream");
        ParameterizedTypeReference<ServerSentEvent<String>> type
                = new ParameterizedTypeReference<ServerSentEvent<String>>() {};

        Flux<ServerSentEvent<String>> eventStream = client.get()
                .uri("/recentchange")
                .retrieve()
                .bodyToFlux(type);

        eventStream.subscribe(
                content -> kafkaWriter.writeMessage(content.data()),
                error -> logger.error("Error receiving SSE: {}", error),
                () -> logger.info("Completed!!!"));
    }

}
