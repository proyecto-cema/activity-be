package com.cema.activity.services.client.administration;

import com.cema.activity.domain.audit.Audit;

public interface AdministrationClientService {

    void validateEstablishment(String cuig);

    void sendAuditRequest(Audit audit);
}
