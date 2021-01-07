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
import java.text.SimpleDateFormat;

@Service
public class SSEListener {
    private Logger logger = LoggerFactory.getLogger(SSEListener.class);
    SimpleDateFormat sinceDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    @Autowired
    WikiEventService wikiEventService;

    @PostConstruct
    public void consumeServerSentEvent() {
        // todo получить самую позднюю дату

        WebClient client = WebClient.create("https://stream.wikimedia.org/v2/stream");
        ParameterizedTypeReference<ServerSentEvent<String>> type
                = new ParameterizedTypeReference<ServerSentEvent<String>>() {};

        Flux<ServerSentEvent<String>> eventStream = client.get()
                .uri("/recentchange?since=2018-06-14T00:00:00Z")
                .retrieve()
                .bodyToFlux(type);

        eventStream.subscribe(
                content -> wikiEventService.process(content.data()),
                error -> logger.error("Error receiving SSE: {}", error.toString()),
                () -> logger.info("Completed!!!"));
    }

}
