package com.cema.activity.mapping;

public interface ActivityMapper<DOM, ENT> {

    DOM mapEntityToDomain(ENT cemaActivity);

    ENT mapDomainToEntity(DOM activity);

    ENT updateEntityWithDomain(DOM activity, ENT cemaActivity);
}
