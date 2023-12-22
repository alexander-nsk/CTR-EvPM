package com.allmagen.testtask.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;


@RestController
public class HighChartController {
    @GetMapping("/chart")
    public String barChart(Model model) {
        Map<String, Integer> data = new LinkedHashMap<>();
        data.put("Ashish", 30);
        data.put("Ankit", 50);
        data.put("Gurpreet", 70);
        data.put("Mohit", 90);
        data.put("Manish", 25);

        model.addAttribute("keySet", data.keySet());
        model.addAttribute("values", data.values());

        return "barchart";
    }
}
