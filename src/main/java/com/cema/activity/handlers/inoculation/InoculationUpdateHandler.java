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
import com.cema.activity.services.client.bovine.BovineClientService;
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
    private final BovineClientService bovineClientService;

    public InoculationUpdateHandler(AuthorizationService authorizationService, InoculationMapper inoculationMapper,
                                    InoculationRepository inoculationRepository, BovineClientService bovineClientService) {
        this.authorizationService = authorizationService;
        this.inoculationMapper = inoculationMapper;
        this.inoculationRepository = inoculationRepository;
        this.bovineClientService = bovineClientService;
    }

    @Override
    public Inoculation handle(Inoculation activity) {
        String cuig = activity.getEstablishmentCuig();
        UUID uuid = activity.getId();
        log.info("Handling update of inoculation activity {}.", uuid);

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        String batchName = activity.getBatchName();
        String tag = activity.getBovineTag();

        if (!StringUtils.isEmpty(batchName) && !StringUtils.isEmpty(tag)) {
            throw new ValidationException(Messages.MISSING_OR_INCORRECT_FIELDS);
        }

        if (!StringUtils.isEmpty(batchName)) {
            bovineClientService.validateBatch(batchName, cuig);
        }
        if (!StringUtils.isEmpty(tag)) {
            bovineClientService.validateBovine(tag, cuig);
        }

        Optional<CemaInoculation> cemaInoculationOptional = inoculationRepository.findCemaInoculationByIdAndEstablishmentCuig(uuid, cuig);
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
