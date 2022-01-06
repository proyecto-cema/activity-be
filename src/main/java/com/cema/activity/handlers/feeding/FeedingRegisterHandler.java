package com.cema.activity.handlers.feeding;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Feeding;
import com.cema.activity.entities.CemaFeeding;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.impl.FeedingMapper;
import com.cema.activity.repositories.FeedingRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import com.cema.activity.services.client.bovine.BovineClientService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FeedingRegisterHandler implements ActivityHandler<Feeding, Feeding> {

    private final FeedingMapper feedingMapper;
    private final AuthorizationService authorizationService;
    private final FeedingRepository feedingRepository;
    private final BovineClientService bovineClientService;

    public FeedingRegisterHandler(FeedingMapper feedingMapper, AuthorizationService authorizationService,
                                  FeedingRepository feedingRepository, BovineClientService bovineClientService) {
        this.feedingMapper = feedingMapper;
        this.authorizationService = authorizationService;
        this.feedingRepository = feedingRepository;
        this.bovineClientService = bovineClientService;
    }

    @Override
    public Feeding handle(Feeding activity) {
        String cuig = activity.getEstablishmentCuig();

        log.info("Handling registration of new feeding activity {}.", activity.getId());

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        String tag = activity.getBovineTag();
        if (!StringUtils.isEmpty(tag)) {
            bovineClientService.validateBovine(tag, cuig);
        }

        CemaFeeding cemaFeeding = feedingMapper.mapDomainToEntity(activity);

        feedingRepository.save(cemaFeeding);

        activity.setId(cemaFeeding.getId());
        return activity;
    }

}
