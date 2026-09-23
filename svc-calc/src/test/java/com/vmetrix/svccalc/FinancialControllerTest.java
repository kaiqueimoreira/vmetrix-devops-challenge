package com.vmetrix.svccalc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FinancialControllerTest {

    @Autowired MockMvc mvc;

    @Test
    void simpleInterest_returns_ok() throws Exception {
        mvc.perform(post("/api/financial/simple-interest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"principal\":1000,\"rate\":0.10,\"periods\":1,\"years\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.interest").value(200.00));
    }

    @Test
    void discount_returns_ok() throws Exception {
        mvc.perform(get("/api/financial/discount")
                        .param("value", "100")
                        .param("percentage", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.discountedValue").value(85.00));
    }

    @Test
    void statistics_summary() throws Exception {
        mvc.perform(post("/api/statistics/summary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"values\":[2,4,4,4,5,5,7,9]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.mean").value(5.0))
                .andExpect(jsonPath("$.data.count").value(8));
    }
}
