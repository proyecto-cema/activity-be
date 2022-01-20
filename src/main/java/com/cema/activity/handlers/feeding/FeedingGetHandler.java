package com.cema.activity.handlers.feeding;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Activity;
import com.cema.activity.domain.Feeding;
import com.cema.activity.entities.CemaFeeding;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.impl.FeedingMapper;
import com.cema.activity.repositories.FeedingRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class FeedingGetHandler implements ActivityHandler<Activity, Feeding> {

    private final FeedingMapper feedingMapper;
    private final AuthorizationService authorizationService;
    private final FeedingRepository feedingRepository;

    public FeedingGetHandler(FeedingMapper feedingMapper, AuthorizationService authorizationService,
                             FeedingRepository feedingRepository) {
        this.feedingMapper = feedingMapper;
        this.authorizationService = authorizationService;
        this.feedingRepository = feedingRepository;
    }

    @Override
    public Feeding handle(Activity activity) {
        log.info("Handling retrieval of feeding activity.");

        String cuig = activity.getEstablishmentCuig();
        UUID uuid = activity.getId();

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }
        Optional<CemaFeeding> cemaFeedingOptional = feedingRepository.findById(uuid);

        if (!cemaFeedingOptional.isPresent()) {
            throw new NotFoundException(String.format("Feeding with id %s doesn't exits", uuid));
        }

        return feedingMapper.mapEntityToDomain(cemaFeedingOptional.get());
    }
}
