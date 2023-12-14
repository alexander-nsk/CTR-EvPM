package com.allmagen.testtask;

import com.allmagen.testtask.controller.StatisticsController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)

public class StatisticsIntegrationTests {
    private final static String INTERVIEW_X = "classpath:testdata/interview.x.small.csv";
    private final static String INTERVIEW_Y = "classpath:testdata/interview.y.csv";

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private StatisticsController statisticsController;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testUploadViewsFromFile() throws Exception {
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
    public void testUploadViewsFromFileCsvException() throws Exception {
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
    public void testUploadActionsFromFile() throws Exception {
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
    public void testUploadActionsFromFileCsvException() throws Exception {
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
    public void testGetMmDmaCTR() throws Exception {
        String responseJson = mockMvc.perform(MockMvcRequestBuilders.get("/views/ctrByMmDma")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        JsonNode responseNode = objectMapper.readTree(responseJson).get("items");

        assertThat(responseNode.isArray()).isTrue();
    }

    @Test
    public void testGetMmDmaCTRByTag() throws Exception {
        String responseJson = mockMvc.perform(MockMvcRequestBuilders.get("/views/ctrByMmDmaByTag?tag=registration")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        JsonNode responseNode = objectMapper.readTree(responseJson).get("items");

        assertThat(responseNode.isArray()).isTrue();
    }

    @Test
    public void testGetSiteIdCTR() throws Exception {
        String responseJson = mockMvc.perform(MockMvcRequestBuilders.get("/views/ctrBySiteId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        JsonNode responseNode = objectMapper.readTree(responseJson).get("items");

        assertThat(responseNode.isArray()).isTrue();
    }

    @Test
    public void testGetSiteIdCTRByTag() throws Exception {
        String responseJson = mockMvc.perform(MockMvcRequestBuilders.get("/views/ctrBySiteIdByTag?tag=registration")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        JsonNode responseNode = objectMapper.readTree(responseJson).get("items");

        assertThat(responseNode.isArray()).isTrue();
    }

}