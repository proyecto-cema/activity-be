package com.cema.activity.services.database;

import com.cema.activity.domain.Activity;
import com.cema.activity.domain.Feeding;
import com.cema.activity.domain.Inoculation;
import com.cema.activity.domain.Movement;
import com.cema.activity.domain.Ultrasound;
import com.cema.activity.domain.Weighing;
import com.cema.activity.entities.CemaFeeding;
import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.entities.CemaMovement;
import com.cema.activity.entities.CemaUltrasound;
import com.cema.activity.entities.CemaWeighing;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface DatabaseService {
    Page<CemaInoculation> searchInoculations(Inoculation inoculation, int page, int size);

    Page<CemaWeighing> searchWeightings(Weighing weighing, int page, int size);

    Page<CemaUltrasound> searchUltrasounds(Ultrasound weighing, int page, int size);

    Page<CemaFeeding> searchFeedings(Feeding feeding, int page, int size);

    Page<CemaMovement> searchMovements(Movement movement, int page, int size);

    void makeAllNonDefaultButThis(String cuig, UUID id);

    void makeAllNonDefault(String cuig);

    void makeFirstDefault(String cuig);

    void makeFirstDefaultWhenNonAreDefault(String cuig);

    void saveMovement(Movement movement);

    List<Activity> getAllUsersToNotifyToday();
}
