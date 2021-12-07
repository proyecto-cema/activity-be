package com.cema.activity.services.client.bovine;

public interface BovineClientService {
    void validateBovine(String tag, String cuig);

    void validateBatch(String batchName, String cuig);
}
