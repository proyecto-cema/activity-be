package com.cema.activity.handlers.movement;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Movement;
import com.cema.activity.entities.CemaMovement;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.exceptions.ValidationException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.impl.MovementMapper;
import com.cema.activity.repositories.MovementRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import com.cema.activity.services.client.bovine.BovineClientService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class MovementUpdateHandler implements ActivityHandler<Movement, Movement> {

    private final AuthorizationService authorizationService;
    private final MovementMapper movementMapper;
    private final MovementRepository movementRepository;
    private final BovineClientService bovineClientService;

    public MovementUpdateHandler(AuthorizationService authorizationService, MovementMapper movementMapper,
                                 MovementRepository movementRepository, BovineClientService bovineClientService) {
        this.authorizationService = authorizationService;
        this.movementMapper = movementMapper;
        this.movementRepository = movementRepository;
        this.bovineClientService = bovineClientService;
    }

    @Override
    public Movement handle(Movement activity) {
        String cuig = activity.getEstablishmentCuig();
        UUID uuid = activity.getId();
        log.info("Handling update of movement activity {}.", uuid);

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        String batchName = activity.getBatchName();
        String tag = activity.getBovineTag();

        if (!StringUtils.isEmpty(batchName) && !StringUtils.isEmpty(tag)) {
            throw new ValidationException(Messages.MISSING_OR_INCORRECT_FIELDS);
        }

        if (!StringUtils.isEmpty(batchName)) {
            bovineClientService.validateBatch(batchName, cuig);
        }
        if (!StringUtils.isEmpty(tag)) {
            bovineClientService.validateBovine(tag, cuig);
        }

        Optional<CemaMovement> cemaMovementOptional = movementRepository.findCemaMovementByIdAndEstablishmentCuig(uuid, cuig);
        CemaMovement cemaMovement;
        if (cemaMovementOptional.isPresent()) {
            cemaMovement = cemaMovementOptional.get();
            cemaMovement = movementMapper.updateEntityWithDomain(activity, cemaMovement);
            cemaMovement = movementRepository.save(cemaMovement);
        } else {
            throw new NotFoundException(String.format(Messages.ACTIVITY_NOT_FOUND, uuid));
        }

        return movementMapper.mapEntityToDomain(cemaMovement);
    }
}
