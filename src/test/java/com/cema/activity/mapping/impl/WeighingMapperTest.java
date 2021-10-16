package com.cema.activity.mapping.impl;

import com.cema.activity.domain.Weighing;
import com.cema.activity.entities.CemaWeighing;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class WeighingMapperTest {

    private final WeighingMapper weighingMapper = new WeighingMapper();

    @Test
    public void mapEntityToDomainShouldReturnAnWeighingFromACemaWeighing() {
        UUID id = UUID.randomUUID();
        String cuig = "321";
        String name = "name";
        String description = "description";
        Date executionDate = new Date();
        String bovineTag = "bovineTag";
        Long weight = 1253L;
        String category = "category";
        String dentalNotes = "dentalNotes";

        CemaWeighing cemaWeighing = new CemaWeighing();

        cemaWeighing.setId(id);
        cemaWeighing.setEstablishmentCuig(cuig);
        cemaWeighing.setName(name);
        cemaWeighing.setDescription(description);
        cemaWeighing.setExecutionDate(executionDate);
        cemaWeighing.setBovineTag(bovineTag);
        cemaWeighing.setWeight(weight);
        cemaWeighing.setCategory(category);
        cemaWeighing.setDentalNotes(dentalNotes);

        Weighing result = weighingMapper.mapEntityToDomain(cemaWeighing);

        assertThat(result.getId(), is(id));
        assertThat(result.getEstablishmentCuig(), is(cuig));
        assertThat(result.getName(), is(name));
        assertThat(result.getDescription(), is(description));
        assertThat(result.getExecutionDate(), is(executionDate));
        assertThat(result.getBovineTag(), is(bovineTag));
        assertThat(result.getWeight(), is(weight));
        assertThat(result.getCategory(), is(category));
        assertThat(result.getDentalNotes(), is(dentalNotes));
        assertThat(result.getType(), is("Weighing"));
    }

    @Test
    public void mapDomainToEntityShouldReturnACemaWeighingFromAnWeighing() {
        UUID id = UUID.randomUUID();
        String cuig = "321";
        String name = "name";
        String description = "description";
        Date executionDate = new Date();
        String bovineTag = "bovineTag";
        String batchName = "batchName";
        Long weight = 123L;
        String category = "category";
        String dentalNotes = "dentalNotes";

        Weighing weighing = Weighing.builder()
                .id(id)
                .establishmentCuig(cuig)
                .name(name)
                .description(description)
                .executionDate(executionDate)
                .bovineTag(bovineTag)
                .weight(weight)
                .category(category)
                .dentalNotes(dentalNotes)
                .build();

        CemaWeighing result = weighingMapper.mapDomainToEntity(weighing);

        assertThat(result.getId(), is(id));
        assertThat(result.getEstablishmentCuig(), is(cuig));
        assertThat(result.getName(), is(name));
        assertThat(result.getDescription(), is(description));
        assertThat(result.getExecutionDate(), is(executionDate));
        assertThat(result.getBovineTag(), is(bovineTag));
        assertThat(result.getWeight(), is(weight));
        assertThat(result.getCategory(), is(category));
        assertThat(result.getDentalNotes(), is(dentalNotes));
    }

    @Test
    public void mapDomainToEntityShouldPopulateAllFieldsFromWeighingWhenPresent() {
        UUID id = UUID.randomUUID();
        String cuig = "321";
        String name = "name";
        String description = "description";
        Date executionDate = new Date();
        String bovineTag = "bovineTag";
        Long weight = 123L;
        String category = "category";
        String dentalNotes = "dentalNotes";

        Weighing weighing = Weighing.builder()
                .id(UUID.randomUUID())
                .establishmentCuig("otherCuig")
                .name(name)
                .description(description)
                .executionDate(executionDate)
                .bovineTag(bovineTag)
                .weight(weight)
                .category(category)
                .dentalNotes(dentalNotes)
                .build();

        CemaWeighing cemaWeighing = new CemaWeighing();
        cemaWeighing.setId(id);
        cemaWeighing.setEstablishmentCuig(cuig);

        CemaWeighing result = weighingMapper.updateEntityWithDomain(weighing, cemaWeighing);

        assertThat(result, is(cemaWeighing));

        assertThat(result.getId(), is(id));
        assertThat(result.getEstablishmentCuig(), is(cuig));
        assertThat(result.getName(), is(name));
        assertThat(result.getDescription(), is(description));
        assertThat(result.getExecutionDate(), is(executionDate));
        assertThat(result.getBovineTag(), is(bovineTag));
        assertThat(result.getWeight(), is(weight));
        assertThat(result.getCategory(), is(category));
        assertThat(result.getDentalNotes(), is(dentalNotes));
    }

    @Test
    public void mapDomainToEntityShouldLeaveCemaWeighingFieldsAsTheyAreIfNotPresentInWeighing() {
        UUID id = UUID.randomUUID();
        String cuig = "321";
        String name = "name";
        String description = "description";
        Date executionDate = new Date();
        String bovineTag = "bovineTag";
        Long weight = 123L;
        String category = "category";
        String dentalNotes = "dentalNotes";

        CemaWeighing cemaWeighing = new CemaWeighing();

        cemaWeighing.setId(id);
        cemaWeighing.setEstablishmentCuig(cuig);
        cemaWeighing.setName(name);
        cemaWeighing.setDescription(description);
        cemaWeighing.setExecutionDate(executionDate);
        cemaWeighing.setBovineTag(bovineTag);
        cemaWeighing.setWeight(weight);
        cemaWeighing.setCategory(category);
        cemaWeighing.setDentalNotes(dentalNotes);

        Weighing weighing = Weighing.builder().build();

        CemaWeighing result = weighingMapper.updateEntityWithDomain(weighing, cemaWeighing);

        assertThat(result, is(cemaWeighing));

        assertThat(result.getId(), is(id));
        assertThat(result.getEstablishmentCuig(), is(cuig));
        assertThat(result.getName(), is(name));
        assertThat(result.getDescription(), is(description));
        assertThat(result.getExecutionDate(), is(executionDate));
        assertThat(result.getBovineTag(), is(bovineTag));
        assertThat(result.getWeight(), is(weight));
        assertThat(result.getCategory(), is(category));
        assertThat(result.getDentalNotes(), is(dentalNotes));
    }

}