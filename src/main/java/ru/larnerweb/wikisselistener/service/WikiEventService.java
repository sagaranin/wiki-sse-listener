package ru.larnerweb.wikisselistener.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.larnerweb.wikisselistener.entitiy.WikiEvent;

import java.util.Date;

@Log4j2
@Service
public class WikiEventService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper mapper;

    private Date lastDate = new Date(1552057576);

    @SneakyThrows
    public void process(String jsonString){
        WikiEvent event = mapper.readValue(jsonString, WikiEvent.class);
        lastDate = event.getMeta().getDt();
        kafkaTemplate.send("wiki-src", jsonString);
    }

    public Date getMaxDt() {
        return lastDate;
    }
}
