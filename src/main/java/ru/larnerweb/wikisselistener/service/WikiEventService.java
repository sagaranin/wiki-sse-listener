package ru.larnerweb.wikisselistener.service;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.larnerweb.wikisselistener.entitiy.WikiEvent;
import ru.larnerweb.wikisselistener.repository.WikiEventRepository;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@Log4j2
@Service
public class WikiEventService {
    String hostname = InetAddress.getLocalHost().getHostName();
    Counter eventCounter = Metrics.counter("app.event.counter.consumed", "host", hostname);
    Counter errorCounter = Metrics.counter("app.event.counter.error", "host", hostname);
    Counter exceptionCounter = Metrics.counter("app.event.counter.exception",  "host", hostname);

    @Autowired
    WikiEventRepository wikiEventRepository;

    @Autowired
    JSONParserService parser;

    public WikiEventService() throws UnknownHostException {
    }

    @Timed
    public void process(String jsonString){
        eventCounter.increment();

        WikiEvent event;
        try {
            event = parser.parse(jsonString);
            wikiEventRepository.save(event);

        } catch (IOException e) {
            exceptionCounter.increment();
            log.error(e.getLocalizedMessage());
        } catch (DataIntegrityViolationException e){
            errorCounter.increment();
            log.error("Record already exists");
        }
    }

    @Timed
    public Date getMaxDt() {
        return wikiEventRepository.findMaxDt();
    }
}
