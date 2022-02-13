package com.sapient.idempotencypoc.service;

import com.sapient.idempotencypoc.request.BusinessRequest;
import com.sapient.idempotencypoc.response.BusinessResponse;

public interface Service {
    public BusinessResponse createBusiness(BusinessRequest businessRequest);
}
