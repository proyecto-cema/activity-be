package com.cema.activity.repositories;

import com.cema.activity.entities.CemaMovement;
import com.cema.activity.entities.CemaUltrasound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UltrasoundRepository extends JpaRepository<CemaUltrasound, UUID> {

    Optional<CemaUltrasound> findCemaUltrasoundByIdAndEstablishmentCuig(UUID id, String cuig);
}
