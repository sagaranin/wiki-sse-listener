package ru.larnerweb.wikisselistener.entitiy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.util.Map;

@Data
@Getter
@Entity
@JsonIgnoreProperties({"log_id", "log_type", "log_action", "log_params", "log_action_comment"})
public class WikiEvent {
    @JsonProperty("$schema")
    String schema;
    @OneToOne(cascade = CascadeType.ALL)
//    @MapsId
    @JoinColumn(name = "request_id")
    WikiEventMeta meta;
    @Id
    long id;
    @Column(length = 1000)
    String type;
    int namespace;
    @Column(length = 1000)
    String title;
    @Lob
    String comment;
    long timestamp;
    @Column(length = 1000, name = "username")
    String user;
    boolean bot;
    boolean minor;
    boolean patrolled;
    long length_old;
    long length_new;
    long revision_old;
    long revision_new;
    @JsonProperty("server_url")
    @Column(length = 4000)
    String serverUrl;
    @JsonProperty("server_name")
    String serverName;
    @JsonProperty("server_script_path")
    @Column(length = 4000)
    String serverScriptPath;
    String wiki;
    @Lob
    String parsedcomment;

    public long getId() {
        return id;
    }

    @JsonProperty("length")
    private void unpackLength(Map<String, Long> length) {
        if (length != null && length.containsKey("old") && length.containsKey("new")) {
            length_old = length.get("old");
            length_new = length.get("new");
        }
    }

    @JsonProperty("revision")
    private void unpackRevision(Map<String, Long> revision) {
        if (revision != null && revision.containsKey("old") && revision.containsKey("new")) {
            revision_old = revision.get("old");
            revision_new = revision.get("new");
        }
    }
}
