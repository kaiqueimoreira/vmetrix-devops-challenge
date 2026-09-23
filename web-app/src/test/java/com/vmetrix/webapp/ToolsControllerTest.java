package com.vmetrix.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ToolsControllerTest {

    @Autowired MockMvc mvc;

    @Test void home_loads()       throws Exception { mvc.perform(get("/")).andExpect(status().isOk()); }
    @Test void string_page()      throws Exception { mvc.perform(get("/tools/string")).andExpect(status().isOk()); }
    @Test void validation_page()  throws Exception { mvc.perform(get("/tools/validation")).andExpect(status().isOk()); }
    @Test void financial_page()   throws Exception { mvc.perform(get("/tools/financial")).andExpect(status().isOk()); }
    @Test void statistics_page()  throws Exception { mvc.perform(get("/tools/statistics")).andExpect(status().isOk()); }

    @Test
    void reverse_post() throws Exception {
        mvc.perform(post("/tools/string/reverse").param("input", "hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("olleh")));
    }

    @Test
    void stats_post() throws Exception {
        mvc.perform(post("/tools/statistics/summary").param("values", "2,4,4,4,5,5,7,9"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("6.0000")));
    }
}
