package com.cema.activity.mapping.impl;

import com.cema.activity.constants.Activities;
import com.cema.activity.domain.Inoculation;
import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.mapping.ActivityMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class InoculationMapper implements ActivityMapper<Inoculation, CemaInoculation> {

    @Override
    public Inoculation mapEntityToDomain(CemaInoculation cemaActivity) {
        return Inoculation.builder()
                .id(cemaActivity.getId())
                .name(cemaActivity.getName())
                .description(cemaActivity.getDescription())
                .executionDate(cemaActivity.getExecutionDate())
                .bovineTag(cemaActivity.getBovineTag())
                .batchName(cemaActivity.getBatchName())
                .establishmentCuig(cemaActivity.getEstablishmentCuig())
                .type(Activities.INOCULATION)
                .dose(cemaActivity.getDose())
                .brand(cemaActivity.getBrand())
                .drug(cemaActivity.getDrug())
                .product(cemaActivity.getProduct())
                .build();
    }

    @Override
    public CemaInoculation mapDomainToEntity(Inoculation activity) {
        CemaInoculation cemaInoculation = new CemaInoculation();

        cemaInoculation.setId(activity.getId());
        cemaInoculation.setEstablishmentCuig(activity.getEstablishmentCuig());
        cemaInoculation.setName(activity.getName());
        cemaInoculation.setDescription(activity.getDescription());
        cemaInoculation.setExecutionDate(activity.getExecutionDate());
        cemaInoculation.setBovineTag(activity.getBovineTag());
        cemaInoculation.setBatchName(activity.getBatchName());
        cemaInoculation.setDose(activity.getDose());
        cemaInoculation.setBrand(activity.getBrand());
        cemaInoculation.setDrug(activity.getDrug());
        cemaInoculation.setProduct(activity.getProduct());

        return cemaInoculation;
    }

    @Override
    public CemaInoculation mapDomainToEntity(Inoculation inoculation, CemaInoculation cemaInoculation) {
        if (StringUtils.hasText(inoculation.getBatchName())) {
            cemaInoculation.setBovineTag(null);
        } else if (StringUtils.hasText(inoculation.getBovineTag())) {
            cemaInoculation.setBatchName(null);
        }

        String name = StringUtils.hasText(inoculation.getName()) ? inoculation.getName() : cemaInoculation.getName();
        String description = StringUtils.hasText(inoculation.getDescription()) ? inoculation.getDescription() : cemaInoculation.getDescription();
        Date executionDate = inoculation.getExecutionDate() != null ? inoculation.getExecutionDate() : cemaInoculation.getExecutionDate();
        Long dose = inoculation.getDose() != null ? inoculation.getDose() : cemaInoculation.getDose();
        String brand = StringUtils.hasText(inoculation.getBrand()) ? inoculation.getBrand() : cemaInoculation.getBrand();
        String drug = StringUtils.hasText(inoculation.getDrug()) ? inoculation.getDrug() : cemaInoculation.getDrug();
        String product = StringUtils.hasText(inoculation.getProduct()) ? inoculation.getProduct() : cemaInoculation.getProduct();
        String batchName = StringUtils.hasText(inoculation.getBatchName()) ? inoculation.getBatchName() : cemaInoculation.getBatchName();
        String bovineTag = StringUtils.hasText(inoculation.getBovineTag()) ? inoculation.getBovineTag() : cemaInoculation.getBovineTag();

        cemaInoculation.setName(name);
        cemaInoculation.setDescription(description);
        cemaInoculation.setExecutionDate(executionDate);
        cemaInoculation.setDose(dose);
        cemaInoculation.setBrand(brand);
        cemaInoculation.setDrug(drug);
        cemaInoculation.setProduct(product);
        cemaInoculation.setBatchName(batchName);
        cemaInoculation.setBovineTag(bovineTag);

        return cemaInoculation;
    }
}
