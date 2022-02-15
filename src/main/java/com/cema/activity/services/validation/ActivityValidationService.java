package com.cema.activity.services.validation;

public interface ActivityValidationService<DOMAIN, ENTITY> {

    public void validateActivityUpdate(DOMAIN newDomain, ENTITY originalEntity);
}
