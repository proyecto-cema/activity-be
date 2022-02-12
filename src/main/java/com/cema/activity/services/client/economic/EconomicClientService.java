package com.cema.activity.services.client.economic;

import com.cema.activity.domain.economic.SupplyOperation;
import lombok.SneakyThrows;

public interface EconomicClientService {
    @SneakyThrows
    void validateSupply(String name);

    @SneakyThrows
    void registerSupplyOperation(SupplyOperation supplyOperation);
}
