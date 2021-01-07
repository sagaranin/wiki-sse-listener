package ru.larnerweb.wikisselistener.repository;

import org.springframework.data.repository.CrudRepository;
import ru.larnerweb.wikisselistener.entitiy.WikiEvent;

public interface WikiEventRepository extends CrudRepository<WikiEvent, Long> {
}
