package com.sapient.idempotencypoc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sapient.idempotencypoc.entity.CachedEntity;
import com.sapient.idempotencypoc.redis.RedisService;
import com.sapient.idempotencypoc.request.BusinessRequest;
import com.sapient.idempotencypoc.response.BusinessResponse;
import com.sapient.idempotencypoc.service.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.sapient.idempotencypoc.constants.Constants.IN_PROGRESS;
import static com.sapient.idempotencypoc.constants.Constants.SUCCESS;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class Controller {

    @Autowired
    private Service service;

    @Autowired
    private StringRedisTemplate template;

    @Autowired
    private RedisService redisService;

    @GetMapping("/health")
    public String health() {
        return "up";
    }

    @RequestMapping(
            value = "/create/business",
            method = RequestMethod.POST,
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<BusinessResponse> createBusiness(@RequestBody BusinessRequest request,
                                                           @RequestHeader String correlationId)
            throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        ObjectMapper om = new ObjectMapper();
        String json;
        BusinessResponse response;
        log.info("validating request correlationId: {}", correlationId);
        var redisCachedEntity = redisService.getCacheValue(correlationId);
        if (redisCachedEntity == null) { // unique request
            json = ow.writeValueAsString(CachedEntity.builder().status(IN_PROGRESS).build()); // set in progress
            redisService.setCacheKeyValue(correlationId, json); // store in redis
            response = service.createBusiness(request); // create business
            json = ow.writeValueAsString(CachedEntity.builder()
                    .status(SUCCESS)
                    .businessResponse(response)
                    .build());
            redisService.setCacheKeyValue(correlationId, json); // store success
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        // not a unique request
        var cachedEntity = om.readValue(redisCachedEntity, CachedEntity.class);
        if (cachedEntity.getStatus().equalsIgnoreCase(SUCCESS)) { // if successful
            cachedEntity.getBusinessResponse().setStatus(HttpStatus.BAD_REQUEST);
            return ResponseEntity
                    .badRequest()
                    .body(cachedEntity.getBusinessResponse());  // return response from cache
        }
        // if in progress
        response = service.createBusiness(request); // create business
        json = ow.writeValueAsString(CachedEntity.builder().status(SUCCESS).businessResponse(response).build()); // created successfully
        redisService.setCacheKeyValue(correlationId, json); // store in cache
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
