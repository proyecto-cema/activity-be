package com.cema.activity.mapping;

public interface Mapper<DOM, ENT> {

    DOM mapEntityToDomain(ENT entity);

    ENT mapDomainToEntity(DOM domain);

    ENT updateEntityWithDomain(DOM domain, ENT entity);
}
