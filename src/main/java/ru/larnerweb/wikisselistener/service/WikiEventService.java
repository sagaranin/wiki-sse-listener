package ru.larnerweb.wikisselistener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.larnerweb.wikisselistener.entitiy.WikiEvent;
import ru.larnerweb.wikisselistener.repository.WikiEventRepository;

import java.io.IOException;

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

            if (!wikiEventRepository.existsById(event.getId()))
                wikiEventRepository.save(event);

        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());
        }
    }
}
