package com.allmagen.testtask.service;

import com.allmagen.testtask.controller.StatisticsController;
import com.allmagen.testtask.model.ActionEntity;
import com.allmagen.testtask.model.ViewEntity;
import com.allmagen.testtask.model.metrics.MmDmaCTR;
import com.allmagen.testtask.model.metrics.MmDmaCTRByDates;
import com.allmagen.testtask.model.metrics.MmDmaCount;
import com.allmagen.testtask.model.metrics.SiteIdCount;
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
import java.util.stream.Stream;

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

    /**
     * Uploads views from a CSV file specified in the provided {@link MultipartFile}.
     *
     * @param file containing the CSV file data.
     * @return The number of views successfully uploaded from the file.
     * @throws CsvValidationException If there is an error during CSV validation.
     * @throws IOException            If an I/O error occurs while reading the CSV file.
     * @throws RuntimeException       If the length of any CSV line does not match the expected length of 10 elements.
     * @see #uploadViewsFromFile(InputStream, String)
     */
    @Transactional
    public int uploadViewsFromFile(MultipartFile file) throws CsvValidationException, IOException {
        return uploadViewsFromFile(file.getInputStream(), file.getOriginalFilename());
    }

    /**
     * Uploads views from a CSV file.
     *
     * @param inputStream The input stream of the CSV file.
     * @param fileName    The name of the CSV file being processed.
     * @return The number of views successfully uploaded from the file.
     * @throws CsvValidationException If there is an error during CSV validation.
     * @throws IOException            If an I/O error occurs while reading the CSV file.
     * @throws RuntimeException       If the length of any CSV line does not match the expected length of 10 elements.
     */
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
                    String error = "Error: The length of the CSV line should exactly match the expected length of 10 elements.";
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

    /**
     * Parses a CSV line and creates a ViewEntity object.
     *
     * @param csvLine The CSV line to parse.
     * @return A ViewEntity representing the parsed data.
     * @throws NumberFormatException If there is an error parsing numeric values from the CSV line.
     */
    private ViewEntity parseViewEntity(String[] csvLine) {
        ViewEntity viewEntity = new ViewEntity(csvLine[ViewColumn.UID.value]);
        viewEntity.setRegTime(LocalDateTime.parse(csvLine[ViewColumn.REG_TIME.value], formatter));
        viewEntity.setFcImpChk(Integer.parseInt(csvLine[ViewColumn.FC_IMP_CHK.value]));
        viewEntity.setFcTimeChk(Integer.parseInt(csvLine[ViewColumn.FC_TIME_CHK.value]));
        viewEntity.setUtmtr(Integer.parseInt(csvLine[ViewColumn.UTMR.value]));
        viewEntity.setMmDma(Integer.parseInt(csvLine[ViewColumn.MM_DMA.value]));
        viewEntity.setOsName(csvLine[ViewColumn.OS_NAME.value]);
        viewEntity.setModel(csvLine[ViewColumn.MODEL.value]);
        viewEntity.setHardware(csvLine[ViewColumn.HARDWARE.value]);
        viewEntity.setSiteId(csvLine[ViewColumn.SITE_ID.value]);
        return viewEntity;
    }

    /**
     * Uploads action data from a CSV file specified in the provided {@link MultipartFile}.
     *
     * @param file containing the CSV file data.
     * @return The number of actions successfully uploaded from the file.
     * @throws CsvValidationException If there is an error during CSV validation.
     * @throws IOException            If an I/O error occurs while reading the CSV file.
     * @throws RuntimeException       If the length of any CSV line does not match the expected length of 2 elements.
     */
    @Transactional
    public int uploadActionsFromFile(MultipartFile file) throws CsvValidationException, IOException {
        return uploadActionsFromFile(file.getInputStream(), file.getOriginalFilename());
    }

    /**
     * Uploads action data from a CSV file specified in the provided {@link InputStream} and file name.
     *
     * @param inputStream containing the CSV file data.
     * @param fileName    The name of the CSV file.
     * @return The number of actions successfully uploaded from the file.
     * @throws CsvValidationException If there is an error during CSV validation.
     * @throws IOException            If an I/O error occurs while reading the CSV file.
     * @throws RuntimeException       If the length of any CSV line does not match the expected length of 2 elements.
     */
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
                    String error = "Error: The length of the CSV line should exactly match the expected length of 2 elements";
                    throw new RuntimeException(error);
                }

                String uid = csvLine[ActionColumn.UID.value];
                Optional<ViewEntity> optionalViewEntity = viewRepository.findById(uid);
                if (optionalViewEntity.isEmpty()) {
                    LOGGER.log(Level.INFO, "Action with UID " + uid + " does not exist in the view table.");
                    continue;
                }

                ViewEntity viewEntity = optionalViewEntity.get();

                ActionEntity action = new ActionEntity(viewEntity, csvLine[ActionColumn.TAG.value]);
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
                    .toList();

            actionRepository.saveAll(actionEntities);

            int actionsNumber = actionEntities.size();
            LOGGER.log(Level.INFO, "Upload actions from file " + fileName + " finished: " + actionsNumber);

            return actionsNumber;
        }
    }


    @Transactional
    public Stream<MmDmaCTRByDates> getCTR(LocalDateTime startDate, LocalDateTime endDate, StatisticsController.Interval interval, String tag) {
        return viewRepository.getCTR(startDate, endDate, interval.getValue(), tag);
    }

    @Transactional
    public Stream<MmDmaCTRByDates> getEvPM(LocalDateTime startDate, LocalDateTime endDate, StatisticsController.Interval interval, String tag) {
        return viewRepository.getEvPM(startDate, endDate, interval.getValue(), tag);
    }

    @Transactional
    public Stream<MmDmaCount> getViewsCountByMmDma(LocalDate startDate, LocalDate endDate) {
        return viewRepository.getViewsCountByMmDma(startDate, endDate);
    }

    @Transactional
    public Stream<SiteIdCount> getViewsCountBySiteId(LocalDate startDate, LocalDate endDate) {
        return viewRepository.getViewsCountBySiteId(startDate, endDate);
    }

    @Transactional
    public Stream<MmDmaCTR> getCtrAggregateByMmDma(LocalDateTime startDate, LocalDateTime endDate, String tag) {
        return viewRepository.getCtrAggregateByMmDma(startDate, endDate, tag);
    }

    @Transactional
    public Stream<MmDmaCTR> getCtrAggregateBySiteId(LocalDateTime startDate, LocalDateTime endDate, String tag) {
        return viewRepository.getCtrAggregateBySiteId(startDate, endDate, tag);
    }

    /**
     * Enumeration representing the columns in the CSV file used for ViewEntity.
     */
    private enum ViewColumn {
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

        ViewColumn(int value) {
            this.value = value;
        }
    }

    /**
     * Enumeration representing the columns in the CSV file used for ActionEntity.
     */
    private enum ActionColumn {
        UID(0),
        TAG(1);

        private final int value;

        ActionColumn(int value) {
            this.value = value;
        }
    }
}