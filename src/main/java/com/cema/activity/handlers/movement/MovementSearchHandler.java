package com.cema.activity.handlers.movement;

import com.cema.activity.domain.Movement;
import com.cema.activity.domain.search.SearchRequest;
import com.cema.activity.domain.search.SearchResponse;
import com.cema.activity.entities.CemaMovement;
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
public class MovementSearchHandler implements ActivityHandler<SearchRequest, SearchResponse<Movement>> {

    private final DatabaseService databaseService;
    private final Mapper<Movement, CemaMovement> movementMapper;
    private final AuthorizationService authorizationService;

    public MovementSearchHandler(DatabaseService databaseService, Mapper<Movement, CemaMovement> movementMapper,
                                 AuthorizationService authorizationService) {
        this.databaseService = databaseService;
        this.movementMapper = movementMapper;
        this.authorizationService = authorizationService;
    }

    @Override
    public SearchResponse<Movement> handle(SearchRequest activity) {
        Movement movement = (Movement) activity.getActivity();
        log.info("Handling search of movement activity {}.", movement.getId());

        if (!authorizationService.isAdmin()) {
            movement.setEstablishmentCuig(authorizationService.getCurrentUserCuig());
        }

        Page<CemaMovement> cemaMovementPage = databaseService.searchMovements(movement, activity.getPage(), activity.getSize());
        List<CemaMovement> cemaMovements = cemaMovementPage.getContent();
        List<Movement> movements = cemaMovements.stream()
                .map(movementMapper::mapEntityToDomain)
                .collect(Collectors.toList());

        return SearchResponse.<Movement>builder()
                .activities(movements)
                .currentPage(cemaMovementPage.getNumber())
                .totalElements(cemaMovementPage.getNumberOfElements())
                .totalPages(cemaMovementPage.getTotalPages())
                .build();
    }
}
