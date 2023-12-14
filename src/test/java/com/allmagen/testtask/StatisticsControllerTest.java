package com.allmagen.testtask;

import com.allmagen.testtask.controller.StatisticsController;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StatisticsControllerTest {
    private final static String INTERVIEW_X = "classpath:testdata/interview.x.small.csv";
    private final static String INTERVIEW_Y = "classpath:testdata/interview.y.csv";

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private StatisticsController statisticsController;
    @Autowired
    private ResourceLoader resourceLoader;

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

}
