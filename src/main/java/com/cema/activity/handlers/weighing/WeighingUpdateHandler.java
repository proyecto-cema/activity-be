package com.cema.activity.handlers.weighing;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Weighing;
import com.cema.activity.entities.CemaWeighing;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.exceptions.ValidationException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.impl.WeighingMapper;
import com.cema.activity.repositories.WeighingRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import com.cema.activity.services.client.bovine.BovineClientService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class WeighingUpdateHandler implements ActivityHandler<Weighing, Weighing> {

    private final AuthorizationService authorizationService;
    private final WeighingMapper weighingMapper;
    private final WeighingRepository weighingRepository;
    private final BovineClientService bovineClientService;

    public WeighingUpdateHandler(AuthorizationService authorizationService, WeighingMapper weighingMapper,
                                 WeighingRepository weighingRepository, BovineClientService bovineClientService) {
        this.authorizationService = authorizationService;
        this.weighingMapper = weighingMapper;
        this.weighingRepository = weighingRepository;
        this.bovineClientService = bovineClientService;
    }

    @Override
    public Weighing handle(Weighing activity) {
        String cuig = activity.getEstablishmentCuig();
        UUID uuid = activity.getId();
        log.info("Handling update of weighing activity {}.", uuid);

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        String tag = activity.getBovineTag();
        if (!StringUtils.isEmpty(tag)) {
            bovineClientService.validateBovine(tag, cuig);
        }

        Optional<CemaWeighing> cemaWeighingOptional = weighingRepository.findCemaWeighingByIdAndEstablishmentCuig(uuid, cuig);
        CemaWeighing cemaWeighing;
        if (cemaWeighingOptional.isPresent()) {
            cemaWeighing = cemaWeighingOptional.get();
            cemaWeighing = weighingMapper.updateEntityWithDomain(activity, cemaWeighing);
            cemaWeighing = weighingRepository.save(cemaWeighing);
        } else {
            throw new NotFoundException(String.format(Messages.ACTIVITY_NOT_FOUND, uuid));
        }

        return weighingMapper.mapEntityToDomain(cemaWeighing);
    }
}
