package com.cema.activity.handlers.feeding;

import com.cema.activity.domain.Feeding;
import com.cema.activity.domain.search.SearchRequest;
import com.cema.activity.domain.search.SearchResponse;
import com.cema.activity.entities.CemaFeeding;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.Mapper;
import com.cema.activity.services.authorization.AuthorizationService;
import com.cema.activity.services.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FeedingSearchHandler implements ActivityHandler<SearchRequest, SearchResponse<Feeding>> {

    private final DatabaseService databaseService;
    private final Mapper<Feeding, CemaFeeding> feedingMapper;
    private final AuthorizationService authorizationService;

    public FeedingSearchHandler(DatabaseService databaseService, Mapper<Feeding, CemaFeeding> feedingMapper,
                                AuthorizationService authorizationService) {
        this.databaseService = databaseService;
        this.feedingMapper = feedingMapper;
        this.authorizationService = authorizationService;
    }

    @Override
    public SearchResponse<Feeding> handle(SearchRequest activity) {
        Feeding feeding = (Feeding) activity.getActivity();
        log.info("Handling search of feeding activity {}.", feeding.getId());

        if (!authorizationService.isAdmin()) {
            feeding.setEstablishmentCuig(authorizationService.getCurrentUserCuig());
        }

        Page<CemaFeeding> cemaFeedingPage = databaseService.searchFeedings(feeding, activity.getPage(), activity.getSize());
        List<CemaFeeding> cemaWeightings = cemaFeedingPage.getContent();
        List<Feeding> weightings = cemaWeightings.stream()
                .map(feedingMapper::mapEntityToDomain)
                .collect(Collectors.toList());

        return SearchResponse.<Feeding>builder()
                .activities(weightings)
                .currentPage(cemaFeedingPage.getNumber())
                .totalElements(cemaFeedingPage.getNumberOfElements())
                .totalPages(cemaFeedingPage.getTotalPages())
                .build();
    }
}
