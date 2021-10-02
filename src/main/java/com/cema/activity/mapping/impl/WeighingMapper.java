package com.cema.activity.mapping.impl;

import com.cema.activity.domain.Weighing;
import com.cema.activity.entities.CemaWeighing;
import com.cema.activity.mapping.ActivityMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class WeighingMapper implements ActivityMapper<Weighing, CemaWeighing> {

    private static final String TYPE = "Weighing";

    @Override
    public Weighing mapEntityToDomain(CemaWeighing cemaActivity) {
        return Weighing.builder()
                .id(cemaActivity.getId())
                .name(cemaActivity.getName())
                .description(cemaActivity.getDescription())
                .executionDate(cemaActivity.getExecutionDate())
                .bovineTag(cemaActivity.getBovineTag())
                .establishmentCuig(cemaActivity.getEstablishmentCuig())
                .type(TYPE)
                .weight(cemaActivity.getWeight())
                .category(cemaActivity.getCategory())
                .dentalNotes(cemaActivity.getDentalNotes())
                .build();
    }

    @Override
    public CemaWeighing mapDomainToEntity(Weighing activity) {
        CemaWeighing cemaWeighing = new CemaWeighing();

        cemaWeighing.setId(activity.getId());
        cemaWeighing.setEstablishmentCuig(activity.getEstablishmentCuig());
        cemaWeighing.setName(activity.getName());
        cemaWeighing.setDescription(activity.getDescription());
        cemaWeighing.setExecutionDate(activity.getExecutionDate());
        cemaWeighing.setBovineTag(activity.getBovineTag());
        cemaWeighing.setWeight(activity.getWeight());
        cemaWeighing.setCategory(activity.getCategory());
        cemaWeighing.setDentalNotes(activity.getDentalNotes());

        return cemaWeighing;
    }

    @Override
    public CemaWeighing updateEntityWithDomain(Weighing weighing, CemaWeighing cemaWeighing) {

        String name = StringUtils.hasText(weighing.getName()) ? weighing.getName() : cemaWeighing.getName();
        String description = StringUtils.hasText(weighing.getDescription()) ? weighing.getDescription() : cemaWeighing.getDescription();
        Date executionDate = weighing.getExecutionDate() != null ? weighing.getExecutionDate() : cemaWeighing.getExecutionDate();
        Long weight = weighing.getWeight() != null ? weighing.getWeight() : cemaWeighing.getWeight();
        String category = StringUtils.hasText(weighing.getCategory()) ? weighing.getCategory() : cemaWeighing.getCategory();
        String dentalNotes = StringUtils.hasText(weighing.getDentalNotes()) ? weighing.getDentalNotes() : cemaWeighing.getDentalNotes();
        String bovineTag = StringUtils.hasText(weighing.getBovineTag()) ? weighing.getBovineTag() : cemaWeighing.getBovineTag();

        cemaWeighing.setName(name);
        cemaWeighing.setDescription(description);
        cemaWeighing.setExecutionDate(executionDate);
        cemaWeighing.setWeight(weight);
        cemaWeighing.setCategory(category);
        cemaWeighing.setDentalNotes(dentalNotes);
        cemaWeighing.setBovineTag(bovineTag);

        return cemaWeighing;
    }
}
