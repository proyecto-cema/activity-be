package com.cema.activity.handlers.inoculation;

import com.cema.activity.domain.Inoculation;
import com.cema.activity.domain.search.SearchRequest;
import com.cema.activity.domain.search.SearchResponse;
import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.mapping.Mapper;
import com.cema.activity.services.authorization.AuthorizationService;
import com.cema.activity.services.database.DatabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.wildfly.common.Assert.assertTrue;


class InoculationSearchHandlerTest {

    @Mock
    private DatabaseService databaseService;
    @Mock
    private Mapper<Inoculation, CemaInoculation> inoculationMapper;
    @Mock
    private AuthorizationService authorizationService;

    private InoculationSearchHandler inoculationSearchHandler;

    private String cuig = "321";

    @BeforeEach
    public void setUp() {
        openMocks(this);
        when(authorizationService.isOnTheSameEstablishment(cuig)).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn(cuig);
        inoculationSearchHandler = new InoculationSearchHandler(databaseService, inoculationMapper, authorizationService);
    }

    @Test
    public void handleShouldReturnSearchResultsFromParameters() {
        int size = 2;
        int page = 0;
        Inoculation inoculation = Inoculation.builder().build();
        SearchRequest searchRequest = SearchRequest.builder()
                .size(size)
                .page(page)
                .activity(inoculation)
                .build();
        CemaInoculation cemaInoculation = new CemaInoculation();
        List<CemaInoculation> cemaInoculationList = Collections.singletonList(cemaInoculation);
        Page<CemaInoculation> cemaInoculationPage = mock(Page.class);
        when(cemaInoculationPage.getContent()).thenReturn(cemaInoculationList);
        int totalPages = 3;
        int currentPage = 0;
        int totalElements = 5;
        when(cemaInoculationPage.getTotalPages()).thenReturn(totalPages);
        when(cemaInoculationPage.getNumberOfElements()).thenReturn(totalElements);
        when(cemaInoculationPage.getNumber()).thenReturn(currentPage);
        when(databaseService.searchInoculations(inoculation, page, size)).thenReturn(cemaInoculationPage);

        Inoculation mappedInoculation = Inoculation.builder().build();
        when(inoculationMapper.mapEntityToDomain(cemaInoculation)).thenReturn(mappedInoculation);

        SearchResponse result = inoculationSearchHandler.handle(searchRequest);

        assertThat(result.getCurrentPage(), is(currentPage));
        assertThat(result.getTotalElements(), is(totalElements));
        assertThat(result.getTotalPages(), is(totalPages));
        List<Inoculation> inoculations = result.getActivities();

        assertTrue(inoculations.contains(mappedInoculation));

    }

}