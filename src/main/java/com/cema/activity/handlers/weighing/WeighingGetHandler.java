package com.cema.activity.handlers.weighing;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Activity;
import com.cema.activity.domain.Weighing;
import com.cema.activity.entities.CemaWeighing;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.impl.WeighingMapper;
import com.cema.activity.repositories.WeighingRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class WeighingGetHandler implements ActivityHandler<Activity, Weighing> {

    private final WeighingMapper weighingMapper;
    private final AuthorizationService authorizationService;
    private final WeighingRepository weighingRepository;

    public WeighingGetHandler(WeighingMapper weighingMapper, AuthorizationService authorizationService,
                              WeighingRepository weighingRepository) {
        this.weighingMapper = weighingMapper;
        this.authorizationService = authorizationService;
        this.weighingRepository = weighingRepository;
    }

    @Override
    public Weighing handle(Activity activity) {
        log.info("Handling retrieval of weighing activity.");

        String cuig = activity.getEstablishmentCuig();
        UUID uuid = activity.getId();

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }
        Optional<CemaWeighing> cemaWeighingOptional = weighingRepository.findById(uuid);

        if (!cemaWeighingOptional.isPresent()) {
            throw new NotFoundException(String.format("Weighing with id %s doesn't exits", uuid));
        }

        return weighingMapper.mapEntityToDomain(cemaWeighingOptional.get());
    }
}
