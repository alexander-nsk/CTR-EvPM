package com.allmagen.testtask.repository;

import com.allmagen.testtask.model.ViewEntity;
import com.allmagen.testtask.model.metrics.MmDmaCTR;
import com.allmagen.testtask.model.metrics.SiteIdCTR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.stream.Stream;

/**
 * Repository interface for managing ViewEntity instance
 */
@Repository
public interface ViewRepository extends JpaRepository<ViewEntity, String> {
    /**
     * Retrieves the number of views for a given mmDma within the specified date range.
     *
     * @param startDate The start date of the date range.
     * @param endDate   The end date of the date range.
     * @param mmDma     The mmDma for which to calculate the number of views.
     * @return A list of integers representing the number of views for the given mmDma and dates.
     */
    @Query("SELECT COUNT(v) FROM ViewEntity v " +
            "WHERE DATE(v.regTime) >= :startDate AND DATE(v.regTime) <= :endDate AND v.mmDma = :mmDma " +
            "GROUP BY DATE(v.regTime)")
    Stream<Integer> getNumMmaByDates(LocalDate startDate, LocalDate endDate, int mmDma);

    /**
     * Retrieves the number of views for a given siteId within the specified date range.
     *
     * @param startDate The start date of the date range.
     * @param endDate   The end date of the date range.
     * @param siteId    The siteId for which to calculate the number of views.
     * @return A list of integers representing the number of views for the given siteId and dates.
     */
    @Query("SELECT COUNT(v) FROM ViewEntity v " +
            "WHERE DATE(v.regTime) >= :startDate AND DATE(v.regTime) <= :endDate AND v.siteId = :siteId " +
            "GROUP BY DATE(v.regTime)")
    Stream<Integer> getNumSiteIdByDates(LocalDate startDate, LocalDate endDate, String siteId);

    /**
     * Retrieves the CTR for MmDma.
     *
     * @return A list of MmDmaCTR representing the MmDma and CTR pairs.
     */
    @Query("SELECT ve.mmDma AS mmDma, " +
            "SUM(COALESCE(ae.count, 0)) * 1.0 / COUNT(DISTINCT ve.uid) AS ctr " +
            "FROM ViewEntity ve " +
            "FULL JOIN ActionEntity ae ON (ve.uid = ae.viewEntity.uid AND (ae.tag = 'fclick' OR NOT (ae.tag LIKE 'v%'))) " +
            "WHERE ve.mmDma IS NOT NULL " +
            "GROUP BY ve.mmDma")
    Stream<MmDmaCTR> getMmDmaCTR();

    /**
     * Retrieves the CTR for MmDma with a specific tag.
     *
     * @param tag The tag to filter the results.
     * @return A list of MmDmaCTR representing the MmDma and CTR pairs with the specified tag.
     */
    @Query("SELECT ve.mmDma AS mmDma, " +
            "SUM(COALESCE(ae.count, 0)) * 1.0 / COUNT(DISTINCT ve.uid) AS ctr " +
            "FROM ViewEntity ve " +
            "FULL JOIN ActionEntity ae ON (ve.uid = ae.viewEntity.uid AND (ae.tag = :tag OR ae.tag = CONCAT('v', :tag))) " +
            "WHERE ve.mmDma IS NOT NULL " +
            "GROUP BY ve.mmDma")
    Stream<MmDmaCTR> getMmDmaCTR(String tag);

    /**
     * Retrieves the CTR for SiteId.
     *
     * @return A list of SiteIdCTR representing the SiteId and CTR pairs.
     */
    @Query("SELECT ve.siteId AS siteId, " +
            "SUM(COALESCE(ae.count, 0)) * 1.0 / COUNT(DISTINCT ve.uid) AS ctr " +
            "FROM ViewEntity ve " +
            "FULL JOIN ActionEntity ae ON (ve.uid = ae.viewEntity.uid AND (ae.tag = 'fclick' OR NOT (ae.tag LIKE 'v%'))) " +
            "WHERE ve.siteId IS NOT NULL " +
            "GROUP BY ve.siteId")
    Stream<SiteIdCTR> getSiteIdCTR();

    /**
     * Retrieves the CTR for SiteId with a specific tag.
     *
     * @param tag The tag to filter the results.
     * @return A list of SiteIdCTR representing the SiteId and CTR pairs with the specified tag.
     */
    @Query("SELECT ve.siteId AS siteId, " +
            "SUM(COALESCE(ae.count, 0)) * 1.0 / COUNT(DISTINCT ve.uid) AS ctr " +
            "FROM ViewEntity ve " +
            "FULL JOIN ActionEntity ae ON (ve.uid = ae.viewEntity.uid AND (ae.tag = :tag OR ae.tag = CONCAT('v', :tag))) " +
            "WHERE ve.siteId IS NOT NULL " +
            "GROUP BY ve.siteId")
    Stream<SiteIdCTR> getSiteIdCTR(String tag);
}