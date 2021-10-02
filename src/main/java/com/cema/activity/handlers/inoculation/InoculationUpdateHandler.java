package com.cema.activity.handlers.inoculation;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Inoculation;
import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.exceptions.ValidationException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.impl.InoculationMapper;
import com.cema.activity.repositories.InoculationRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class InoculationUpdateHandler implements ActivityHandler<Inoculation, Inoculation> {

    private final AuthorizationService authorizationService;
    private final InoculationMapper inoculationMapper;
    private final InoculationRepository inoculationRepository;

    public InoculationUpdateHandler(AuthorizationService authorizationService, InoculationMapper inoculationMapper,
                                    InoculationRepository inoculationRepository) {
        this.authorizationService = authorizationService;
        this.inoculationMapper = inoculationMapper;
        this.inoculationRepository = inoculationRepository;
    }

    @Override
    public Inoculation handle(Inoculation activity) {
        String cuig = activity.getEstablishmentCuig();
        UUID uuid = activity.getId();
        log.info("Handling update of inoculation activity {}.", uuid);

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        if (!StringUtils.isEmpty(activity.getBatchName()) && !StringUtils.isEmpty(activity.getBovineTag())) {
            throw new ValidationException(Messages.MISSING_OR_INCORRECT_FIELDS);
        }

        Optional<CemaInoculation> cemaInoculationOptional = inoculationRepository.findById(uuid);
        CemaInoculation cemaInoculation;
        if (cemaInoculationOptional.isPresent()) {
            cemaInoculation = cemaInoculationOptional.get();
            cemaInoculation = inoculationMapper.updateEntityWithDomain(activity, cemaInoculation);
            cemaInoculation = inoculationRepository.save(cemaInoculation);
        } else {
            throw new NotFoundException(String.format(Messages.ACTIVITY_NOT_FOUND, uuid));
        }

        return inoculationMapper.mapEntityToDomain(cemaInoculation);
    }
}
