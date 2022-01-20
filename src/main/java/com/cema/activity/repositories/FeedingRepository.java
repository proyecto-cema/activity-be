package com.cema.activity.repositories;

import com.cema.activity.entities.CemaFeeding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedingRepository extends JpaRepository<CemaFeeding, UUID> {

    Optional<CemaFeeding> findCemaFeedingByIdAndEstablishmentCuig(UUID id, String cuig);
}
