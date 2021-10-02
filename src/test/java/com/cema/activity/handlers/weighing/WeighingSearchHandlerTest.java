package com.cema.activity.handlers.weighing;

import com.cema.activity.domain.Weighing;
import com.cema.activity.domain.search.SearchRequest;
import com.cema.activity.domain.search.SearchResponse;
import com.cema.activity.entities.CemaWeighing;
import com.cema.activity.handlers.weighing.WeighingSearchHandler;
import com.cema.activity.mapping.ActivityMapper;
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


class WeighingSearchHandlerTest {

    @Mock
    private DatabaseService databaseService;
    @Mock
    private ActivityMapper<Weighing, CemaWeighing> weighingMapper;
    @Mock
    private AuthorizationService authorizationService;

    private WeighingSearchHandler weighingSearchHandler;

    private String cuig = "321";

    @BeforeEach
    public void setUp() {
        openMocks(this);
        when(authorizationService.isOnTheSameEstablishment(cuig)).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn(cuig);
        weighingSearchHandler = new WeighingSearchHandler(databaseService, weighingMapper, authorizationService);
    }

    @Test
    public void handleShouldReturnSearchResultsFromParameters() {
        int size = 2;
        int page = 0;
        Weighing weighing = Weighing.builder().build();
        SearchRequest searchRequest = SearchRequest.builder()
                .size(size)
                .page(page)
                .activity(weighing)
                .build();
        CemaWeighing cemaWeighing = new CemaWeighing();
        List<CemaWeighing> cemaWeighingList = Collections.singletonList(cemaWeighing);
        Page<CemaWeighing> cemaWeighingPage = mock(Page.class);
        when(cemaWeighingPage.getContent()).thenReturn(cemaWeighingList);
        int totalPages = 3;
        int currentPage = 0;
        int totalElements = 5;
        when(cemaWeighingPage.getTotalPages()).thenReturn(totalPages);
        when(cemaWeighingPage.getNumberOfElements()).thenReturn(totalElements);
        when(cemaWeighingPage.getNumber()).thenReturn(currentPage);
        when(databaseService.searchWeightings(weighing, page, size)).thenReturn(cemaWeighingPage);

        Weighing mappedWeighing = Weighing.builder().build();
        when(weighingMapper.mapEntityToDomain(cemaWeighing)).thenReturn(mappedWeighing);

        SearchResponse result = weighingSearchHandler.handle(searchRequest);

        assertThat(result.getCurrentPage(), is(currentPage));
        assertThat(result.getTotalElements(), is(totalElements));
        assertThat(result.getTotalPages(), is(totalPages));
        List<Weighing> weighings = result.getActivities();

        assertTrue(weighings.contains(mappedWeighing));

    }

}