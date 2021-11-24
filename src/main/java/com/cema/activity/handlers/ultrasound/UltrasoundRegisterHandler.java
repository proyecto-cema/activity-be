package com.cema.activity.handlers.ultrasound;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Ultrasound;
import com.cema.activity.entities.CemaUltrasound;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.impl.UltrasoundMapper;
import com.cema.activity.repositories.UltrasoundRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import com.cema.activity.services.client.bovine.BovineClientService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UltrasoundRegisterHandler implements ActivityHandler<Ultrasound, Ultrasound> {

    private final UltrasoundMapper ultrasoundMapper;
    private final AuthorizationService authorizationService;
    private final UltrasoundRepository ultrasoundRepository;
    private final BovineClientService bovineClientService;

    public UltrasoundRegisterHandler(UltrasoundMapper ultrasoundMapper, AuthorizationService authorizationService,
                                     UltrasoundRepository ultrasoundRepository, BovineClientService bovineClientService) {
        this.ultrasoundMapper = ultrasoundMapper;
        this.authorizationService = authorizationService;
        this.ultrasoundRepository = ultrasoundRepository;
        this.bovineClientService = bovineClientService;
    }

    @Override
    public Ultrasound handle(Ultrasound activity) {
        String cuig = activity.getEstablishmentCuig();

        log.info("Handling registration of new ultrasound activity {}.", activity.getId());

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        String tag = activity.getBovineTag();
        if (!StringUtils.isEmpty(tag)) {
            bovineClientService.validateBovine(tag, cuig);
        }

        CemaUltrasound cemaUltrasound = ultrasoundMapper.mapDomainToEntity(activity);

        ultrasoundRepository.save(cemaUltrasound);

        activity.setId(cemaUltrasound.getId());
        return activity;
    }

}
