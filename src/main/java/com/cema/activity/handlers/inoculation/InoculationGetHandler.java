package com.cema.activity.handlers.inoculation;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Activity;
import com.cema.activity.domain.Inoculation;
import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.impl.InoculationMapper;
import com.cema.activity.repositories.InoculationRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class InoculationGetHandler implements ActivityHandler<Activity, Inoculation> {

    private final InoculationMapper inoculationMapper;
    private final AuthorizationService authorizationService;
    private final InoculationRepository inoculationRepository;

    public InoculationGetHandler(InoculationMapper inoculationMapper, AuthorizationService authorizationService,
                                 InoculationRepository inoculationRepository) {
        this.inoculationMapper = inoculationMapper;
        this.authorizationService = authorizationService;
        this.inoculationRepository = inoculationRepository;
    }

    @Override
    public Inoculation handle(Activity activity) {
        log.info("Handling retrieval of inoculation activity.");

        String cuig = activity.getEstablishmentCuig();
        UUID uuid = activity.getId();

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }
        Optional<CemaInoculation> cemaInoculationOptional = inoculationRepository.findById(uuid);

        if (!cemaInoculationOptional.isPresent()) {
            throw new NotFoundException(String.format("Inoculation with id %s doesn't exits", uuid));
        }

        return inoculationMapper.mapEntityToDomain(cemaInoculationOptional.get());
    }
}
