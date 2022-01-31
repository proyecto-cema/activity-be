package com.cema.activity.services.client.economic;

import lombok.SneakyThrows;

public interface EconomicClientService {
    @SneakyThrows
    void validateSupply(String name);
}
