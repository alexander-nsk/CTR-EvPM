package com.allmagen.testtask.controller;

import com.allmagen.testtask.model.metrics.MmDmaCTR;
import com.allmagen.testtask.model.metrics.SiteIdCTR;
import com.allmagen.testtask.service.StatisticsService;
import com.opencsv.exceptions.CsvValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Operation(summary = "Upload view data from CSV")
    @RequestMapping(
            path = "views",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "text/plain")
    public ResponseEntity<String> uploadViewsFromFile(@RequestPart(value = "file") MultipartFile multipartFile) throws CsvValidationException, IOException {
        int viewsNumber = statisticsService.uploadViewsFromFile(multipartFile);
        return ResponseEntity.ok("Views uploaded: " + viewsNumber);
    }

    @Operation(summary = "Upload action data from CSV")
    @RequestMapping(
            path = "actions",
            method = RequestMethod.POST,
            produces = "text/plain")
    public ResponseEntity<String> uploadActionsFromFile(@RequestPart(value = "file") MultipartFile multipartFile) throws CsvValidationException, IOException {
        int actionsNumber = statisticsService.uploadActionsFromFile(multipartFile);
        return ResponseEntity.ok("Actions uploaded: " + actionsNumber);
    }

    @Operation(summary = "Load test data from directory resourses/testdata")
    @RequestMapping(
            path = "loadTestData",
            method = RequestMethod.POST,
            produces = "text/plain")
    public ResponseEntity<String> uploadViewsAndActionsFromFile() throws CsvValidationException, IOException {
        String message = statisticsService.loadTestDataToDataBase();
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Calculate number of views for given mmDma and dates")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Number of views calculated for mmDma")})
    @GetMapping(value = "/views/allByMmDma", produces = {"application/json"})
    public ResponseEntity<List<Integer>> getNumMmaByDates(
            @Parameter(description = "Date from", required = true)
            @RequestParam(value = "dateFrom")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateFrom,
            @Parameter(description = "Date to", required = true)
            @RequestParam(value = "dateTo")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateTo,
            @Parameter(description = "mmDma", required = true) @RequestParam(value = "mmDma") int mmDma) {

        List<Integer> mmDmaNums = statisticsService.getNumMmaByDates(dateFrom, dateTo, mmDma);

        return ResponseEntity.ok(mmDmaNums);
    }

    @Operation(summary = "Calculate number of views for given siteId and dates")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Number of views calculated for siteId")})
    @GetMapping(value = "/views/allBySiteId", produces = {"application/json"})
    public ResponseEntity<List<Integer>> getSiteIdNumsByDates(
            @Parameter(description = "Date from", required = true)
            @RequestParam(value = "dateFrom")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateFrom,
            @Parameter(description = "Date to", required = true)
            @RequestParam(value = "dateTo")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateTo,
            @Parameter(description = "siteId", required = true) @RequestParam(value = "siteId") String siteId) {

        List<Integer> siteIdNums = statisticsService.getNumSiteIdByDates(dateFrom, dateTo, siteId);

        return ResponseEntity.ok(siteIdNums);
    }

    @Operation(summary = "CTR for MmDma")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Array of mmDma and CTR pairs")})
    @GetMapping(value = "/views/ctrByMmDma", produces = {"application/json"})
    public ResponseEntity<List<MmDmaCTR>> getMmDmaCTR() {
        List<MmDmaCTR> pairList = statisticsService.getMmDmaCTR();
        return ResponseEntity.ok(pairList);
    }

    @Operation(summary = "CTR for MmDma with Tag")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Array of mmDma and CTR pairs with a specific tag")})
    @GetMapping(value = "/views/ctrByMmDmaByTag", produces = {"application/json"})
    public ResponseEntity<List<MmDmaCTR>> getMmDmaCTRByTag(@Parameter(description = "Tag") String tag) {
        List<MmDmaCTR> pairList = statisticsService.getMmDmaCTR(tag);
        return ResponseEntity.ok(pairList);
    }

    @Operation(summary = "CTR for SiteId")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Array of siteId and CTR pairs")})
    @GetMapping(value = "/views/ctrBySiteId", produces = {"application/json"})
    public ResponseEntity<List<SiteIdCTR>> getSiteIdCTR() {
        List<SiteIdCTR> pairList = statisticsService.getSiteIdCTR();
        return ResponseEntity.ok(pairList);
    }

    @Operation(summary = "CTR for SiteId with Tag")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Array of siteId and CTR pairs with a specific tag")})
    @GetMapping(value = "/views/ctrBySiteIdByTag", produces = {"application/json"})
    public ResponseEntity<List<SiteIdCTR>> getSiteIdCTRByTag(@Parameter(description = "Tag") String tag) {
        List<SiteIdCTR> pairList = statisticsService.getSiteIdCTR(tag);
        return ResponseEntity.ok(pairList);
    }

    @Operation(summary = "Clear Database Tables")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Tables successfully cleared")})
    @GetMapping(value = "/clearTables", produces = "text/plain")
    public ResponseEntity<String> clearDatabaseTables() {
        statisticsService.clearDatabaseTables();
        return ResponseEntity.ok("Database tables cleared successfully");
    }
}