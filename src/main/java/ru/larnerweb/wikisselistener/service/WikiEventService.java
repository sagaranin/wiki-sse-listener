package ru.larnerweb.wikisselistener.service;

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

    @Autowired
    WikiEventRepository wikiEventRepository;

    @Autowired
    JSONParserService parser;

    public void process(String jsonString){
        WikiEvent event;
        try {
            event = parser.parse(jsonString);
            wikiEventRepository.save(event);

        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        } catch (DataIntegrityViolationException e){
            log.error("Record already exists");
        }
    }

    public Date getMaxDt() {
        return wikiEventRepository.findMaxDt();
    }
}
