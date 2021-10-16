package com.cema.activity.handlers.inoculation;

import com.cema.activity.domain.Inoculation;
import com.cema.activity.domain.search.SearchRequest;
import com.cema.activity.domain.search.SearchResponse;
import com.cema.activity.entities.CemaInoculation;
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
public class InoculationSearchHandler implements ActivityHandler<SearchRequest, SearchResponse<Inoculation>> {

    private final DatabaseService databaseService;
    private final ActivityMapper<Inoculation, CemaInoculation> inoculationMapper;
    private final AuthorizationService authorizationService;

    public InoculationSearchHandler(DatabaseService databaseService, ActivityMapper<Inoculation, CemaInoculation> inoculationMapper,
                                    AuthorizationService authorizationService) {
        this.databaseService = databaseService;
        this.inoculationMapper = inoculationMapper;
        this.authorizationService = authorizationService;
    }

    @Override
    public SearchResponse<Inoculation> handle(SearchRequest activity) {
        Inoculation inoculation = (Inoculation) activity.getActivity();
        log.info("Handling deleting of inoculation activity {}.", inoculation.getId());

        if (!authorizationService.isAdmin()) {
            inoculation.setEstablishmentCuig(authorizationService.getCurrentUserCuig());
        }

        Page<CemaInoculation> cemaInoculationPage = databaseService.searchInoculations(inoculation, activity.getPage(), activity.getSize());
        List<CemaInoculation> cemaInoculations = cemaInoculationPage.getContent();
        List<Inoculation> inoculations = cemaInoculations.stream()
                .map(inoculationMapper::mapEntityToDomain)
                .collect(Collectors.toList());

        return SearchResponse.<Inoculation>builder()
                .activities(inoculations)
                .currentPage(cemaInoculationPage.getNumber())
                .totalElements(cemaInoculationPage.getNumberOfElements())
                .totalPages(cemaInoculationPage.getTotalPages())
                .build();
    }
}
