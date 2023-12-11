package com.allmagen.testtask;

import com.allmagen.testtask.model.ActionEntity;
import com.allmagen.testtask.model.ViewEntity;
import com.allmagen.testtask.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    @Operation(summary = "add view data from CSV")
    @RequestMapping(
            path = "view",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "application/json")
    public ResponseEntity<String> addViewData(@RequestPart(value = "file") MultipartFile multipartFile) {
        statisticsService.addViewFromFile(multipartFile);
        return ResponseEntity.ok("Muito obregado");
    }

    @Operation(summary = "add action data from CSV")
    @RequestMapping(
            path = "action",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "application/json")
    public ResponseEntity<String> addActionData(@RequestPart(value = "file") MultipartFile multipartFile) {
        statisticsService.addActionFromFile(multipartFile);
        return ResponseEntity.ok("Muito obregado");
    }

    @Operation(summary = "get views from DB(for tests)")
    @RequestMapping(
            path = "viewscount",
            method = RequestMethod.GET,
            produces = {"application/json"})

    public ResponseEntity<Iterable<ViewEntity>> getViews() {
        return ResponseEntity.ok(statisticsService.getViews());
    }

    @Operation(summary = "get actions from DB(for tests)")
    @RequestMapping(
            path = "getactions",
            method = RequestMethod.GET,
            produces = {"application/json"})
    public ResponseEntity<Iterable<ActionEntity>> getActions() {
        return ResponseEntity.ok(statisticsService.getActions());
    }

    @Operation(summary = "Calculate number of views for given dates")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Views number calculated")})
    @GetMapping(value = "/views_number", produces = {"application/json"})
    public ResponseEntity<List<Integer>> getNumMmaByDates(
            @Parameter(description = "Date from", required = true)
            @RequestParam(value = "dateFrom")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateFrom,
            @Parameter(description = "Date to", required = true)
            @RequestParam(value = "dateTo")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateTo) {

        List<Integer> sales = statisticsService.getNumMmaByDates(dateFrom, dateTo);

        return ResponseEntity.ok(sales);
    }
}
