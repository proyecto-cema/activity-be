package com.cema.activity.handlers.movement;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Activity;
import com.cema.activity.domain.Movement;
import com.cema.activity.entities.CemaMovement;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.impl.MovementMapper;
import com.cema.activity.repositories.MovementRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class MovementGetHandler implements ActivityHandler<Activity, Movement> {

    private final MovementMapper movementMapper;
    private final AuthorizationService authorizationService;
    private final MovementRepository movementRepository;

    public MovementGetHandler(MovementMapper movementMapper, AuthorizationService authorizationService,
                              MovementRepository movementRepository) {
        this.movementMapper = movementMapper;
        this.authorizationService = authorizationService;
        this.movementRepository = movementRepository;
    }

    @Override
    public Movement handle(Activity activity) {
        log.info("Handling retrieval of movement activity.");

        String cuig = activity.getEstablishmentCuig();
        UUID uuid = activity.getId();

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }
        Optional<CemaMovement> cemaMovementOptional = movementRepository.findById(uuid);

        if (!cemaMovementOptional.isPresent()) {
            throw new NotFoundException(String.format("Movement with id %s doesn't exits", uuid));
        }

        return movementMapper.mapEntityToDomain(cemaMovementOptional.get());
    }
}
