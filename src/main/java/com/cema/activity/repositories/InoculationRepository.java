package com.cema.activity.repositories;

import com.cema.activity.entities.CemaInoculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InoculationRepository extends JpaRepository<CemaInoculation, UUID> {

    Optional<CemaInoculation> findCemaInoculationByIdAndEstablishmentCuig(UUID id, String cuig);

}
