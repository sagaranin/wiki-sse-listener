package ru.larnerweb.wikisselistener.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.larnerweb.wikisselistener.entitiy.WikiEvent;

import java.util.Date;

@Log4j2
@Service
@AllArgsConstructor
public class WikiEventService {

    private KafkaTemplate<String, String> kafkaTemplate;
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
