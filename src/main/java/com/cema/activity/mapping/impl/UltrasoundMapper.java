package com.cema.activity.mapping.impl;

import com.cema.activity.domain.Ultrasound;
import com.cema.activity.entities.CemaUltrasound;
import com.cema.activity.mapping.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class UltrasoundMapper implements Mapper<Ultrasound, CemaUltrasound> {

    private static final String TYPE = "Ultrasound";

    @Override
    public Ultrasound mapEntityToDomain(CemaUltrasound entity) {
        return Ultrasound.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .executionDate(entity.getExecutionDate())
                .bovineTag(entity.getBovineTag())
                .establishmentCuig(entity.getEstablishmentCuig())
                .type(TYPE)
                .serviceNumber(entity.getServiceNumber())
                .result(entity.getResultStatus())
                .workerUserName(entity.getWorkerUsername())
                .build();
    }

    @Override
    public CemaUltrasound mapDomainToEntity(Ultrasound domain) {
        CemaUltrasound cemaUltrasound = new CemaUltrasound();

        cemaUltrasound.setId(domain.getId());
        cemaUltrasound.setEstablishmentCuig(domain.getEstablishmentCuig());
        cemaUltrasound.setName(domain.getName());
        cemaUltrasound.setDescription(domain.getDescription());
        cemaUltrasound.setExecutionDate(domain.getExecutionDate());
        cemaUltrasound.setBovineTag(domain.getBovineTag());
        cemaUltrasound.setServiceNumber(domain.getServiceNumber());
        cemaUltrasound.setResultStatus(domain.getResult());
        cemaUltrasound.setWorkerUsername(domain.getWorkerUserName());

        return cemaUltrasound;
    }

    @Override
    public CemaUltrasound updateEntityWithDomain(Ultrasound domain, CemaUltrasound entity) {

        String name = StringUtils.hasText(domain.getName()) ? domain.getName() : entity.getName();
        String description = StringUtils.hasText(domain.getDescription()) ? domain.getDescription() : entity.getDescription();
        Date executionDate = domain.getExecutionDate() != null ? domain.getExecutionDate() : entity.getExecutionDate();
        String result = domain.getResult() != null ? domain.getResult() : entity.getResultStatus();
        String serviceNumber = StringUtils.hasText(domain.getServiceNumber()) ? domain.getServiceNumber() : entity.getServiceNumber();
        String bovineTag = StringUtils.hasText(domain.getBovineTag()) ? domain.getBovineTag() : entity.getBovineTag();
        String workerUsername = StringUtils.hasText(domain.getWorkerUserName()) ? domain.getWorkerUserName() : entity.getWorkerUsername();

        entity.setName(name);
        entity.setDescription(description);
        entity.setExecutionDate(executionDate);
        entity.setResultStatus(result);
        entity.setServiceNumber(serviceNumber);
        entity.setBovineTag(bovineTag);
        entity.setWorkerUsername(workerUsername);

        return entity;
    }
}
