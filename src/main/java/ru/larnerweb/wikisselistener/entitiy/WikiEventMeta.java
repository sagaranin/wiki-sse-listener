package ru.larnerweb.wikisselistener.entitiy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class WikiEventMeta {
    String uri;
    @JsonProperty("request_id")
    String requestId;
    String id;
    Date dt;
    String domain;
    String stream;
    String topic;
    int partition;
    long offset;

}
