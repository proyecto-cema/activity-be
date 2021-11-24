package com.cema.activity.repositories;

import com.cema.activity.entities.CemaLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<CemaLocation, UUID> {

    CemaLocation findCemaLocationByNameAndEstablishmentCuigIgnoreCase(String name, String cuig);

    List<CemaLocation> findAllByEstablishmentCuig(String cuig);

}
