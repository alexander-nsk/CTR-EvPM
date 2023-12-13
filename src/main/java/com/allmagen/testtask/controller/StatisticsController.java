package com.allmagen.testtask.controller;

import com.allmagen.testtask.model.dto.MmDmaCTR;
import com.allmagen.testtask.model.dto.SiteIdCTR;
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

    @Operation(summary = "Add view data from CSV")
    @RequestMapping(
            path = "views",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "application/json")
    public ResponseEntity<String> uploadViewsFromFile(@RequestPart(value = "file") MultipartFile multipartFile) throws CsvValidationException, IOException {
        statisticsService.uploadViewsFromFile(multipartFile);
        return ResponseEntity.ok("views uploaded");
    }

    @Operation(summary = "add action data from CSV")
    @RequestMapping(
            path = "actions",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "application/json")
    public ResponseEntity<String> uploadActionsFromFile(@RequestPart(value = "file") MultipartFile multipartFile) throws CsvValidationException, IOException {
        statisticsService.uploadActionsFromFile(multipartFile);
        return ResponseEntity.ok("actions uploaded");
    }

    @Operation(summary = "Calculate number of views for given mmDma and dates")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "mmDma number calculated")})
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

    @Operation(summary = "Calculate number of views for given sideId and dates")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "SiteId number calculated")})
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
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Array of pairs of mmDma and CTR")})
    @GetMapping(value = "/views/ctrByMmDma", produces = {"application/json"})
    public ResponseEntity<List<MmDmaCTR>> getMmDmaCTR() {
        List<MmDmaCTR> pairList = statisticsService.getMmDmaCTR();
        return ResponseEntity.ok(pairList);
    }

    @Operation(summary = "CTR for MmDma")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Array of pairs of mmDma and CTR")})
    @GetMapping(value = "/views/ctrByMmDmaByTag", produces = {"application/json"})
    public ResponseEntity<List<MmDmaCTR>> getMmDmaCTRByTag(@Parameter(description = "tag") String tag) {
        List<MmDmaCTR> pairList = statisticsService.getMmDmaCTR(tag);
        return ResponseEntity.ok(pairList);
    }

    @Operation(summary = "CTR for site id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Array of pairs of site id and CTR")})
    @GetMapping(value = "/views/ctrBySiteId", produces = {"application/json"})
    public ResponseEntity<List<SiteIdCTR>> getSiteIdCTR() {
        List<SiteIdCTR> pairList = statisticsService.getSiteIdCTR();
        return ResponseEntity.ok(pairList);
    }

    @Operation(summary = "CTR for site id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Array of pairs of site id and CTR")})
    @GetMapping(value = "/views/ctrBySiteIdByTag", produces = {"application/json"})
    public ResponseEntity<List<SiteIdCTR>> getSiteIdCTRByTag(@Parameter(description = "tag") String tag) {
        List<SiteIdCTR> pairList = statisticsService.getSiteIdCTR(tag);
        return ResponseEntity.ok(pairList);
    }
}