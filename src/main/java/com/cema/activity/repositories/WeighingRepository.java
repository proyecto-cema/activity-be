package com.cema.activity.repositories;

import com.cema.activity.entities.CemaWeighing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WeighingRepository extends JpaRepository<CemaWeighing, UUID> {
}