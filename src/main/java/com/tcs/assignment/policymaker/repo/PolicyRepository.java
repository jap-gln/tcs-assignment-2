package com.tcs.assignment.policymaker.repo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.tcs.assignment.policymaker.model.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class PolicyRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public void savePolicy(Policy policy) {
        dynamoDBMapper.save(policy);
    }

}
