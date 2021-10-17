package com.cema.activity.handlers.ultrasound;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Ultrasound;
import com.cema.activity.entities.CemaUltrasound;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.impl.UltrasoundMapper;
import com.cema.activity.repositories.UltrasoundRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UltrasoundRegisterHandler implements ActivityHandler<Ultrasound, Ultrasound> {

    private final UltrasoundMapper ultrasoundMapper;
    private final AuthorizationService authorizationService;
    private final UltrasoundRepository ultrasoundRepository;

    public UltrasoundRegisterHandler(UltrasoundMapper ultrasoundMapper, AuthorizationService authorizationService,
                                     UltrasoundRepository ultrasoundRepository) {
        this.ultrasoundMapper = ultrasoundMapper;
        this.authorizationService = authorizationService;
        this.ultrasoundRepository = ultrasoundRepository;
    }


    @Override
    public Ultrasound handle(Ultrasound activity) {
        String cuig = activity.getEstablishmentCuig();

        log.info("Handling registration of new ultrasound activity {}.", activity.getId());

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        CemaUltrasound cemaUltrasound = ultrasoundMapper.mapDomainToEntity(activity);

        ultrasoundRepository.save(cemaUltrasound);

        activity.setId(cemaUltrasound.getId());
        return activity;
    }

}
