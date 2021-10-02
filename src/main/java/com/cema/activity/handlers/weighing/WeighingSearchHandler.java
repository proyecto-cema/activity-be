package com.cema.activity.handlers.weighing;

import com.cema.activity.domain.Weighing;
import com.cema.activity.domain.search.SearchRequest;
import com.cema.activity.domain.search.SearchResponse;
import com.cema.activity.entities.CemaWeighing;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.mapping.ActivityMapper;
import com.cema.activity.services.authorization.AuthorizationService;
import com.cema.activity.services.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WeighingSearchHandler implements ActivityHandler<SearchRequest, SearchResponse<Weighing>> {

    private final DatabaseService databaseService;
    private final ActivityMapper<Weighing, CemaWeighing> weighingMapper;
    private final AuthorizationService authorizationService;

    public WeighingSearchHandler(DatabaseService databaseService, ActivityMapper<Weighing, CemaWeighing> weighingMapper,
                                 AuthorizationService authorizationService) {
        this.databaseService = databaseService;
        this.weighingMapper = weighingMapper;
        this.authorizationService = authorizationService;
    }

    @Override
    public SearchResponse<Weighing> handle(SearchRequest activity) {
        Weighing weighing = (Weighing) activity.getActivity();
        log.info("Handling deleting of weighing activity {}.", weighing.getId());

        if (!authorizationService.isAdmin()) {
            weighing.setEstablishmentCuig(authorizationService.getCurrentUserCuig());
        }

        Page<CemaWeighing> cemaWeighingPage = databaseService.searchWeightings(weighing, activity.getPage(), activity.getSize());
        List<CemaWeighing> cemaWeightings = cemaWeighingPage.getContent();
        List<Weighing> weightings = cemaWeightings.stream()
                .map(weighingMapper::mapEntityToDomain)
                .collect(Collectors.toList());

        return SearchResponse.<Weighing>builder()
                .activities(weightings)
                .currentPage(cemaWeighingPage.getNumber())
                .totalElements(cemaWeighingPage.getNumberOfElements())
                .totalPages(cemaWeighingPage.getTotalPages())
                .build();
    }
}
