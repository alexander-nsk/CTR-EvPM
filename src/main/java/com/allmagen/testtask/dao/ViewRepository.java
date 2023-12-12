package com.allmagen.testtask.dao;

import com.allmagen.testtask.model.ViewEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface ViewRepository extends CrudRepository<ViewEntity, String> {
    @Query("SELECT COUNT(v) FROM ViewEntity v " +
            "WHERE DATE(v.regTime) >= :startDate AND DATE(v.regTime) <= :endDate AND v.mmDma = :mmDma " +
            "GROUP BY DATE(v.regTime)")
    List<Integer> getNumMmaByDates(LocalDate startDate, LocalDate endDate, int mmDma);

    @Query("SELECT COUNT(v) FROM ViewEntity v " +
            "WHERE DATE(v.regTime) >= :startDate AND DATE(v.regTime) <= :endDate AND v.siteId = :siteId " +
            "GROUP BY DATE(v.regTime)")
    List<Integer> getNumSiteIdByDates(LocalDate startDate, LocalDate endDate, String siteId);
}