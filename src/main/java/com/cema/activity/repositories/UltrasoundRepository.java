package com.cema.activity.repositories;

import com.cema.activity.entities.CemaUltrasound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UltrasoundRepository extends JpaRepository<CemaUltrasound, UUID> {

    Optional<CemaUltrasound> findCemaUltrasoundByIdAndEstablishmentCuig(UUID id, String cuig);

    @Query(value = "SELECT * FROM ultrasound WHERE execution_date = current_date", nativeQuery = true)
    List<CemaUltrasound> getAllForToday();
}
