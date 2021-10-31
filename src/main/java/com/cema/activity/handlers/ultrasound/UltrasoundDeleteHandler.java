package com.cema.activity.handlers.ultrasound;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Ultrasound;
import com.cema.activity.entities.CemaUltrasound;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.repositories.UltrasoundRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UltrasoundDeleteHandler implements ActivityHandler<String, Ultrasound> {

    private final UltrasoundRepository ultrasoundRepository;

    private final AuthorizationService authorizationService;

    public UltrasoundDeleteHandler(UltrasoundRepository ultrasoundRepository, AuthorizationService authorizationService) {
        this.ultrasoundRepository = ultrasoundRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    public Ultrasound handle(String uuid) {
        log.info("Handling deleting of ultrasound activity {}.", uuid);
        UUID uuidObject = UUID.fromString(uuid);
        Optional<CemaUltrasound> cemaUltrasoundOptional = ultrasoundRepository.findById(uuidObject);
        if (cemaUltrasoundOptional.isPresent()) {
            CemaUltrasound cemaUltrasound = cemaUltrasoundOptional.get();
            String cuig = cemaUltrasound.getEstablishmentCuig();
            if (!authorizationService.isOnTheSameEstablishment(cuig)) {
                throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
            }
            ultrasoundRepository.delete(cemaUltrasound);
        } else {
            throw new NotFoundException(String.format("Ultrasound with id %s doesn't exits", uuid));
        }
        return null;
    }
}
