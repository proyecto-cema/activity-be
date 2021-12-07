package com.cema.activity.repositories;

import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.entities.CemaMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovementRepository extends JpaRepository<CemaMovement, UUID> {

    Optional<CemaMovement> findCemaMovementByIdAndEstablishmentCuig(UUID id, String cuig);

}
