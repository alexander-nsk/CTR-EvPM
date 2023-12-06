package com.allmagen.testtask.service;

import com.allmagen.testtask.dao.ActionRepository;
import com.allmagen.testtask.dao.ViewRepository;
import com.allmagen.testtask.model.ActionEntity;
import com.allmagen.testtask.model.ViewEntity;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@EnableAsync
public class StatisticsService {
    @Autowired
    private ViewRepository viewRepository;

    @Autowired
    private ActionRepository actionRepository;

    public void addViewFromFile(MultipartFile file) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream())); CSVReader csvReader = new CSVReaderBuilder(fileReader).build()) {
            // skip csv header
            csvReader.readNext();

            List<ViewEntity> viewEntityList = new ArrayList<>();

            while (true) {
                String[] csvLine = csvReader.readNext();
                if (csvLine == null) {
                    break;
                }

                if (csvLine.length != 10) {
                    throw new RuntimeException("View csvLine should has length 10");
                }

                ViewEntity viewEntity = new ViewEntity();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime localDateTime = LocalDateTime.parse(csvLine[0], formatter);

                viewEntity.setUid(csvLine[1]);
                viewEntity.setRegTime(localDateTime);
                viewEntity.setFcImpChk(Integer.parseInt(csvLine[2]));
                viewEntity.setFcTimeChk(Integer.parseInt(csvLine[3]));
                viewEntity.setUtmtr(Integer.parseInt(csvLine[4]));
                viewEntity.setMmDma(Integer.parseInt(csvLine[5]));
                viewEntity.setOsName(csvLine[6]);
                viewEntity.setModel(csvLine[7]);
                viewEntity.setHardware(csvLine[8]);
                viewEntity.setSiteId(csvLine[9]);

                viewEntityList.add(viewEntity);
            }

            viewRepository.saveAll(viewEntityList);

        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    public void addActionFromFile(MultipartFile file) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream())); CSVReader csvReader = new CSVReaderBuilder(fileReader).build()) {
            // skip csv header
            csvReader.readNext();

            List<ActionEntity> actionEntities = new ArrayList<>();

            while (true) {
                String[] csvLine = csvReader.readNext();
                if (csvLine == null) {
                    break;
                }

                if (csvLine.length != 2) {
                    throw new RuntimeException("Action csvLine should has length 2");
                }

                ActionEntity actionEntity = new ActionEntity();
                actionEntity.setUid(csvLine[0]);
                actionEntity.setTag(csvLine[1]);

                actionEntities.add(actionEntity);
            }

            actionRepository.saveAll(actionEntities);

        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterable<ViewEntity> getViews() {
        return viewRepository.findAll();
    }

    public Iterable<ActionEntity> getActions() {
        return actionRepository.findAll();
    }
}