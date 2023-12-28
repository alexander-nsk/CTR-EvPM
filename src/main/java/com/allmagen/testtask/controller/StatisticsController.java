package com.allmagen.testtask.controller;

import com.allmagen.testtask.model.metrics.*;
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
    public ResponseEntity<StreamResponse<CtrDates>> getCTR(@Parameter(description = "Date from (e.g. 2021-07-20T20:00:00)", required = true)
                                                           @RequestParam(value = "dateFrom")
                                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateFrom,
                                                           @Parameter(description = "Date to (e.g. 2021-07-22T20:00:00)", required = true)
                                                           @RequestParam(value = "dateTo")
                                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTo,
                                                           @Parameter(in = ParameterIn.QUERY, name = "interval", schema = @Schema(implementation = Interval.class), required = true) Interval interval,
                                                           @Parameter(description = "tag") @RequestParam(value = "tag", required = false) String tag) {

        Stream<CtrDates> resultStream = statisticsService.getCTR(dateFrom, dateTo, interval, tag);

        return ResponseEntity.ok(new StreamResponse<>(resultStream));
    }

    @GetMapping("/ctrChart")
    public String getCTRChart(@Parameter(description = "Date from (e.g. 2021-07-20T20:00:00)", required = true)
                              @RequestParam(value = "dateFrom")
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateFrom,
                              @Parameter(description = "Date to (e.g. 2021-07-22T20:00:00)", required = true)
                              @RequestParam(value = "dateTo")
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTo,
                              @Parameter(in = ParameterIn.QUERY, name = "interval", schema = @Schema(implementation = Interval.class), required = true) Interval interval,
                              @Parameter(description = "tag") @RequestParam(value = "tag", required = false) String tag,
                              Model model) {
        List<LocalDateTime> x = new ArrayList<>();
        List<Float> y = new ArrayList<>();

        Stream<CtrDates> resultStream = statisticsService.getCTR(dateFrom, dateTo, interval, tag);

        resultStream.forEach(mmDmaCTR -> {
            x.add(mmDmaCTR.getIntervalStart());
            y.add(mmDmaCTR.getCtr());
        });

        return fillModelAndDrawChart(model, "CTR for given tag: " + tag + "\n from " + dateFrom + " to " + dateTo, "CTR", y, x);
    }

    @Operation(summary = "Get EvPM within Date Range and Tag")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Array of EvPM pairs with a specific Date Range")})
    @GetMapping("/evpm")
    public ResponseEntity<StreamResponse<CtrDates>> getEvPM(@Parameter(description = "Date from (e.g. 2021-07-20T20:00:00)", required = true)
                                                            @RequestParam(value = "dateFrom")
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateFrom,
                                                            @Parameter(description = "Date to (e.g. 2021-07-23T20:00:00)", required = true)
                                                            @RequestParam(value = "dateTo")
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTo,
                                                            @Parameter(in = ParameterIn.QUERY, name = "interval", schema = @Schema(implementation = Interval.class), required = true) Interval interval,
                                                            @Parameter(description = "tag") @RequestParam(value = "tag", required = false) String tag) {

        Stream<CtrDates> resultStream = statisticsService.getEvPM(dateFrom, dateTo, interval, tag);

        return ResponseEntity.ok(new StreamResponse<>(resultStream));
    }

    @GetMapping("/evpmChart")
    public String getEvPMChart(@Parameter(description = "Date from (e.g. 2021-07-20T20:00:00)", required = true)
                               @RequestParam(value = "dateFrom")
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateFrom,
                               @Parameter(description = "Date to (e.g. 2021-07-22T20:00:00)", required = true)
                               @RequestParam(value = "dateTo")
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTo,
                               @Parameter(in = ParameterIn.QUERY, name = "interval", schema = @Schema(implementation = Interval.class), required = true) Interval interval,
                               @Parameter(description = "tag") @RequestParam(value = "tag", required = false) String tag,
                               Model model) {
        List<LocalDateTime> x = new ArrayList<>();
        List<Float> y = new ArrayList<>();

        Stream<CtrDates> resultStream = statisticsService.getEvPM(dateFrom, dateTo, interval, tag);

        resultStream.forEach(mmDmaCTR -> {
            x.add(mmDmaCTR.getIntervalStart());
            y.add(mmDmaCTR.getCtr());
        });

        return fillModelAndDrawChart(model, "EvPM for given tag: " + tag + ", from " + dateFrom + " to " + dateTo, "EvPM", y, x);
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
            @Parameter(description = "Date from (e.g. 2021-07-20)", required = true)
            @RequestParam(value = "dateFrom")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateFrom,
            @Parameter(description = "Date to (e.g. 2021-07-22)", required = true)
            @RequestParam(value = "dateTo")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateTo) {

        Stream<SiteIdCount> viewsCounts = statisticsService.getViewsCountBySiteId(dateFrom, dateTo);

        return ResponseEntity.ok(new StreamResponse<>(viewsCounts));
    }

    @Operation(summary = "Get CtrAggregateByMmDma within Date Range and Tag")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Array of Ctr Aggregate By MmDma pairs with a specific Date Range")})
    @GetMapping("/ctrByMmDma")
    public ResponseEntity<StreamResponse<MmDmaCTR>> getCtrAggregateByMmDma(@Parameter(description = "Date from (e.g. 2021-07-20T20:00:00)", required = true)
                                                                           @RequestParam(value = "dateFrom")
                                                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateFrom,
                                                                           @Parameter(description = "Date to (e.g. 2021-07-23T20:00:00)", required = true)
                                                                           @RequestParam(value = "dateTo")
                                                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTo,
                                                                           @Parameter(description = "tag") @RequestParam(value = "tag", required = false) String tag) {

        Stream<MmDmaCTR> resultStream = statisticsService.getCtrAggregateByMmDma(dateFrom, dateTo, tag);
        return ResponseEntity.ok(new StreamResponse<>(resultStream));
    }

    @GetMapping("/ctrByMmDmaChart")
    public String getCtrAggregateByMmDmaChart(@Parameter(description = "Date from (e.g. 2021-07-20T20:00:00)", required = true)
                                              @RequestParam(value = "dateFrom")
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateFrom,
                                              @Parameter(description = "Date to (e.g. 2021-07-23T20:00:00)", required = true)
                                              @RequestParam(value = "dateTo")
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTo,
                                              @Parameter(description = "tag") @RequestParam(value = "tag", required = false) String tag,
                                              Model model) {
        List<Integer> x = new ArrayList<>();
        List<Float> y = new ArrayList<>();

        Stream<MmDmaCTR> resultStream = statisticsService.getCtrAggregateByMmDma(dateFrom, dateTo, tag);

        resultStream.forEach(mmDmaCTR -> {
            x.add(mmDmaCTR.getMmDma());
            y.add(mmDmaCTR.getCtr());
        });

        return fillModelAndDrawChart(model, "CTR aggregated by MmDma for given tag: " + tag + ", from " + dateFrom + " to " + dateTo, "CTR", y, x);
    }

    @Operation(summary = "Get Ctr Aggregate By SiteId within Date Range and Tag")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Array of Ctr Aggregate By SiteId pairs with a specific Date Range")})
    @GetMapping("/ctrBySiteId")
    public ResponseEntity<StreamResponse<SiteIdCTR>> getCtrAggregateBySiteId(@Parameter(description = "Date from (e.g. 2021-07-20T20:00:00)", required = true)
                                                                             @RequestParam(value = "dateFrom")
                                                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateFrom,
                                                                             @Parameter(description = "Date to (e.g. 2021-07-23T20:00:00)", required = true)
                                                                             @RequestParam(value = "dateTo")
                                                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTo,
                                                                             @Parameter(description = "tag") @RequestParam(value = "tag", required = false) String tag) {

        Stream<SiteIdCTR> resultStream = statisticsService.getCtrAggregateBySiteId(dateFrom, dateTo, tag);
        return ResponseEntity.ok(new StreamResponse<>(resultStream));
    }

    @GetMapping("/ctrBySiteIdChart")
    public String getCtrAggregateBySiteIdChart(@Parameter(description = "Date from (e.g. 2021-07-20T20:00:00)", required = true)
                                               @RequestParam(value = "dateFrom")
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateFrom,
                                               @Parameter(description = "Date to (e.g. 2021-07-22T20:00:00)", required = true)
                                               @RequestParam(value = "dateTo")
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTo,
                                               @Parameter(description = "tag") @RequestParam(value = "tag", required = false) String tag,
                                               Model model) {
        List<String> x = new ArrayList<>();
        List<Float> y = new ArrayList<>();

        Stream<SiteIdCTR> resultStream = statisticsService.getCtrAggregateBySiteId(dateFrom, dateTo, tag);

        resultStream.forEach(mmDmaCTR -> {
            x.add(mmDmaCTR.getSiteId());
            y.add(mmDmaCTR.getCtr());
        });

        return fillModelAndDrawChart(model, "CTR aggregate by SiteId for given tag: " + tag + ", from " + dateFrom + " to " + dateTo, "CTR", y, x);
    }

    /**
     * Fills the provided Spring MVC Model with data and draws a bar chart.
     *
     * @param model     the Spring MVC model to be filled
     * @param yAxisData the data for the Y-axis of the chart
     * @param xAxisData the data for the X-axis of the chart
     * @return the name of the view to be rendered, in this case, "barChart"
     */
    private String fillModelAndDrawChart(Model model, String graphTitle, String yAxisTitle, Object yAxisData, Object xAxisData) {
        if (model == null || graphTitle == null || yAxisTitle == null || yAxisData == null || xAxisData == null) {
            throw new IllegalArgumentException("One or more input parameters are null.");
        }

        /*String graphTitle = "CTR Aggregate By SiteId Graph from " + dateFrom + " to " + dateTo;
        String yAxisTitle = "CTR Aggregate By SiteId";*/

        model.addAttribute("graphTitle", graphTitle);
        model.addAttribute("yAxisTitle", yAxisTitle);
        model.addAttribute("xAxisData", xAxisData);
        model.addAttribute("yAxisData", yAxisData);

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