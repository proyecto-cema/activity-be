package com.cema.activity.mapping.impl;

import com.cema.activity.domain.Weighing;
import com.cema.activity.entities.CemaWeighing;
import com.cema.activity.mapping.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class WeighingMapper implements Mapper<Weighing, CemaWeighing> {

    private static final String TYPE = "Weighing";

    @Override
    public Weighing mapEntityToDomain(CemaWeighing entity) {
        return Weighing.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .executionDate(entity.getExecutionDate())
                .bovineTag(entity.getBovineTag())
                .establishmentCuig(entity.getEstablishmentCuig())
                .type(TYPE)
                .weight(entity.getWeight())
                .category(entity.getCategory())
                .dentalNotes(entity.getDentalNotes())
                .workerUserName(entity.getWorkerUsername())
                .build();
    }

    @Override
    public CemaWeighing mapDomainToEntity(Weighing domain) {
        CemaWeighing cemaWeighing = new CemaWeighing();

        cemaWeighing.setId(domain.getId());
        cemaWeighing.setEstablishmentCuig(domain.getEstablishmentCuig());
        cemaWeighing.setName(domain.getName());
        cemaWeighing.setDescription(domain.getDescription());
        cemaWeighing.setExecutionDate(domain.getExecutionDate());
        cemaWeighing.setBovineTag(domain.getBovineTag());
        cemaWeighing.setWeight(domain.getWeight());
        cemaWeighing.setCategory(domain.getCategory());
        cemaWeighing.setDentalNotes(domain.getDentalNotes());
        cemaWeighing.setWorkerUsername(domain.getWorkerUserName());

        return cemaWeighing;
    }

    @Override
    public CemaWeighing updateEntityWithDomain(Weighing domain, CemaWeighing entity) {

        String name = StringUtils.hasText(domain.getName()) ? domain.getName() : entity.getName();
        String description = StringUtils.hasText(domain.getDescription()) ? domain.getDescription() : entity.getDescription();
        Date executionDate = domain.getExecutionDate() != null ? domain.getExecutionDate() : entity.getExecutionDate();
        Long weight = domain.getWeight() != null ? domain.getWeight() : entity.getWeight();
        String category = StringUtils.hasText(domain.getCategory()) ? domain.getCategory() : entity.getCategory();
        String dentalNotes = StringUtils.hasText(domain.getDentalNotes()) ? domain.getDentalNotes() : entity.getDentalNotes();
        String bovineTag = StringUtils.hasText(domain.getBovineTag()) ? domain.getBovineTag() : entity.getBovineTag();
        String workerUsername = StringUtils.hasText(domain.getWorkerUserName()) ? domain.getWorkerUserName() : entity.getWorkerUsername();

        entity.setName(name);
        entity.setDescription(description);
        entity.setExecutionDate(executionDate);
        entity.setWeight(weight);
        entity.setCategory(category);
        entity.setDentalNotes(dentalNotes);
        entity.setBovineTag(bovineTag);
        entity.setWorkerUsername(workerUsername);

        return entity;
    }
}
