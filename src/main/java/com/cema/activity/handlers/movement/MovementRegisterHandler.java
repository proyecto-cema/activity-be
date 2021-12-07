package com.cema.activity.handlers.movement;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Movement;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.exceptions.ValidationException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.services.authorization.AuthorizationService;
import com.cema.activity.services.client.bovine.BovineClientService;
import com.cema.activity.services.database.DatabaseService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MovementRegisterHandler implements ActivityHandler<Movement, Movement> {


    private final AuthorizationService authorizationService;
    private final DatabaseService databaseService;
    private final BovineClientService bovineClientService;

    public MovementRegisterHandler(AuthorizationService authorizationService, DatabaseService databaseService,
                                   BovineClientService bovineClientService) {
        this.authorizationService = authorizationService;
        this.databaseService = databaseService;
        this.bovineClientService = bovineClientService;
    }


    @Override
    public Movement handle(Movement activity) {
        String cuig = activity.getEstablishmentCuig();

        log.info("Handling registration of new movement activity {}.", activity.getId());

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        String batchName = activity.getBatchName();
        String tag = activity.getBovineTag();

        if (StringUtils.isEmpty(batchName) == StringUtils.isEmpty(tag)) {
            throw new ValidationException(Messages.MISSING_OR_INCORRECT_FIELDS);
        }

        if (!StringUtils.isEmpty(batchName)) {
            bovineClientService.validateBatch(batchName, cuig);
        }
        if (!StringUtils.isEmpty(tag)) {
            bovineClientService.validateBovine(tag, cuig);
        }

        databaseService.saveMovement(activity);

        return activity;
    }

}
