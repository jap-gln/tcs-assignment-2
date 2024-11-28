package com.tcs.assignment.policymaker.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcs.assignment.policymaker.model.Policy;
import com.tcs.assignment.policymaker.repo.PolicyRepository;
import io.awspring.cloud.sns.core.SnsTemplate;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;

@Component
public class SQSListener {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SnsTemplate snsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PolicyRepository policyRepository;

    @SqsListener("listener")
    public void listen(String message) throws JsonProcessingException {
        log.info("message - {}", message);
        JsonNode rootNode = objectMapper.readTree(message);
        String policyJson = rootNode.get("Message").asText();
        log.info("read the json - {}", policyJson);
        Policy policy = objectMapper.readValue(policyJson, Policy.class);
        log.info("jaimini - {}, {}, {}", policy.getId(), policy.getName(), policy.getAmount());
        if (policy.getName() != null && !policy.getName().isEmpty()) {
            policyRepository.savePolicy(policy);
            log.info("Message saved to DynamoDB - {}", policy);
        }
    }
}
