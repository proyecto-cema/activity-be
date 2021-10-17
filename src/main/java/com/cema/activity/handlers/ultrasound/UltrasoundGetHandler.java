package com.cema.activity.handlers.ultrasound;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Activity;
import com.cema.activity.domain.Ultrasound;
import com.cema.activity.entities.CemaUltrasound;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.impl.UltrasoundMapper;
import com.cema.activity.repositories.UltrasoundRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UltrasoundGetHandler implements ActivityHandler<Activity, Ultrasound> {

    private final UltrasoundMapper ultrasoundMapper;
    private final AuthorizationService authorizationService;
    private final UltrasoundRepository ultrasoundRepository;

    public UltrasoundGetHandler(UltrasoundMapper ultrasoundMapper, AuthorizationService authorizationService,
                                UltrasoundRepository ultrasoundRepository) {
        this.ultrasoundMapper = ultrasoundMapper;
        this.authorizationService = authorizationService;
        this.ultrasoundRepository = ultrasoundRepository;
    }

    @Override
    public Ultrasound handle(Activity activity) {
        log.info("Handling retrieval of ultrasound activity.");

        String cuig = activity.getEstablishmentCuig();
        UUID uuid = activity.getId();

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }
        Optional<CemaUltrasound> cemaUltrasoundOptional = ultrasoundRepository.findById(uuid);

        if (!cemaUltrasoundOptional.isPresent()) {
            throw new NotFoundException(String.format("Ultrasound with id %s doesn't exits", uuid));
        }

        return ultrasoundMapper.mapEntityToDomain(cemaUltrasoundOptional.get());
    }
}
