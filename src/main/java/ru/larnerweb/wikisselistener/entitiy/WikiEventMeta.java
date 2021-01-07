package ru.larnerweb.wikisselistener.entitiy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
public class WikiEventMeta {
    @Column(length = 4000)
    String uri;
    @JsonProperty("request_id")
    String requestId;
    @Id
    String id;
    Date dt;
    String domain;
    String stream;
    String topic;
    int partition;
    @Column(name = "data_offset")
    long offset;

}
