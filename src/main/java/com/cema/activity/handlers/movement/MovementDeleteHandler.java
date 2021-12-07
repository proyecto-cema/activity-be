package com.cema.activity.handlers.movement;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Movement;
import com.cema.activity.entities.CemaMovement;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.repositories.MovementRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class MovementDeleteHandler implements ActivityHandler<String, Movement> {

    private final MovementRepository movementRepository;

    private final AuthorizationService authorizationService;

    public MovementDeleteHandler(MovementRepository movementRepository, AuthorizationService authorizationService) {
        this.movementRepository = movementRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    public Movement handle(String uuid) {
        log.info("Handling deleting of movement activity {}.", uuid);
        UUID uuidObject = UUID.fromString(uuid);
        Optional<CemaMovement> cemaMovementOptional = movementRepository.findById(uuidObject);
        if (cemaMovementOptional.isPresent()) {
            CemaMovement cemaMovement = cemaMovementOptional.get();
            String cuig = cemaMovement.getEstablishmentCuig();
            if (!authorizationService.isOnTheSameEstablishment(cuig)) {
                throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
            }
            movementRepository.delete(cemaMovement);
        } else {
            throw new NotFoundException(String.format("Movement with id %s doesn't exits", uuid));
        }
        return null;
    }
}
