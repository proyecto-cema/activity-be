package com.cema.activity.handlers.weighing;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Weighing;
import com.cema.activity.entities.CemaWeighing;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.repositories.WeighingRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class WeighingDeleteHandler implements ActivityHandler<String, Weighing> {

    private final WeighingRepository weighingRepository;

    private final AuthorizationService authorizationService;

    public WeighingDeleteHandler(WeighingRepository weighingRepository, AuthorizationService authorizationService) {
        this.weighingRepository = weighingRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    public Weighing handle(String uuid) {
        log.info("Handling deleting of weighing activity {}.", uuid);
        UUID uuidObject = UUID.fromString(uuid);
        Optional<CemaWeighing> cemaWeighingOptional = weighingRepository.findById(uuidObject);
        if (cemaWeighingOptional.isPresent()) {
            CemaWeighing cemaWeighing = cemaWeighingOptional.get();
            String cuig = cemaWeighing.getEstablishmentCuig();
            if (!authorizationService.isOnTheSameEstablishment(cuig)) {
                throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
            }
            weighingRepository.delete(cemaWeighing);
        } else {
            throw new NotFoundException(String.format("Weighing with id %s doesn't exits", uuid));
        }
        return null;
    }
}
