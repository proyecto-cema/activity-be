package com.cema.activity.handlers.inoculation;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Activity;
import com.cema.activity.domain.Inoculation;
import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.exceptions.ValidationException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.impl.InoculationMapper;
import com.cema.activity.repositories.InoculationRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InoculationRegisterHandler implements ActivityHandler<Inoculation, Inoculation> {

    private final Logger LOG = LoggerFactory.getLogger(InoculationRegisterHandler.class);

    private final InoculationMapper inoculationMapper;
    private final AuthorizationService authorizationService;
    private final InoculationRepository inoculationRepository;

    public InoculationRegisterHandler(InoculationMapper inoculationMapper, AuthorizationService authorizationService,
                                      InoculationRepository inoculationRepository) {
        this.inoculationMapper = inoculationMapper;
        this.authorizationService = authorizationService;
        this.inoculationRepository = inoculationRepository;
    }


    @Override
    public Inoculation handle(Inoculation activity) {
        String cuig = activity.getEstablishmentCuig();

        LOG.info("Handling registration of new inoculation activity {}.", activity.getId());

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        if(StringUtils.isEmpty(activity.getBatchName()) == StringUtils.isEmpty(activity.getBovineTag())){
            throw new ValidationException(Messages.MISSING_OR_INCORRECT_FIELDS);
        }
        CemaInoculation cemaInoculation = inoculationMapper.mapDomainToEntity(activity);

        inoculationRepository.save(cemaInoculation);

        activity.setId(cemaInoculation.getId());
        return activity;
    }

}
