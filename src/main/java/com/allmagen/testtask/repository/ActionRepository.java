package com.allmagen.testtask.repository;

import com.allmagen.testtask.model.ActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing ActionEntity instances.
 */
@Repository
public interface ActionRepository extends JpaRepository<ActionEntity, Long> {
}