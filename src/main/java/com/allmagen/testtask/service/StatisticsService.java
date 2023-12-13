package com.allmagen.testtask.service;

import com.allmagen.testtask.dao.ActionRepository;
import com.allmagen.testtask.dao.ViewRepository;
import com.allmagen.testtask.model.ActionEntity;
import com.allmagen.testtask.model.ViewEntity;
import com.allmagen.testtask.model.dto.MmDmaCTR;
import com.allmagen.testtask.model.dto.SiteIdCTR;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    private final ViewRepository viewRepository;
    private final ActionRepository actionRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatisticsService(ViewRepository viewRepository, ActionRepository actionRepository) {
        this.viewRepository = viewRepository;
        this.actionRepository = actionRepository;
    }

    @Transactional
    public Iterable<ViewEntity> addViewFromFile(MultipartFile file) throws CsvValidationException, IOException {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream())); CSVReader csvReader = new CSVReaderBuilder(fileReader).build()) {
            // skip csv header
            csvReader.readNext();

            List<ViewEntity> viewEntityList = new ArrayList<>();

            while (csvReader.iterator().hasNext()) {
                String[] csvLine = csvReader.readNext();
                if (csvLine == null) {
                    break;
                }

                if (csvLine.length != 10) {
                    throw new RuntimeException("View csvLine should has length 10");
                }

                ViewEntity viewEntity = parseViewEntity(csvLine);
                viewEntityList.add(viewEntity);
            }

            return viewRepository.saveAll(viewEntityList);

        }
    }

    private ViewEntity parseViewEntity(String[] csvLine) {
        ViewEntity viewEntity = new ViewEntity();
        viewEntity.setRegTime(LocalDateTime.parse(csvLine[0], formatter));
        viewEntity.setUid(csvLine[1]);
        viewEntity.setFcImpChk(Integer.parseInt(csvLine[2]));
        viewEntity.setFcTimeChk(Integer.parseInt(csvLine[3]));
        viewEntity.setUtmtr(Integer.parseInt(csvLine[4]));
        viewEntity.setMmDma(Integer.parseInt(csvLine[5]));
        viewEntity.setOsName(csvLine[6]);
        viewEntity.setModel(csvLine[7]);
        viewEntity.setHardware(csvLine[8]);
        viewEntity.setSiteId(csvLine[9]);
        return viewEntity;
    }

    @Transactional
    public void addActionFromFile(MultipartFile file) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream())); CSVReader csvReader = new CSVReaderBuilder(fileReader).build()) {
            // skip csv header
            csvReader.readNext();

            Map<ActionEntity.UidTag, Integer> uidTagMap = new HashMap<>();

            while (true) {
                String[] csvLine = csvReader.readNext();
                if (csvLine == null) {
                    break;
                }

                if (csvLine.length != 2) {
                    throw new RuntimeException("Action csvLine should has length 2");
                }

                String uid = csvLine[0].trim();
                //TODO:
                Optional<ViewEntity> optionalViewEntity = viewRepository.findById(uid);
                if (optionalViewEntity.isEmpty()) {
                    System.out.println(uid + " not exist");
                    continue;
                }

                ViewEntity viewEntity = optionalViewEntity.get();

                ActionEntity.UidTag uidTag = new ActionEntity.UidTag(csvLine[1], viewEntity);
                if (uidTagMap.containsKey(uidTag)) {
                    uidTagMap.put(uidTag, uidTagMap.get(uidTag) + 1);
                } else {
                    uidTagMap.put(uidTag, 1);
                }
            }

            List<ActionEntity> actionEntities = uidTagMap.entrySet().stream()
                    .map(entry -> new ActionEntity(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());

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

    public List<Integer> getNumMmaByDates(LocalDate startDate, LocalDate endDate, int mmDma) {
        return viewRepository.getNumMmaByDates(startDate, endDate, mmDma);
    }

    public List<Integer> getNumSiteIdByDates(LocalDate startDate, LocalDate endDate, String siteId) {
        return viewRepository.getNumSiteIdByDates(startDate, endDate, siteId);
    }

    public List<MmDmaCTR> getMmDmaCTR() {
        return viewRepository.getMmDmaCTR();
    }

    public List<SiteIdCTR> getSiteIdCTR() {
        return viewRepository.getSiteIdCTR();
    }
}