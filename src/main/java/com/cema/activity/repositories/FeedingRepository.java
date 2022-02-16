package com.cema.activity.repositories;

import com.cema.activity.entities.CemaFeeding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedingRepository extends JpaRepository<CemaFeeding, UUID> {

    Optional<CemaFeeding> findCemaFeedingByIdAndEstablishmentCuig(UUID id, String cuig);

    @Query(value = "SELECT * FROM feeding WHERE execution_date = current_date", nativeQuery = true)
    List<CemaFeeding> getAllForToday();
}
