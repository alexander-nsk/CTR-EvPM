package com.allmagen.testtask;

import com.allmagen.testtask.controller.StatisticsController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StatisticsIntegrationTests {
    private final static String INTERVIEW_X = "classpath:testdata/interview.x.small.csv";
    private final static String INTERVIEW_Y = "classpath:testdata/interview.y.csv";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final LocalDateTime dateFrom = LocalDateTime.parse("2021-07-20T20:00:00", formatter);
    private final LocalDateTime dateTo = LocalDateTime.parse("2021-07-20T23:00:00", formatter);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testUploadViewsFromFile() throws Exception {
        byte[] fileContent = resourceLoader.getResource(INTERVIEW_X).getInputStream().readAllBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-views.csv",
                "text/csv",
                fileContent);

        mockMvc.perform(multipart("/views")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    void testUploadViewsFromFileCsvException() throws Exception {
        byte[] fileContent = resourceLoader.getResource(INTERVIEW_Y).getInputStream().readAllBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-views.csv",
                "text/csv",
                fileContent);

        mockMvc.perform(multipart("/views")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    Throwable exception = result.getResolvedException();
                    assertThat(exception).isInstanceOf(RuntimeException.class);
                    assert exception != null;
                    assertThat(exception.getMessage()).isEqualTo("Error: The length of the CSV line should exactly match the expected length of 10 elements.");
                });
    }

    @Test
    void testUploadActionsFromFile() throws Exception {
        byte[] fileContent = resourceLoader.getResource(INTERVIEW_Y).getInputStream().readAllBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-views.csv",
                "text/csv",
                fileContent);

        mockMvc.perform(multipart("/actions")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    void testUploadActionsFromFileCsvException() throws Exception {
        byte[] fileContent = resourceLoader.getResource(INTERVIEW_X).getInputStream().readAllBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-views.csv",
                "text/csv",
                fileContent);

        mockMvc.perform(multipart("/actions")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    Throwable exception = result.getResolvedException();
                    assertThat(exception).isInstanceOf(RuntimeException.class);
                    assert exception != null;
                    assertThat(exception.getMessage()).isEqualTo("Error: The length of the CSV line should exactly match the expected length of 2 elements");
                });
    }

    @Test
    void testGetCTR() throws Exception {
        LocalDateTime dateFrom = LocalDateTime.parse("2021-07-20T20:00:00", formatter);
        LocalDateTime dateTo = LocalDateTime.parse("2021-07-20T23:00:00", formatter);

        String responseJson = mockMvc.perform(get("/ctr")
                        .param("dateFrom", dateFrom.toString())
                        .param("dateTo", dateTo.toString())
                        .param("interval", StatisticsController.Interval.HOUR.getValue().toUpperCase())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        JsonNode responseNode = objectMapper.readTree(responseJson).get("items");

        assertThat(responseNode.isArray()).isTrue();
    }

    @Test
    void testGetCTRWithTag() throws Exception {
        String responseJson = mockMvc.perform(get("/ctr")
                        .param("dateFrom", dateFrom.toString())
                        .param("dateTo", dateTo.toString())
                        .param("interval", StatisticsController.Interval.HOUR.getValue().toUpperCase())
                        .param("tag", "registration")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        JsonNode responseNode = objectMapper.readTree(responseJson).get("items");

        assertThat(responseNode.isArray()).isTrue();
    }

    @Test
    void testEvPm() throws Exception {
        String responseJson = mockMvc.perform(get("/evpm")
                        .param("dateFrom", dateFrom.toString())
                        .param("dateTo", dateTo.toString())
                        .param("interval", StatisticsController.Interval.HOUR.getValue().toUpperCase())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        JsonNode responseNode = objectMapper.readTree(responseJson).get("items");

        assertThat(responseNode.isArray()).isTrue();
    }

    @Test
    void testViewsCountByMmDma() throws Exception {
        String responseJson = mockMvc.perform(get("/viewsCountByMmDma")
                        .param("dateFrom", dateFrom.toLocalDate().toString())
                        .param("dateTo", dateTo.toLocalDate().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        JsonNode responseNode = objectMapper.readTree(responseJson).get("items");

        assertThat(responseNode.isArray()).isTrue();
    }

    @Test
    void testViewsCountBySiteId() throws Exception {
        String responseJson = mockMvc.perform(get("/viewsCountBySiteId")
                        .param("dateFrom", dateFrom.toLocalDate().toString())
                        .param("dateTo", dateTo.toLocalDate().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        JsonNode responseNode = objectMapper.readTree(responseJson).get("items");

        assertThat(responseNode.isArray()).isTrue();
    }

    @Test
    void testCtrByMmDma() throws Exception {
        String responseJson = mockMvc.perform(get("/ctrByMmDma")
                        .param("dateFrom", dateFrom.toString())
                        .param("dateTo", dateTo.toString())
                        .param("tag", "registration")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        JsonNode responseNode = objectMapper.readTree(responseJson).get("items");

        assertThat(responseNode.isArray()).isTrue();
    }

    @Test
    void testCtrBySiteId() throws Exception {
        String responseJson = mockMvc.perform(get("/ctrBySiteId")
                        .param("dateFrom", dateFrom.toString())
                        .param("dateTo", dateTo.toString())
                        .param("tag", "registration")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        JsonNode responseNode = objectMapper.readTree(responseJson).get("items");

        assertThat(responseNode.isArray()).isTrue();
    }

    @Test
    void tetCtrAggregateBySiteIdChart() throws Exception {
        mockMvc.perform(get("/ctrBySiteIdChart")
                        .param("dateFrom", dateFrom.toString())
                        .param("dateTo", dateTo.toString())
                        .param("tag", "registration")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
