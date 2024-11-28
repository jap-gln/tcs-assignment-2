package com.tcs.assignment.policymaker.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcs.assignment.policymaker.model.Policy;
import com.tcs.assignment.policymaker.repo.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SQSListenerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private SQSListener sqsListener;

    private String snsMessage;
    private String policyJson;
    private Policy policy;

    @BeforeEach
    public void setup() throws Exception {
        policy = new Policy(UUID.randomUUID(), "Health Insurance", 250000.0);
        ObjectMapper realObjectMapper = new ObjectMapper();
        policyJson = realObjectMapper.writeValueAsString(policy);

        // Debugging log to check policyJson
        System.out.println("Constructed policyJson: " + policyJson);

        // Escape quotes in policyJson for inclusion in snsMessage
        String escapedPolicyJson = policyJson.replace("\"", "\\\"");

        // Construct snsMessage with escaped policyJson
        snsMessage = "{\"Type\": \"Notification\", \"MessageId\": \"000b6a99-ffd5-4687-ab26-ad6cc7786ed1\", \"TopicArn\": \"arn:aws:sns:us-east-1:000000000000:sender\", \"Message\": \"" + escapedPolicyJson + "\"}";

        // Debugging log to check snsMessage
        System.out.println("Constructed snsMessage: " + snsMessage);

        // Mock object mapper behavior
        JsonNode rootNode = realObjectMapper.readTree(snsMessage);
        String extractedPolicyJson = rootNode.get("Message").asText();
        when(objectMapper.readTree(anyString())).thenReturn(rootNode);
        when(objectMapper.readValue(anyString(), eq(Policy.class))).thenReturn(policy);
    }

    @Test
    public void testListen() throws Exception {
        sqsListener.listen(snsMessage);

        verify(policyRepository).savePolicy(policy);
        verifyNoMoreInteractions(policyRepository);
    }
}
