package com.cema.activity.services.validation.impl;

import com.cema.activity.domain.Feeding;
import com.cema.activity.entities.CemaFeeding;
import com.cema.activity.exceptions.ValidationException;
import com.cema.activity.services.validation.ActivityValidationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FeedingValidationService implements ActivityValidationService<Feeding, CemaFeeding> {

    @Override
    public void validateActivityUpdate(Feeding newDomain, CemaFeeding originalEntity) {
        String supplyName = StringUtils.hasText(newDomain.getFood()) ? newDomain.getFood() : originalEntity.getName();

        if(originalEntity.getAmount() == null && newDomain.getAmount() != null){
            if(!StringUtils.hasText(supplyName)){
                throw new ValidationException("When populating the feeding amount, supply name must be populated as well");
            }
        }
    }
}
