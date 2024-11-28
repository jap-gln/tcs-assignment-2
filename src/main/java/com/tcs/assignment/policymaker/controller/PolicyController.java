package com.tcs.assignment.policymaker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcs.assignment.policymaker.model.Policy;
import com.tcs.assignment.policymaker.repo.PolicyRepository;
import io.awspring.cloud.sns.core.SnsNotification;
import io.awspring.cloud.sns.core.SnsTemplate;
import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SnsTemplate snsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PolicyRepository policyRepository;

    @PostMapping
    @ResponseBody
    public String insertIntoDynamoDB(@RequestHeader("x-api-header") String apiKey, @Valid @RequestBody Policy policy) throws JsonProcessingException {
        String policyJson = objectMapper.writeValueAsString(policy);
        log.info("policyJson - {}", policyJson);
        log.info("controller - {}, {}, {}", policy.getId(), policy.getName(), policy.getAmount());
        snsTemplate.sendNotification("sender", SnsNotification.of(policyJson));
        return "Message published to SNS topic sender ";
    }

}
