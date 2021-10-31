package com.cema.activity.handlers.ultrasound;

import com.cema.activity.domain.Ultrasound;
import com.cema.activity.domain.search.SearchRequest;
import com.cema.activity.domain.search.SearchResponse;
import com.cema.activity.entities.CemaUltrasound;
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
public class UltrasoundSearchHandler implements ActivityHandler<SearchRequest, SearchResponse<Ultrasound>> {

    private final DatabaseService databaseService;
    private final ActivityMapper<Ultrasound, CemaUltrasound> ultrasoundMapper;
    private final AuthorizationService authorizationService;

    public UltrasoundSearchHandler(DatabaseService databaseService, ActivityMapper<Ultrasound, CemaUltrasound> ultrasoundMapper,
                                   AuthorizationService authorizationService) {
        this.databaseService = databaseService;
        this.ultrasoundMapper = ultrasoundMapper;
        this.authorizationService = authorizationService;
    }

    @Override
    public SearchResponse<Ultrasound> handle(SearchRequest activity) {
        Ultrasound ultrasound = (Ultrasound) activity.getActivity();
        log.info("Handling deleting of ultrasound activity {}.", ultrasound.getId());

        if (!authorizationService.isAdmin()) {
            ultrasound.setEstablishmentCuig(authorizationService.getCurrentUserCuig());
        }

        Page<CemaUltrasound> cemaUltrasoundPage = databaseService.searchUltrasounds(ultrasound, activity.getPage(), activity.getSize());
        List<CemaUltrasound> cemaWeightings = cemaUltrasoundPage.getContent();
        List<Ultrasound> weightings = cemaWeightings.stream()
                .map(ultrasoundMapper::mapEntityToDomain)
                .collect(Collectors.toList());

        return SearchResponse.<Ultrasound>builder()
                .activities(weightings)
                .currentPage(cemaUltrasoundPage.getNumber())
                .totalElements(cemaUltrasoundPage.getNumberOfElements())
                .totalPages(cemaUltrasoundPage.getTotalPages())
                .build();
    }
}
