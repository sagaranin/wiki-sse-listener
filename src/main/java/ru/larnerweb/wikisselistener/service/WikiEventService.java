package ru.larnerweb.wikisselistener.service;

import io.micrometer.core.annotation.Timed;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Log4j2
@Service
public class WikiEventService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Timed(value = "json_processing")
    public void process(String jsonString){
        kafkaTemplate.send("wiki", jsonString);
    }

    public Date getMaxDt() {
        return new Date();
    }
}
