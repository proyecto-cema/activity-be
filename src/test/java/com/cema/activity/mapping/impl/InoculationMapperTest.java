package com.cema.activity.mapping.impl;

import com.cema.activity.domain.Inoculation;
import com.cema.activity.entities.CemaInoculation;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class InoculationMapperTest {

    private final InoculationMapper inoculationMapper = new InoculationMapper();

    @Test
    public void mapEntityToDomainShouldReturnAnInoculationFromACemaInoculation() {
        UUID id = UUID.randomUUID();
        String cuig = "321";
        String name = "name";
        String description = "description";
        Date executionDate = new Date();
        String bovineTag = "bovineTag";
        String batchName = "batchName";
        Long dose = 123L;
        String brand = "brand";
        String drug = "drug";
        String product = "product";

        CemaInoculation cemaInoculation = new CemaInoculation();

        cemaInoculation.setId(id);
        cemaInoculation.setEstablishmentCuig(cuig);
        cemaInoculation.setName(name);
        cemaInoculation.setDescription(description);
        cemaInoculation.setExecutionDate(executionDate);
        cemaInoculation.setBovineTag(bovineTag);
        cemaInoculation.setBatchName(batchName);
        cemaInoculation.setDose(dose);
        cemaInoculation.setBrand(brand);
        cemaInoculation.setDrug(drug);
        cemaInoculation.setProduct(product);

        Inoculation result = inoculationMapper.mapEntityToDomain(cemaInoculation);

        assertThat(result.getId(), is(id));
        assertThat(result.getEstablishmentCuig(), is(cuig));
        assertThat(result.getName(), is(name));
        assertThat(result.getDescription(), is(description));
        assertThat(result.getExecutionDate(), is(executionDate));
        assertThat(result.getBovineTag(), is(bovineTag));
        assertThat(result.getBatchName(), is(batchName));
        assertThat(result.getDose(), is(dose));
        assertThat(result.getBrand(), is(brand));
        assertThat(result.getDrug(), is(drug));
        assertThat(result.getProduct(), is(product));
        assertThat(result.getType(), is("Inoculation"));
    }

    @Test
    public void mapDomainToEntityShouldReturnACemaInoculationFromAnInoculation(){
        UUID id = UUID.randomUUID();
        String cuig = "321";
        String name = "name";
        String description = "description";
        Date executionDate = new Date();
        String bovineTag = "bovineTag";
        String batchName = "batchName";
        Long dose = 123L;
        String brand = "brand";
        String drug = "drug";
        String product = "product";

        Inoculation inoculation = Inoculation.builder()
                .id(id)
                .establishmentCuig(cuig)
                .name(name)
                .description(description)
                .executionDate(executionDate)
                .bovineTag(bovineTag)
                .batchName(batchName)
                .dose(dose)
                .brand(brand)
                .drug(drug)
                .product(product)
                .build();

        CemaInoculation result = inoculationMapper.updateEntityWithDomain(inoculation);

        assertThat(result.getId(), is(id));
        assertThat(result.getEstablishmentCuig(), is(cuig));
        assertThat(result.getName(), is(name));
        assertThat(result.getDescription(), is(description));
        assertThat(result.getExecutionDate(), is(executionDate));
        assertThat(result.getBovineTag(), is(bovineTag));
        assertThat(result.getBatchName(), is(batchName));
        assertThat(result.getDose(), is(dose));
        assertThat(result.getBrand(), is(brand));
        assertThat(result.getDrug(), is(drug));
        assertThat(result.getProduct(), is(product));
    }

    @Test
    public void mapDomainToEntityShouldPopulateAllFieldsFromInoculationWhenPresent(){
        UUID id = UUID.randomUUID();
        String cuig = "321";
        String name = "name";
        String description = "description";
        Date executionDate = new Date();
        String bovineTag = "bovineTag";
        Long dose = 123L;
        String brand = "brand";
        String drug = "drug";
        String product = "product";

        Inoculation inoculation = Inoculation.builder()
                .id(UUID.randomUUID())
                .establishmentCuig("otherCuig")
                .name(name)
                .description(description)
                .executionDate(executionDate)
                .bovineTag(bovineTag)
                .dose(dose)
                .brand(brand)
                .drug(drug)
                .product(product)
                .build();

        CemaInoculation cemaInoculation = new CemaInoculation();
        cemaInoculation.setId(id);
        cemaInoculation.setEstablishmentCuig(cuig);

        CemaInoculation result = inoculationMapper.updateEntityWithDomain(inoculation, cemaInoculation);

        assertThat(result, is(cemaInoculation));

        assertThat(result.getId(), is(id));
        assertThat(result.getEstablishmentCuig(), is(cuig));
        assertThat(result.getName(), is(name));
        assertThat(result.getDescription(), is(description));
        assertThat(result.getExecutionDate(), is(executionDate));
        assertThat(result.getBovineTag(), is(bovineTag));
        assertThat(result.getDose(), is(dose));
        assertThat(result.getBrand(), is(brand));
        assertThat(result.getDrug(), is(drug));
        assertThat(result.getProduct(), is(product));
    }

    @Test
    public void mapDomainToEntityShouldLeaveCemaInoculationFieldsAsTheyAreIfNotPresentInInoculation(){
        UUID id = UUID.randomUUID();
        String cuig = "321";
        String name = "name";
        String description = "description";
        Date executionDate = new Date();
        String bovineTag = "bovineTag";
        Long dose = 123L;
        String brand = "brand";
        String drug = "drug";
        String product = "product";

        CemaInoculation cemaInoculation = new CemaInoculation();

        cemaInoculation.setId(id);
        cemaInoculation.setEstablishmentCuig(cuig);
        cemaInoculation.setName(name);
        cemaInoculation.setDescription(description);
        cemaInoculation.setExecutionDate(executionDate);
        cemaInoculation.setBovineTag(bovineTag);
        cemaInoculation.setDose(dose);
        cemaInoculation.setBrand(brand);
        cemaInoculation.setDrug(drug);
        cemaInoculation.setProduct(product);

        Inoculation inoculation = Inoculation.builder().build();

        CemaInoculation result = inoculationMapper.updateEntityWithDomain(inoculation, cemaInoculation);

        assertThat(result, is(cemaInoculation));

        assertThat(result.getId(), is(id));
        assertThat(result.getEstablishmentCuig(), is(cuig));
        assertThat(result.getName(), is(name));
        assertThat(result.getDescription(), is(description));
        assertThat(result.getExecutionDate(), is(executionDate));
        assertThat(result.getBovineTag(), is(bovineTag));
        assertThat(result.getDose(), is(dose));
        assertThat(result.getBrand(), is(brand));
        assertThat(result.getDrug(), is(drug));
        assertThat(result.getProduct(), is(product));
    }

}