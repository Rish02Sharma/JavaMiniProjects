package org.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectExplorerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetApiOverview() throws Exception {
        mockMvc.perform(get("/api/v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("Java Mini Projects Microservice"))
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void testGetModules() throws Exception {
        mockMvc.perform(get("/api/v1/modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.webhookDesign").isArray())
                .andExpect(jsonPath("$.lowLevelDesign").isArray());
    }

    @Test
    void testGetRecommendations() throws Exception {
        mockMvc.perform(get("/api/v1/recommendations/A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").value("A"))
                .andExpect(jsonPath("$.recommendedSongsWithScore").isMap());
    }
}
