package com.tcs.assignment.policymaker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcs.assignment.policymaker.model.Policy;
import io.awspring.cloud.sns.core.SnsNotification;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PolicyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SnsTemplate snsTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PolicyController policyController;

    private Policy policy;
    private String policyJson;

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(policyController).build();
        policy = new Policy(UUID.randomUUID(), "Health Insurance", 250000.0);
        policyJson = new ObjectMapper().writeValueAsString(policy);
    }

    @Test
    public void testInsertIntoDynamoDB() throws Exception {
        when(objectMapper.writeValueAsString(any(Policy.class))).thenReturn(policyJson);

        mockMvc.perform(post("/api/policies")
                        .header("x-api-header", "ValidApiKey12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(policyJson))
                .andExpect(status().isOk());

        verify(snsTemplate).sendNotification(eq("sender"), any(SnsNotification.class));
    }
}
