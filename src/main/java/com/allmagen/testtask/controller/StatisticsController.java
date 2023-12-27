package com.allmagen.testtask.controller;

import com.allmagen.testtask.model.metrics.MmDmaCTR;
import com.allmagen.testtask.model.metrics.MmDmaCTRByDates;
import com.allmagen.testtask.model.metrics.MmDmaCount;
import com.allmagen.testtask.model.metrics.SiteIdCount;
import com.allmagen.testtask.service.StatisticsService;
import com.opencsv.exceptions.CsvValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Controller
@RequestMapping("/")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Operation(summary = "Upload view data from CSV")
    @PostMapping(
            path = "views",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "text/plain")
    public ResponseEntity<String> uploadViewsFromFile(@RequestPart(value = "file") MultipartFile multipartFile) throws CsvValidationException, IOException {
        int viewsNumber = statisticsService.uploadViewsFromFile(multipartFile);
        return ResponseEntity.ok("Views uploaded: " + viewsNumber);
    }

    @Operation(summary = "Upload action data from CSV")
    @PostMapping(
            path = "actions",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "text/plain")
    public ResponseEntity<String> uploadActionsFromFile(@RequestPart(value = "file") MultipartFile multipartFile) throws CsvValidationException, IOException {
        int actionsNumber = statisticsService.uploadActionsFromFile(multipartFile);
        return ResponseEntity.ok("Actions uploaded: " + actionsNumber);
    }

    @Operation(summary = "Get CTR within Date Range and Tag")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Array of CTR pairs with a specific Date Range")})
    @GetMapping("/ctr")
    public String getCTR(@Parameter(description = "Date from", required = true)
                         @RequestParam(value = "dateFrom")
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateFrom,
                         @Parameter(description = "Date to", required = true)
                         @RequestParam(value = "dateTo")
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTo,
                         @Parameter(in = ParameterIn.QUERY, name = "interval", schema = @Schema(implementation = Interval.class), required = true) Interval interval,
                         @Parameter(description = "tag") @RequestParam(value = "tag", required = false) String tag,
                         Model model) {
        List<LocalDateTime> intervalStarts = new ArrayList<>();
        List<Float> ctrValues = new ArrayList<>();

        Stream<MmDmaCTRByDates> resultStream = statisticsService.getCTR(dateFrom, dateTo, interval, tag);

        resultStream.forEach(mmDmaCTR -> {
            intervalStarts.add(mmDmaCTR.getIntervalStart());
            ctrValues.add(mmDmaCTR.getCtr());
        });

        model.addAttribute("graphTitle", "CTR Graph from " + dateFrom + " to " + dateTo);
        model.addAttribute("yAxisTitle", "CTR");
        model.addAttribute("xAxisData", intervalStarts);
        model.addAttribute("yAxisData", ctrValues);
        return "barChart";
    }

    @Operation(summary = "Get EvPM within Date Range and Tag")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Array of EvPM pairs with a specific Date Range")})
    @GetMapping("/evpm")
    public String getEvPM(@Parameter(description = "Date from", required = true)
                          @RequestParam(value = "dateFrom")
                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateFrom,
                          @Parameter(description = "Date to", required = true)
                          @RequestParam(value = "dateTo")
                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTo,
                          @Parameter(in = ParameterIn.QUERY, name = "interval", schema = @Schema(implementation = Interval.class), required = true) Interval interval,
                          @Parameter(description = "tag") @RequestParam(value = "tag", required = false) String tag,
                          Model model) {
        List<LocalDateTime> intervalStarts = new ArrayList<>();
        List<Float> ctrValues = new ArrayList<>();

        Stream<MmDmaCTRByDates> resultStream = statisticsService.getEvPM(dateFrom, dateTo, interval, tag);

        resultStream.forEach(mmDmaCTR -> {
            intervalStarts.add(mmDmaCTR.getIntervalStart());
            ctrValues.add(mmDmaCTR.getCtr());
        });

        model.addAttribute("graphTitle", "EvPM Graph from " + dateFrom + " to " + dateTo);
        model.addAttribute("yAxisTitle", "EvPM");
        model.addAttribute("xAxisData", intervalStarts);
        model.addAttribute("yAxisData", ctrValues);
        return "barChart";
    }

    @Operation(summary = "Aggregate number of views by mmDma for given dates")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Number of views aggregated by mmDma")})
    @GetMapping(value = "/viewsCountByMmDma", produces = {"application/json"})
    public ResponseEntity<StreamResponse<MmDmaCount>> getViewsCountByMmDma(
            @Parameter(description = "Date from", required = true)
            @RequestParam(value = "dateFrom")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateFrom,
            @Parameter(description = "Date to", required = true)
            @RequestParam(value = "dateTo")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateTo) {

        Stream<MmDmaCount> viewsCounts = statisticsService.getViewsCountByMmDma(dateFrom, dateTo);

        return ResponseEntity.ok(new StreamResponse<>(viewsCounts));
    }

    @Operation(summary = "Aggregate number of views by siteId for given dates")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Number of views aggregated by siteId")})
    @GetMapping(value = "/viewsCountBySiteId", produces = {"application/json"})
    public ResponseEntity<StreamResponse<SiteIdCount>> getViewsCountBySiteId(
            @Parameter(description = "Date from", required = true)
            @RequestParam(value = "dateFrom")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateFrom,
            @Parameter(description = "Date to", required = true)
            @RequestParam(value = "dateTo")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateTo) {

        Stream<SiteIdCount> viewsCounts = statisticsService.getViewsCountBySiteId(dateFrom, dateTo);

        return ResponseEntity.ok(new StreamResponse<>(viewsCounts));
    }

    @Operation(summary = "Get CtrAggregateByMmDma within Date Range and Tag")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Array of Ctr Aggregate By MmDma pairs with a specific Date Range")})
    @GetMapping("/ctrByMmDma")
    public String getCtrAggregateByMmDma(@Parameter(description = "Date from", required = true)
                                         @RequestParam(value = "dateFrom")
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateFrom,
                                         @Parameter(description = "Date to", required = true)
                                         @RequestParam(value = "dateTo")
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTo,
                                         @Parameter(description = "tag") @RequestParam(value = "tag", required = false) String tag,
                                         Model model) {
        List<Integer> mmDmas = new ArrayList<>();
        List<Float> ctrValues = new ArrayList<>();

        Stream<MmDmaCTR> resultStream = statisticsService.getCtrAggregateByMmDma(dateFrom, dateTo, tag);

        resultStream.forEach(mmDmaCTR -> {
            mmDmas.add(mmDmaCTR.getMmDma());
            ctrValues.add(mmDmaCTR.getCtr());
        });

        model.addAttribute("graphTitle", "Ctr Aggregate By MmDma Graph from " + dateFrom + " to " + dateTo);
        model.addAttribute("yAxisTitle", "CtrAggregateByMmDma");
        model.addAttribute("xAxisData", mmDmas);
        model.addAttribute("yAxisData", ctrValues);
        return "barChart";
    }

    @Operation(summary = "Get Ctr Aggregate By SiteId within Date Range and Tag")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Array of Ctr Aggregate By SiteId pairs with a specific Date Range")})
    @GetMapping("/ctrBySiteId")
    public String getCtrAggregateBySiteId(@Parameter(description = "Date from", required = true)
                                          @RequestParam(value = "dateFrom")
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateFrom,
                                          @Parameter(description = "Date to", required = true)
                                          @RequestParam(value = "dateTo")
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTo,
                                          @Parameter(description = "tag") @RequestParam(value = "tag", required = false) String tag,
                                          Model model) {
        List<Integer> x = new ArrayList<>();
        List<Float> y = new ArrayList<>();

        Stream<MmDmaCTR> resultStream = statisticsService.getCtrAggregateBySiteId(dateFrom, dateTo, tag);

        resultStream.forEach(mmDmaCTR -> {
            x.add(mmDmaCTR.getMmDma());
            y.add(mmDmaCTR.getCtr());
        });

        model.addAttribute("graphTitle", "Ctr Aggregate By SiteId Graph from " + dateFrom + " to " + dateTo);
        model.addAttribute("yAxisTitle", "CtrAggregateBySiteId");
        model.addAttribute("xAxisData", x);
        model.addAttribute("yAxisData", y);
        return "barChart";
    }

    public record StreamResponse<T>(Stream<T> items) {

    }

    public enum Interval {
        DAY("day"),
        MINUTE("minute"),
        HOUR("hour");

        private final String value;

        Interval(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}