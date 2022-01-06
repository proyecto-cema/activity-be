package com.cema.activity.mapping.impl;

import com.cema.activity.domain.Feeding;
import com.cema.activity.entities.CemaFeeding;
import com.cema.activity.mapping.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class FeedingMapper implements Mapper<Feeding, CemaFeeding> {

    private static final String TYPE = "Feeding";

    @Override
    public Feeding mapEntityToDomain(CemaFeeding entity) {
        return Feeding.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .executionDate(entity.getExecutionDate())
                .bovineTag(entity.getBovineTag())
                .establishmentCuig(entity.getEstablishmentCuig())
                .type(TYPE)
                .food(entity.getFood())
                .amount(entity.getAmount())
                .build();
    }

    @Override
    public CemaFeeding mapDomainToEntity(Feeding domain) {
        CemaFeeding cemaFeeding = new CemaFeeding();

        cemaFeeding.setId(domain.getId());
        cemaFeeding.setEstablishmentCuig(domain.getEstablishmentCuig());
        cemaFeeding.setName(domain.getName());
        cemaFeeding.setDescription(domain.getDescription());
        cemaFeeding.setExecutionDate(domain.getExecutionDate());
        cemaFeeding.setBovineTag(domain.getBovineTag());
        cemaFeeding.setFood(domain.getFood());
        cemaFeeding.setAmount(domain.getAmount());

        return cemaFeeding;
    }

    @Override
    public CemaFeeding updateEntityWithDomain(Feeding domain, CemaFeeding entity) {

        String name = StringUtils.hasText(domain.getName()) ? domain.getName() : entity.getName();
        String description = StringUtils.hasText(domain.getDescription()) ? domain.getDescription() : entity.getDescription();
        Date executionDate = domain.getExecutionDate() != null ? domain.getExecutionDate() : entity.getExecutionDate();
        String food = domain.getFood() != null ? domain.getFood() : entity.getFood();
        Long weight = domain.getAmount() != null ? domain.getAmount() : entity.getAmount();
        String bovineTag = StringUtils.hasText(domain.getBovineTag()) ? domain.getBovineTag() : entity.getBovineTag();

        entity.setName(name);
        entity.setDescription(description);
        entity.setExecutionDate(executionDate);
        entity.setFood(food);
        entity.setAmount(weight);
        entity.setBovineTag(bovineTag);

        return entity;
    }
}
