package com.allmagen.testtask;

import com.allmagen.testtask.model.ActionEntity;
import com.allmagen.testtask.model.ViewEntity;
import com.allmagen.testtask.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

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
            path = "getviews",
            method = RequestMethod.GET)
    public ResponseEntity<Iterable<ViewEntity>> getViews() {
        return ResponseEntity.ok(statisticsService.getViews());
    }

    @Operation(summary = "get actions from DB(for tests)")
    @RequestMapping(
            path = "getactions",
            method = RequestMethod.GET)
    public ResponseEntity<Iterable<ActionEntity>> getActions() {
        return ResponseEntity.ok(statisticsService.getActions());
    }
}
