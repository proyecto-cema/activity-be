package com.cema.activity.handlers.ultrasound;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Ultrasound;
import com.cema.activity.entities.CemaUltrasound;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.impl.UltrasoundMapper;
import com.cema.activity.repositories.UltrasoundRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import com.cema.activity.services.client.bovine.BovineClientService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UltrasoundUpdateHandler implements ActivityHandler<Ultrasound, Ultrasound> {

    private final AuthorizationService authorizationService;
    private final UltrasoundMapper ultrasoundMapper;
    private final UltrasoundRepository ultrasoundRepository;
    private final BovineClientService bovineClientService;

    public UltrasoundUpdateHandler(AuthorizationService authorizationService, UltrasoundMapper ultrasoundMapper,
                                   UltrasoundRepository ultrasoundRepository, BovineClientService bovineClientService) {
        this.authorizationService = authorizationService;
        this.ultrasoundMapper = ultrasoundMapper;
        this.ultrasoundRepository = ultrasoundRepository;
        this.bovineClientService = bovineClientService;
    }

    @Override
    public Ultrasound handle(Ultrasound activity) {
        String cuig = activity.getEstablishmentCuig();
        UUID uuid = activity.getId();
        log.info("Handling update of ultrasound activity {}.", uuid);

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        String tag = activity.getBovineTag();
        if (!StringUtils.isEmpty(tag)) {
            bovineClientService.validateBovine(tag, cuig);
        }

        Optional<CemaUltrasound> cemaUltrasoundOptional = ultrasoundRepository.findCemaUltrasoundByIdAndEstablishmentCuig(uuid, cuig);
        CemaUltrasound cemaUltrasound;
        if (cemaUltrasoundOptional.isPresent()) {
            cemaUltrasound = cemaUltrasoundOptional.get();
            cemaUltrasound = ultrasoundMapper.updateEntityWithDomain(activity, cemaUltrasound);
            cemaUltrasound = ultrasoundRepository.save(cemaUltrasound);
        } else {
            throw new NotFoundException(String.format(Messages.ACTIVITY_NOT_FOUND, uuid));
        }

        return ultrasoundMapper.mapEntityToDomain(cemaUltrasound);
    }
}
