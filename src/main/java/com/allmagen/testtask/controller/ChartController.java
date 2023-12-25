package com.allmagen.testtask.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ChartController {
    @GetMapping("/chart")
    public String showChart() {
        return "chart";
    }
}
