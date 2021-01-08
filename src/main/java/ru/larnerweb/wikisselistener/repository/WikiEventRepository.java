package ru.larnerweb.wikisselistener.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.larnerweb.wikisselistener.entitiy.WikiEvent;

import java.util.Date;

public interface WikiEventRepository extends CrudRepository<WikiEvent, Long> {

    @Query(value = "select max(dt) from wiki_event_meta", nativeQuery = true)
    Date findMaxDt();
}
