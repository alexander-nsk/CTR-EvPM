package com.allmagen.testtask.dao;

import com.allmagen.testtask.model.ViewEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewRepository extends CrudRepository<ViewEntity, Long> {
}