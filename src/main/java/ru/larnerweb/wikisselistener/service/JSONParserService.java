package ru.larnerweb.wikisselistener.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import ru.larnerweb.wikisselistener.entitiy.WikiEvent;

import java.io.IOException;

@Service
public class JSONParserService {

    public WikiEvent parse(String jsonString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        WikiEvent wikiEvent = objectMapper.readValue(jsonString, WikiEvent.class);

        return wikiEvent;
    }

}
