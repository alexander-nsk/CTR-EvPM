package com.allmagen.testtask;

import com.allmagen.testtask.controller.HomeController;
import com.allmagen.testtask.controller.StatisticsController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {HomeController.class, StatisticsController.class})
public class StatisticsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenWac_whenServletContext_thenItProvidesGreetController() throws Exception {
        mockMvc.perform(post("/"))
                .andExpect(status().isOk());
    }
}
