package com.cema.activity.handlers.feeding;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Feeding;
import com.cema.activity.entities.CemaFeeding;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.impl.FeedingMapper;
import com.cema.activity.repositories.FeedingRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import com.cema.activity.services.client.bovine.BovineClientService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class FeedingUpdateHandler implements ActivityHandler<Feeding, Feeding> {

    private final AuthorizationService authorizationService;
    private final FeedingMapper feedingMapper;
    private final FeedingRepository feedingRepository;
    private final BovineClientService bovineClientService;

    public FeedingUpdateHandler(AuthorizationService authorizationService, FeedingMapper feedingMapper,
                                FeedingRepository feedingRepository, BovineClientService bovineClientService) {
        this.authorizationService = authorizationService;
        this.feedingMapper = feedingMapper;
        this.feedingRepository = feedingRepository;
        this.bovineClientService = bovineClientService;
    }

    @Override
    public Feeding handle(Feeding activity) {
        String cuig = activity.getEstablishmentCuig();
        UUID uuid = activity.getId();
        log.info("Handling update of feeding activity {}.", uuid);

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        String tag = activity.getBovineTag();
        if (!StringUtils.isEmpty(tag)) {
            bovineClientService.validateBovine(tag, cuig);
        }

        Optional<CemaFeeding> cemaFeedingOptional = feedingRepository.findCemaFeedingByIdAndEstablishmentCuig(uuid, cuig);
        CemaFeeding cemaFeeding;
        if (cemaFeedingOptional.isPresent()) {
            cemaFeeding = cemaFeedingOptional.get();
            cemaFeeding = feedingMapper.updateEntityWithDomain(activity, cemaFeeding);
            cemaFeeding = feedingRepository.save(cemaFeeding);
        } else {
            throw new NotFoundException(String.format(Messages.ACTIVITY_NOT_FOUND, uuid));
        }

        return feedingMapper.mapEntityToDomain(cemaFeeding);
    }
}
