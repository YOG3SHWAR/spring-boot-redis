package com.sapient.idempotencypoc.service;

import com.sapient.idempotencypoc.entity.Business;
import com.sapient.idempotencypoc.request.BusinessRequest;
import com.sapient.idempotencypoc.response.BusinessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@org.springframework.stereotype.Service
@Slf4j
public class ServiceImpl implements Service {

    @Override
    public BusinessResponse createBusiness(BusinessRequest businessRequest) {
        log.info("calling CRM to create business");
        String uri = "http://localhost:1080/business/create";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Business> response = restTemplate.exchange(uri, HttpMethod.POST, null, Business.class);
        log.info("CRM response = {}", response.getBody());
        return BusinessResponse.builder()
                .description("this is a poc for idempotency")
                .business(businessRequest.getBusiness())
                .status(HttpStatus.OK)
                .build();
    }
}
