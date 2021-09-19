package com.cema.activity.handlers.inoculation;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Inoculation;
import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.repositories.InoculationRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class InoculationDeleteHandler implements ActivityHandler<String, Inoculation> {
    private final Logger LOG = LoggerFactory.getLogger(InoculationDeleteHandler.class);

    private final InoculationRepository inoculationRepository;

    private final AuthorizationService authorizationService;

    public InoculationDeleteHandler(InoculationRepository inoculationRepository, AuthorizationService authorizationService) {
        this.inoculationRepository = inoculationRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    public Inoculation handle(String uuid) {
        LOG.info("Handling deleting of inoculation activity {}.", uuid);
        UUID uuidObject = UUID.fromString(uuid);
        Optional<CemaInoculation> cemaInoculationOptional = inoculationRepository.findById(uuidObject);
        if (cemaInoculationOptional.isPresent()) {
            CemaInoculation cemaInoculation = cemaInoculationOptional.get();
            String cuig = cemaInoculation.getEstablishmentCuig();
            if (!authorizationService.isOnTheSameEstablishment(cuig)) {
                throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
            }
            inoculationRepository.delete(cemaInoculation);
        } else {
            throw new NotFoundException(String.format("Inoculation with id %s doesn't exits", uuid));
        }
        return null;
    }
}
