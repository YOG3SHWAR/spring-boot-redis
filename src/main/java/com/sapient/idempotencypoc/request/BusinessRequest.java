package com.sapient.idempotencypoc.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sapient.idempotencypoc.entity.Business;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessRequest {

    @JsonProperty("Business")
    Business business;

    @JsonProperty("Status")
    String status;

}
