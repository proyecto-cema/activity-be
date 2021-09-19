package com.cema.activity.services.database;

import com.cema.activity.domain.Inoculation;
import com.cema.activity.entities.CemaInoculation;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface DatabaseService {
    Page<CemaInoculation> searchInoculations(Inoculation inoculation, int page, int size);
}
