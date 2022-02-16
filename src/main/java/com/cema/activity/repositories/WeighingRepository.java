package com.cema.activity.repositories;

import com.cema.activity.entities.CemaWeighing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WeighingRepository extends JpaRepository<CemaWeighing, UUID> {

    Optional<CemaWeighing> findCemaWeighingByIdAndEstablishmentCuig(UUID id, String cuig);

    @Query(value = "SELECT * FROM weighing WHERE execution_date = current_date", nativeQuery = true)
    List<CemaWeighing> getAllForToday();
}
