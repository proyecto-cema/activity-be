package com.cema.activity.handlers.weighing;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Weighing;
import com.cema.activity.entities.CemaWeighing;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.impl.WeighingMapper;
import com.cema.activity.repositories.WeighingRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WeighingRegisterHandler implements ActivityHandler<Weighing, Weighing> {

    private final WeighingMapper weighingMapper;
    private final AuthorizationService authorizationService;
    private final WeighingRepository weighingRepository;

    public WeighingRegisterHandler(WeighingMapper weighingMapper, AuthorizationService authorizationService,
                                   WeighingRepository weighingRepository) {
        this.weighingMapper = weighingMapper;
        this.authorizationService = authorizationService;
        this.weighingRepository = weighingRepository;
    }


    @Override
    public Weighing handle(Weighing activity) {
        String cuig = activity.getEstablishmentCuig();

        log.info("Handling registration of new weighing activity {}.", activity.getId());

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        CemaWeighing cemaWeighing = weighingMapper.mapDomainToEntity(activity);

        weighingRepository.save(cemaWeighing);

        activity.setId(cemaWeighing.getId());
        return activity;
    }

}
