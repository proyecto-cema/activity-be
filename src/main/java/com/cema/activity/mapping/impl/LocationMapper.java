package com.cema.activity.mapping.impl;


import com.cema.activity.domain.Location;
import com.cema.activity.entities.CemaLocation;
import com.cema.activity.mapping.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LocationMapper implements Mapper<Location, CemaLocation> {

    @Override
    public Location mapEntityToDomain(CemaLocation entity) {
        return Location.builder()
                .name(entity.getName())
                .description(entity.getDescription())
                .size(entity.getSize())
                .establishmentCuig(entity.getEstablishmentCuig())
                .build();
    }

    @Override
    public CemaLocation mapDomainToEntity(Location domain) {
        return CemaLocation.builder()
                .name(domain.getName())
                .description(domain.getDescription())
                .size(domain.getSize())
                .establishmentCuig(domain.getEstablishmentCuig())
                .build();
    }

    @Override
    public CemaLocation updateEntityWithDomain(Location domain, CemaLocation entity) {

        String name = StringUtils.hasText(domain.getName()) ? domain.getName() : entity.getName();
        String description = StringUtils.hasText(domain.getDescription()) ? domain.getDescription() : entity.getDescription();
        Long size = domain.getSize() != null ? domain.getSize() : entity.getSize();

        entity.setName(name);
        entity.setDescription(description);
        entity.setSize(size);
        return entity;
    }
}
