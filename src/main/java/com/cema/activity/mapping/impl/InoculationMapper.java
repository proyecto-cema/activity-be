package com.cema.activity.mapping.impl;

import com.cema.activity.domain.Inoculation;
import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.mapping.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class InoculationMapper implements Mapper<Inoculation, CemaInoculation> {

    private static final String TYPE = "Inoculation";

    @Override
    public Inoculation mapEntityToDomain(CemaInoculation entity) {
        return Inoculation.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .executionDate(entity.getExecutionDate())
                .bovineTag(entity.getBovineTag())
                .batchName(entity.getBatchName())
                .establishmentCuig(entity.getEstablishmentCuig())
                .type(TYPE)
                .dose(entity.getDose())
                .brand(entity.getBrand())
                .drug(entity.getDrug())
                .product(entity.getProduct())
                .workerUserName(entity.getWorkerUsername())
                .build();
    }

    @Override
    public CemaInoculation mapDomainToEntity(Inoculation domain) {
        CemaInoculation cemaInoculation = new CemaInoculation();

        cemaInoculation.setId(domain.getId());
        cemaInoculation.setEstablishmentCuig(domain.getEstablishmentCuig());
        cemaInoculation.setName(domain.getName());
        cemaInoculation.setDescription(domain.getDescription());
        cemaInoculation.setExecutionDate(domain.getExecutionDate());
        cemaInoculation.setBovineTag(domain.getBovineTag());
        cemaInoculation.setBatchName(domain.getBatchName());
        cemaInoculation.setDose(domain.getDose());
        cemaInoculation.setBrand(domain.getBrand());
        cemaInoculation.setDrug(domain.getDrug());
        cemaInoculation.setProduct(domain.getProduct());
        cemaInoculation.setWorkerUsername(domain.getWorkerUserName());

        return cemaInoculation;
    }

    @Override
    public CemaInoculation updateEntityWithDomain(Inoculation domain, CemaInoculation entity) {
        if (StringUtils.hasText(domain.getBatchName())) {
            entity.setBovineTag(null);
        } else if (StringUtils.hasText(domain.getBovineTag())) {
            entity.setBatchName(null);
        }

        String name = StringUtils.hasText(domain.getName()) ? domain.getName() : entity.getName();
        String description = StringUtils.hasText(domain.getDescription()) ? domain.getDescription() : entity.getDescription();
        Date executionDate = domain.getExecutionDate() != null ? domain.getExecutionDate() : entity.getExecutionDate();
        Long dose = domain.getDose() != null ? domain.getDose() : entity.getDose();
        String brand = StringUtils.hasText(domain.getBrand()) ? domain.getBrand() : entity.getBrand();
        String drug = StringUtils.hasText(domain.getDrug()) ? domain.getDrug() : entity.getDrug();
        String product = StringUtils.hasText(domain.getProduct()) ? domain.getProduct() : entity.getProduct();
        String batchName = StringUtils.hasText(domain.getBatchName()) ? domain.getBatchName() : entity.getBatchName();
        String bovineTag = StringUtils.hasText(domain.getBovineTag()) ? domain.getBovineTag() : entity.getBovineTag();
        String workerUsername = StringUtils.hasText(domain.getWorkerUserName()) ? domain.getWorkerUserName() : entity.getWorkerUsername();

        entity.setName(name);
        entity.setDescription(description);
        entity.setExecutionDate(executionDate);
        entity.setDose(dose);
        entity.setBrand(brand);
        entity.setDrug(drug);
        entity.setProduct(product);
        entity.setBatchName(batchName);
        entity.setBovineTag(bovineTag);
        entity.setWorkerUsername(workerUsername);

        return entity;
    }
}
