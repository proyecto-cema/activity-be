package com.cema.activity.repositories;

import com.cema.activity.entities.CemaUltrasound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UltrasoundRepository extends JpaRepository<CemaUltrasound, UUID> {

    Optional<CemaUltrasound> findCemaUltrasoundByIdAndEstablishmentCuig(UUID id, String cuig);
}
