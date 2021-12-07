package com.cema.activity.mapping.impl;

import com.cema.activity.domain.Movement;
import com.cema.activity.entities.CemaMovement;
import com.cema.activity.mapping.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class MovementMapper implements Mapper<Movement, CemaMovement> {

    private static final String TYPE = "Movement";

    @Override
    public Movement mapEntityToDomain(CemaMovement entity) {
        return Movement.builder()
                .bovineTag(entity.getBovineTag())
                .batchName(entity.getBatchName())
                .description(entity.getDescription())
                .establishmentCuig(entity.getEstablishmentCuig())
                .executionDate(entity.getExecutionDate())
                .id(entity.getId())
                .type(TYPE)
                .locationName(entity.getLocation().getName())
                .name(entity.getName())
                .build();
    }

    @Override
    public CemaMovement mapDomainToEntity(Movement domain) {
        return CemaMovement.builder()
                .batchName(domain.getBatchName())
                .bovineTag(domain.getBovineTag())
                .description(domain.getDescription())
                .establishmentCuig(domain.getEstablishmentCuig())
                .executionDate(domain.getExecutionDate())
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }

    @Override
    public CemaMovement updateEntityWithDomain(Movement domain, CemaMovement entity) {
        if (StringUtils.hasText(domain.getBatchName())) {
            entity.setBovineTag(null);
        } else if (StringUtils.hasText(domain.getBovineTag())) {
            entity.setBatchName(null);
        }

        String name = StringUtils.hasText(domain.getName()) ? domain.getName() : entity.getName();
        String description = StringUtils.hasText(domain.getDescription()) ? domain.getDescription() : entity.getDescription();
        Date executionDate = domain.getExecutionDate() != null ? domain.getExecutionDate() : entity.getExecutionDate();
        String batchName = StringUtils.hasText(domain.getBatchName()) ? domain.getBatchName() : entity.getBatchName();
        String bovineTag = StringUtils.hasText(domain.getBovineTag()) ? domain.getBovineTag() : entity.getBovineTag();


        entity.setName(name);
        entity.setDescription(description);
        entity.setExecutionDate(executionDate);
        entity.setBatchName(batchName);
        entity.setBovineTag(bovineTag);

        return entity;
    }
}
