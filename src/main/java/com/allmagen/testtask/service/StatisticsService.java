package com.allmagen.testtask.service;

import com.allmagen.testtask.model.ActionEntity;
import com.allmagen.testtask.model.ViewEntity;
import com.allmagen.testtask.model.metrics.MmDmaCTR;
import com.allmagen.testtask.model.metrics.SiteIdCTR;
import com.allmagen.testtask.repository.ActionRepository;
import com.allmagen.testtask.repository.ViewRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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

    private final static String INTERVIEW_X = "testdata/interview.x.small.csv";
    private final static String INTERVIEW_Y = "testdata/interview.y.csv";

    public StatisticsService(ViewRepository viewRepository, ActionRepository actionRepository) {
        this.viewRepository = viewRepository;
        this.actionRepository = actionRepository;
    }

    @Transactional
    public int uploadViewsFromFile(MultipartFile file) throws CsvValidationException, IOException {
        return uploadViewsFromFile(file.getInputStream(), file.getOriginalFilename());
    }

    @Transactional
    private int uploadViewsFromFile(InputStream inputStream, String fileName) throws CsvValidationException, IOException {
        LOGGER.log(Level.INFO, "Upload views from file " + fileName + " started");

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream)); CSVReader csvReader = new CSVReaderBuilder(fileReader).build()) {
            // skip csv header
            csvReader.readNext();

            List<ViewEntity> viewEntityList = new ArrayList<>();

            while (true) {
                String[] csvLine = csvReader.readNext();
                if (csvLine == null) {
                    break;
                }

                if (csvLine.length != 10) {
                    String error = "View csvLine should has length 10";
                    throw new RuntimeException(error);
                }

                ViewEntity viewEntity = parseViewEntity(csvLine);
                viewEntityList.add(viewEntity);
            }

            viewRepository.saveAll(viewEntityList);

            int resultSize = viewEntityList.size();

            LOGGER.log(Level.INFO, "Upload views from file " + fileName + " finished: " + resultSize);

            return resultSize;
        }
    }

    private ViewEntity parseViewEntity(String[] csvLine) {
        ViewEntity viewEntity = new ViewEntity();
        viewEntity.setRegTime(LocalDateTime.parse(csvLine[XColumns.REG_TIME.value], formatter));
        viewEntity.setUid(csvLine[XColumns.UID.value]);
        viewEntity.setFcImpChk(Integer.parseInt(csvLine[XColumns.FC_IMP_CHK.value]));
        viewEntity.setFcTimeChk(Integer.parseInt(csvLine[XColumns.FC_TIME_CHK.value]));
        viewEntity.setUtmtr(Integer.parseInt(csvLine[XColumns.UTMR.value]));
        viewEntity.setMmDma(Integer.parseInt(csvLine[XColumns.MM_DMA.value]));
        viewEntity.setOsName(csvLine[XColumns.OS_NAME.value]);
        viewEntity.setModel(csvLine[XColumns.MODEL.value]);
        viewEntity.setHardware(csvLine[XColumns.HARDWARE.value]);
        viewEntity.setSiteId(csvLine[XColumns.SITE_ID.value]);
        return viewEntity;
    }

    @Transactional
    public int uploadActionsFromFile(MultipartFile file) throws CsvValidationException, IOException {
        return uploadActionsFromFile(file.getInputStream(), file.getOriginalFilename());
    }

    @Transactional
    private int uploadActionsFromFile(InputStream inputStream, String fileName) throws CsvValidationException, IOException {
        LOGGER.log(Level.INFO, "Upload actions from file " + fileName + " started");

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream)); CSVReader csvReader = new CSVReaderBuilder(fileReader).build()) {
            // skip csv header
            csvReader.readNext();

            Map<ActionEntity, Integer> actionsMap = new HashMap<>();

            while (true) {
                String[] csvLine = csvReader.readNext();
                if (csvLine == null) {
                    break;
                }

                if (csvLine.length != 2) {
                    String error = "Action csvLine should has length 2";
                    throw new RuntimeException(error);
                }

                String uid = csvLine[YColumns.UID.value];
                Optional<ViewEntity> optionalViewEntity = viewRepository.findById(uid);
                if (optionalViewEntity.isEmpty()) {
                    LOGGER.log(Level.INFO, "action " + uid + " not exist in view_table");
                    continue;
                }

                ViewEntity viewEntity = optionalViewEntity.get();

                ActionEntity action = new ActionEntity(viewEntity, csvLine[YColumns.TAG.value]);
                if (actionsMap.containsKey(action)) {
                    actionsMap.put(action, actionsMap.get(action) + 1);
                } else {
                    actionsMap.put(action, 1);
                }
            }

            List<ActionEntity> actionEntities = actionsMap.entrySet()
                    .stream()
                    .map(entry -> {
                        ActionEntity action = entry.getKey();
                        action.setCount(entry.getValue());
                        return action;
                    })
                    .collect(Collectors.toList());

            actionRepository.saveAll(actionEntities);

            int actionsNumber = actionEntities.size();
            LOGGER.log(Level.INFO, "Upload actions from file " + fileName + " finished: " + actionsNumber);

            return actionsNumber;
        }
    }

    public String loadTestDataToDataBase() throws CsvValidationException, IOException {
        int viewsNumber = uploadViewsFromFile(getClass().getClassLoader().getResourceAsStream(INTERVIEW_X), INTERVIEW_X);
        int actionsNumber = uploadActionsFromFile(getClass().getClassLoader().getResourceAsStream(INTERVIEW_Y), INTERVIEW_Y);

        return "Views uploaded:" + viewsNumber + ", Actions uploaded:" + actionsNumber;
    }

    public List<Integer> getNumMmaByDates(LocalDate startDate, LocalDate endDate, int mmDma) {
        return viewRepository.getNumMmaByDates(startDate, endDate, mmDma);
    }

    public List<Integer> getNumSiteIdByDates(LocalDate startDate, LocalDate endDate, String siteId) {
        return viewRepository.getNumSiteIdByDates(startDate, endDate, siteId);
    }

    public List<MmDmaCTR> getMmDmaCTR(String tag) {
        return viewRepository.getMmDmaCTR(tag);
    }

    public List<MmDmaCTR> getMmDmaCTR() {
        return viewRepository.getMmDmaCTR();
    }

    public List<SiteIdCTR> getSiteIdCTR(String tag) {
        return viewRepository.getSiteIdCTR(tag);
    }

    public List<SiteIdCTR> getSiteIdCTR() {
        return viewRepository.getSiteIdCTR();
    }

    public void clearDatabaseTables() {
        actionRepository.deleteAll();
        viewRepository.deleteAll();
    }

    private enum XColumns {
        REG_TIME(0),
        UID(1),
        FC_IMP_CHK(2),
        FC_TIME_CHK(3),
        UTMR(4),
        MM_DMA(5),
        OS_NAME(6),
        MODEL(7),
        HARDWARE(8),
        SITE_ID(9);

        private final int value;

        XColumns(int value) {
            this.value = value;
        }
    }

    private enum YColumns {
        UID(0),
        TAG(1);

        private final int value;

        YColumns(int value) {
            this.value = value;
        }
    }
}