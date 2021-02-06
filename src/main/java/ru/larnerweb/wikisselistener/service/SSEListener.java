package ru.larnerweb.wikisselistener.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;

@Log4j2
@Service
public class SSEListener {
    private final SimpleDateFormat sinceDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private Disposable sub;

    @Autowired
    WikiEventService wikiEventService;

    @PostConstruct
    private void consumeServerSentEvent() {

        WebClient client = WebClient.create("https://stream.wikimedia.org/v2/stream");
        ParameterizedTypeReference<ServerSentEvent<String>> type
                = new ParameterizedTypeReference<ServerSentEvent<String>>() {
        };

        String sinceDate = sinceDateFormat.format(wikiEventService.getMaxDt());
        log.info("Load stream since {}", sinceDate);

        Flux<ServerSentEvent<String>> eventStream = client.get()
                .uri("/recentchange?since=" + sinceDate)
                .retrieve()
                .bodyToFlux(type)
                .filter(s -> s.data() != null && s.data().contains("},\"id\""));

        sub = eventStream.subscribe(
                content -> wikiEventService.process(content.data()),
                error -> log.error("Error receiving SSE: {}", error.toString()),
                () -> log.info("Completed!!!"));
    }

    @Scheduled(fixedRate = 60000)
    private void checkStreamStatus() {
        if (sub.isDisposed()) {
            log.warn("Recreate WebClient...");
            consumeServerSentEvent();
        }
    }
}
