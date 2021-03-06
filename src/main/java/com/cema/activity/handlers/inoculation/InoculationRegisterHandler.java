package com.cema.activity.handlers.inoculation;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Inoculation;
import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.exceptions.ValidationException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.impl.InoculationMapper;
import com.cema.activity.repositories.InoculationRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import com.cema.activity.services.client.bovine.BovineClientService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InoculationRegisterHandler implements ActivityHandler<Inoculation, Inoculation> {

    private final InoculationMapper inoculationMapper;
    private final AuthorizationService authorizationService;
    private final InoculationRepository inoculationRepository;
    private final BovineClientService bovineClientService;


    public InoculationRegisterHandler(InoculationMapper inoculationMapper, AuthorizationService authorizationService,
                                      InoculationRepository inoculationRepository, BovineClientService bovineClientService) {
        this.inoculationMapper = inoculationMapper;
        this.authorizationService = authorizationService;
        this.inoculationRepository = inoculationRepository;
        this.bovineClientService = bovineClientService;
    }

    @Override
    public Inoculation handle(Inoculation activity) {
        String cuig = activity.getEstablishmentCuig();

        log.info("Handling registration of new inoculation activity {}.", activity.getId());

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        String batchName = activity.getBatchName();
        String tag = activity.getBovineTag();

        if (StringUtils.isEmpty(batchName) == StringUtils.isEmpty(tag)) {
            throw new ValidationException(Messages.MISSING_OR_INCORRECT_FIELDS);
        }

        if (!StringUtils.isEmpty(batchName)) {
            bovineClientService.validateBatch(batchName, cuig);
        }
        if (!StringUtils.isEmpty(tag)) {
            bovineClientService.validateBovine(tag, cuig);
        }

        CemaInoculation cemaInoculation = inoculationMapper.mapDomainToEntity(activity);

        inoculationRepository.save(cemaInoculation);

        activity.setId(cemaInoculation.getId());
        return activity;
    }

}
