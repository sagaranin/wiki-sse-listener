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
import java.util.Date;

@Log4j2
@Service
public class WikiEventService {

    Counter eventCounter = Metrics.counter("app.event.counter.consumed");
    Counter errorCounter = Metrics.counter("app.event.counter.error");

    @Autowired
    WikiEventRepository wikiEventRepository;

    @Autowired
    JSONParserService parser;

    @Timed
    public void process(String jsonString){
        eventCounter.increment();

        WikiEvent event;
        try {
            event = parser.parse(jsonString);
            wikiEventRepository.save(event);

        } catch (IOException e) {
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
