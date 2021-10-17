package com.cema.activity.mapping.impl;

import com.cema.activity.domain.Ultrasound;
import com.cema.activity.entities.CemaUltrasound;
import com.cema.activity.mapping.ActivityMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class UltrasoundMapper implements ActivityMapper<Ultrasound, CemaUltrasound> {

    private static final String TYPE = "Ultrasound";

    @Override
    public Ultrasound mapEntityToDomain(CemaUltrasound cemaActivity) {
        return Ultrasound.builder()
                .id(cemaActivity.getId())
                .name(cemaActivity.getName())
                .description(cemaActivity.getDescription())
                .executionDate(cemaActivity.getExecutionDate())
                .bovineTag(cemaActivity.getBovineTag())
                .establishmentCuig(cemaActivity.getEstablishmentCuig())
                .type(TYPE)
                .serviceNumber(cemaActivity.getServiceNumber())
                .result(cemaActivity.getResultStatus())
                .build();
    }

    @Override
    public CemaUltrasound mapDomainToEntity(Ultrasound activity) {
        CemaUltrasound cemaUltrasound = new CemaUltrasound();

        cemaUltrasound.setId(activity.getId());
        cemaUltrasound.setEstablishmentCuig(activity.getEstablishmentCuig());
        cemaUltrasound.setName(activity.getName());
        cemaUltrasound.setDescription(activity.getDescription());
        cemaUltrasound.setExecutionDate(activity.getExecutionDate());
        cemaUltrasound.setBovineTag(activity.getBovineTag());
        cemaUltrasound.setServiceNumber(activity.getServiceNumber());
        cemaUltrasound.setResultStatus(activity.getResult());

        return cemaUltrasound;
    }

    @Override
    public CemaUltrasound updateEntityWithDomain(Ultrasound ultrasound, CemaUltrasound cemaUltrasound) {

        String name = StringUtils.hasText(ultrasound.getName()) ? ultrasound.getName() : cemaUltrasound.getName();
        String description = StringUtils.hasText(ultrasound.getDescription()) ? ultrasound.getDescription() : cemaUltrasound.getDescription();
        Date executionDate = ultrasound.getExecutionDate() != null ? ultrasound.getExecutionDate() : cemaUltrasound.getExecutionDate();
        String result = ultrasound.getResult() != null ? ultrasound.getResult() : cemaUltrasound.getResultStatus();
        String serviceNumber = StringUtils.hasText(ultrasound.getServiceNumber()) ? ultrasound.getServiceNumber() : cemaUltrasound.getServiceNumber();
        String bovineTag = StringUtils.hasText(ultrasound.getBovineTag()) ? ultrasound.getBovineTag() : cemaUltrasound.getBovineTag();

        cemaUltrasound.setName(name);
        cemaUltrasound.setDescription(description);
        cemaUltrasound.setExecutionDate(executionDate);
        cemaUltrasound.setResultStatus(result);
        cemaUltrasound.setServiceNumber(serviceNumber);
        cemaUltrasound.setBovineTag(bovineTag);

        return cemaUltrasound;
    }
}
