package com.allmagen.testtask.dao;

import com.allmagen.testtask.model.ActionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionRepository extends CrudRepository<ActionEntity, Long> {
}