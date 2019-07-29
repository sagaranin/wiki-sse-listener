package ru.larnerweb.wikisselistener.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaWriter {

    private final KafkaTemplate kafkaTemplate;

    @Autowired
    public KafkaWriter(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void writeMessage(String message){
        kafkaTemplate.send("wikistream", message);
    }

}
