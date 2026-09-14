package com.vmetrix.svcmisc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StringControllerTest {

    @Autowired MockMvc mvc;

    @Test
    void reverse_returns_ok() throws Exception {
        mvc.perform(post("/api/string/reverse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":\"hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result").value("olleh"));
    }

    @Test
    void palindrome_true() throws Exception {
        mvc.perform(post("/api/string/palindrome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":\"racecar\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isPalindrome").value(true));
    }

    @Test
    void email_valid() throws Exception {
        mvc.perform(post("/api/validation/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":\"user@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(true));
    }
}
