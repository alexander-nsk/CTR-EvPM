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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger LOGGER = LogManager.getLogger(StatisticsService.class);

    private final ViewRepository viewRepository;
    private final ActionRepository actionRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatisticsService(ViewRepository viewRepository, ActionRepository actionRepository) {
        this.viewRepository = viewRepository;
        this.actionRepository = actionRepository;
    }

    @Transactional
    public Iterable<ViewEntity> uploadViewsFromFile(MultipartFile file) throws CsvValidationException, IOException {
        LOGGER.log(Level.INFO, "Upload views from file " + file.getOriginalFilename() + " started");

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream())); CSVReader csvReader = new CSVReaderBuilder(fileReader).build()) {
            // skip csv header
            csvReader.readNext();

            List<ViewEntity> viewEntityList = new ArrayList<>();

            while (csvReader.iterator().hasNext()) {
                String[] csvLine = csvReader.readNext();

                if (csvLine.length != 10) {
                    String error = "View csvLine should has length 10";
                    LOGGER.log(Level.ERROR, error);
                    throw new RuntimeException(error);
                }

                ViewEntity viewEntity = parseViewEntity(csvLine);
                viewEntityList.add(viewEntity);
            }

            LOGGER.log(Level.INFO, "Upload views from file " + file.getOriginalFilename() + " finished");

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
    public void uploadActionsFromFile(MultipartFile file) {
        LOGGER.log(Level.INFO, "Upload actions from file " + file.getOriginalFilename() + " started");

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
                    String error = "Action csvLine should has length 2";
                    LOGGER.log(Level.ERROR, error);
                    throw new RuntimeException(error);
                }

                String uid = csvLine[0].trim();
                //TODO:
                Optional<ViewEntity> optionalViewEntity = viewRepository.findById(uid);
                if (optionalViewEntity.isEmpty()) {
                    LOGGER.log(Level.INFO, uid + " not exist");
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
            LOGGER.log(Level.INFO, "Upload actions from file " + file.getOriginalFilename() + " finished");

        } catch (IOException | CsvException e) {
            LOGGER.log(Level.ERROR, "Upload actions from file " + file.getOriginalFilename() + " failed");
            LOGGER.log(Level.ERROR, e);
            throw new RuntimeException(e);
        }
    }

    public Iterable<ViewEntity> getAllViews() {
        return viewRepository.findAll();
    }

    public Iterable<ActionEntity> getAllActions() {
        return actionRepository.findAll();
    }

    public List<Integer> getNumMmaByDates(LocalDate startDate, LocalDate endDate, int mmDma) {
        return viewRepository.getNumMmaByDates(startDate, endDate, mmDma);
    }

    public List<Integer> getNumSiteIdByDates(LocalDate startDate, LocalDate endDate, String siteId) {
        return viewRepository.getNumSiteIdByDates(startDate, endDate, siteId);
    }

    public List<MmDmaCTR> getMmDmaCTR(String tag) {
        return Optional.ofNullable(tag)
                .map(viewRepository::getMmDmaCTR)
                .orElseGet(viewRepository::getMmDmaCTR);
    }

    public List<SiteIdCTR> getSiteIdCTR(String tag) {
        return Optional.ofNullable(tag)
                .map(viewRepository::getSiteIdCTR)
                .orElseGet(viewRepository::getSiteIdCTR);
    }
}