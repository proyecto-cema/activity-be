package com.cema.activity.repositories;

import com.cema.activity.entities.CemaLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<CemaLocation, UUID> {

    CemaLocation findCemaLocationByEstablishmentCuigAndAndIsDefault(String cuig, Boolean isDefault);

    CemaLocation findCemaLocationByNameAndEstablishmentCuigIgnoreCase(String name, String cuig);

    List<CemaLocation> findAllByEstablishmentCuig(String cuig);

    CemaLocation findTopByEstablishmentCuig(String cuig);

    CemaLocation findTopByEstablishmentCuigAndIsDefault(String cuig, Boolean isDefault);

    @Modifying
    @Transactional
    @Query("update CemaLocation location set location.isDefault = false where location.establishmentCuig = :cuig and location.id <> :id")
    void makeAllNonDefault(@Param("cuig") String cuig, @Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("update CemaLocation location set location.isDefault = false where location.establishmentCuig = :cuig")
    void makeAllNonDefault(@Param("cuig") String cuig);

}
