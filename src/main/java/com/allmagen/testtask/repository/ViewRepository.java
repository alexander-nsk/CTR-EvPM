package com.allmagen.testtask.repository;

import com.allmagen.testtask.model.ViewEntity;
import com.allmagen.testtask.model.metrics.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * Repository interface for managing ViewEntity instance
 */
@Repository
public interface ViewRepository extends JpaRepository<ViewEntity, String> {

    @Query("SELECT FUNCTION('DATE_TRUNC', :interval, v.regTime) AS intervalStart, " +
            "SUM(COALESCE(a.count, 0)) * 100.0 / COUNT(DISTINCT v.uid) AS ctr " +
            "FROM ViewEntity v " +
            "FULL JOIN ActionEntity a ON (v.uid = a.viewEntity.uid AND ((:tag is null AND (a.tag = 'fclick' OR NOT (a.tag LIKE 'v%'))) or a.tag = :tag))" +
            "WHERE (v.regTime BETWEEN :startDate AND :endDate)" +
            "GROUP BY intervalStart " +
            "ORDER BY intervalStart")
    Stream<CtrDates> getCTR(LocalDateTime startDate, LocalDateTime endDate, String interval, String tag);

    @Query("SELECT FUNCTION('DATE_TRUNC', :interval, v.regTime) AS intervalStart, " +
            "SUM(COALESCE(a.count, 0)) * 1.0 / COUNT(DISTINCT v.uid) AS ctr " +
            "FROM ViewEntity v " +
            "FULL JOIN ActionEntity a ON (v.uid = a.viewEntity.uid AND (:tag is null OR a.tag = :tag OR a.tag = CONCAT('v', :tag)))" +
            "WHERE (v.regTime BETWEEN :startDate AND :endDate)" +
            "GROUP BY intervalStart " +
            "ORDER BY intervalStart")
    Stream<CtrDates> getEvPM(LocalDateTime startDate, LocalDateTime endDate, String interval, String tag);


    @Query("SELECT v.mmDma AS mmDma, COUNT(v) AS count " +
            "FROM ViewEntity v  " +
            "WHERE DATE(v.regTime) >= :startDate AND DATE(v.regTime) <= :endDate " +
            "GROUP BY mmDma")
    Stream<MmDmaCount> getViewsCountByMmDma(LocalDate startDate, LocalDate endDate);

    @Query("SELECT v.siteId AS siteId, COUNT(v) AS count " +
            "FROM ViewEntity v  " +
            "WHERE DATE(v.regTime) >= :startDate AND DATE(v.regTime) <= :endDate " +
            "GROUP BY siteId")
    Stream<SiteIdCount> getViewsCountBySiteId(LocalDate startDate, LocalDate endDate);

    @Query("SELECT v.mmDma AS mmDma, " +
            "SUM(COALESCE(a.count, 0)) * 1.0 / COUNT(DISTINCT v.uid) AS ctr " +
            "FROM ViewEntity v " +
            "FULL JOIN ActionEntity a ON (v.uid = a.viewEntity.uid AND ((:tag is null AND (a.tag = 'fclick' OR NOT (a.tag LIKE 'v%'))) or a.tag = :tag))" +
            "WHERE (v.regTime BETWEEN :startDate AND :endDate)" +
            "GROUP BY mmDma " +
            "ORDER BY mmDma")
    Stream<MmDmaCTR> getCtrAggregateByMmDma(LocalDateTime startDate, LocalDateTime endDate, String tag);

    @Query("SELECT v.siteId AS siteId, " +
            "SUM(COALESCE(a.count, 0)) * 1.0 / COUNT(DISTINCT v.uid) AS ctr " +
            "FROM ViewEntity v " +
            "FULL JOIN ActionEntity a ON (v.uid = a.viewEntity.uid AND ((:tag is null AND (a.tag = 'fclick' OR NOT (a.tag LIKE 'v%'))) or a.tag = :tag))" +
            "WHERE (v.regTime BETWEEN :startDate AND :endDate)" +
            "GROUP BY siteId " +
            "ORDER BY siteId")
    Stream<SiteIdCTR> getCtrAggregateBySiteId(LocalDateTime startDate, LocalDateTime endDate, String tag);


}