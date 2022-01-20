package com.cema.activity.handlers.feeding;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Feeding;
import com.cema.activity.entities.CemaFeeding;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.repositories.FeedingRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class FeedingDeleteHandler implements ActivityHandler<String, Feeding> {

    private final FeedingRepository feedingRepository;

    private final AuthorizationService authorizationService;

    public FeedingDeleteHandler(FeedingRepository feedingRepository, AuthorizationService authorizationService) {
        this.feedingRepository = feedingRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    public Feeding handle(String uuid) {
        log.info("Handling deleting of feeding activity {}.", uuid);
        UUID uuidObject = UUID.fromString(uuid);
        Optional<CemaFeeding> cemaFeedingOptional = feedingRepository.findById(uuidObject);
        if (cemaFeedingOptional.isPresent()) {
            CemaFeeding cemaFeeding = cemaFeedingOptional.get();
            String cuig = cemaFeeding.getEstablishmentCuig();
            if (!authorizationService.isOnTheSameEstablishment(cuig)) {
                throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
            }
            feedingRepository.delete(cemaFeeding);
        } else {
            throw new NotFoundException(String.format("Feeding with id %s doesn't exits", uuid));
        }
        return null;
    }
}
