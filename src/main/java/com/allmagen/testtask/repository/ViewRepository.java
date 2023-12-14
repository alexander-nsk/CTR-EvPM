package com.allmagen.testtask.repository;

import com.allmagen.testtask.model.ViewEntity;
import com.allmagen.testtask.model.metrics.MmDmaCTR;
import com.allmagen.testtask.model.metrics.SiteIdCTR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface ViewRepository extends JpaRepository<ViewEntity, String> {
    @Query("SELECT COUNT(v) FROM ViewEntity v " +
            "WHERE DATE(v.regTime) >= :startDate AND DATE(v.regTime) <= :endDate AND v.mmDma = :mmDma " +
            "GROUP BY DATE(v.regTime)")
    List<Integer> getNumMmaByDates(LocalDate startDate, LocalDate endDate, int mmDma);

    @Query("SELECT COUNT(v) FROM ViewEntity v " +
            "WHERE DATE(v.regTime) >= :startDate AND DATE(v.regTime) <= :endDate AND v.siteId = :siteId " +
            "GROUP BY DATE(v.regTime)")
    List<Integer> getNumSiteIdByDates(LocalDate startDate, LocalDate endDate, String siteId);

    @Query("SELECT ve.mmDma AS mmDma, " +
            "SUM(COALESCE(ae.count, 0)) * 1.0 / COUNT(DISTINCT ve.uid) AS ctr " +
            "FROM ViewEntity ve " +
            "FULL JOIN ActionEntity ae ON (ve.uid = ae.viewEntity.uid AND (ae.tag = 'fclick' OR NOT (ae.tag LIKE 'v%'))) " +
            "WHERE ve.mmDma IS NOT NULL " +
            "GROUP BY ve.mmDma")
    List<MmDmaCTR> getMmDmaCTR();

    @Query("SELECT ve.mmDma AS mmDma, " +
            "SUM(COALESCE(ae.count, 0)) * 1.0 / COUNT(DISTINCT ve.uid) AS ctr " +
            "FROM ViewEntity ve " +
            "FULL JOIN ActionEntity ae ON (ve.uid = ae.viewEntity.uid AND (ae.tag = :tag OR ae.tag = CONCAT('v', :tag))) " +
            "WHERE ve.mmDma IS NOT NULL " +
            "GROUP BY ve.mmDma")
    List<MmDmaCTR> getMmDmaCTR(String tag);

    @Query("SELECT ve.siteId AS siteId, " +
            "SUM(COALESCE(ae.count, 0)) * 1.0 / COUNT(DISTINCT ve.uid) AS ctr " +
            "FROM ViewEntity ve " +
            "FULL JOIN ActionEntity ae ON (ve.uid = ae.viewEntity.uid AND (ae.tag = 'fclick' OR NOT (ae.tag LIKE 'v%'))) " +
            "WHERE ve.siteId IS NOT NULL " +
            "GROUP BY ve.siteId")
    List<SiteIdCTR> getSiteIdCTR();

    @Query("SELECT ve.siteId AS siteId, " +
            "SUM(COALESCE(ae.count, 0)) * 1.0 / COUNT(DISTINCT ve.uid) AS ctr " +
            "FROM ViewEntity ve " +
            "FULL JOIN ActionEntity ae ON (ve.uid = ae.viewEntity.uid AND (ae.tag = :tag OR ae.tag = CONCAT('v', :tag))) " +
            "WHERE ve.siteId IS NOT NULL " +
            "GROUP BY ve.siteId")
    List<SiteIdCTR> getSiteIdCTR(String tag);
}