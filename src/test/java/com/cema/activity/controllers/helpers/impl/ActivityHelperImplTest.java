package com.cema.activity.controllers.helpers.impl;

import com.cema.activity.constants.HandlerNames;
import com.cema.activity.domain.Activity;
import com.cema.activity.domain.search.SearchRequest;
import com.cema.activity.domain.search.SearchResponse;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.handlers.HandlerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class ActivityHelperImplTest {

    @Captor
    public ArgumentCaptor<Activity> activityArgumentCaptor;

    @Captor
    public ArgumentCaptor<SearchRequest> searchRequestArgumentCaptor;

    @Mock
    private HandlerFactory handlerFactory;

    private ActivityHelperImpl activityHelper;

    private String type = "type";

    @BeforeEach
    public void setUp() {
        openMocks(this);
        activityHelper = new ActivityHelperImpl(handlerFactory);
    }

    @Test
    public void registerShouldGetHandlerAndPassTheActivity() {
        Activity activity = new Activity();
        activity.setType(type);
        activity.setId(UUID.randomUUID());

        ActivityHandler<Activity, Activity> activityHandler = Mockito.mock(ActivityHandler.class);
        when(handlerFactory.getHandler(type + HandlerNames.REGISTER_HANDLER)).thenReturn(activityHandler);
        activityHelper.register(activity);

        verify(activityHandler).handle(activityArgumentCaptor.capture());

        assertThat(activityArgumentCaptor.getValue(), is(activity));
        assertThat(activity.getId(), is(nullValue()));
    }

    @Test
    public void deleteShouldGetHandlerAndPassTheActivity(){
        String uuid = "1fbb888a-0408-47b1-8c07-a0b1dd685d01";

        ActivityHandler<String, Activity> activityHandler = Mockito.mock(ActivityHandler.class);

        when(handlerFactory.getHandler(type + HandlerNames.DELETE_HANDLER)).thenReturn(activityHandler);

        activityHelper.delete(uuid, type);

        verify(activityHandler).handle(uuid);
    }

    @Test
    public void updateShouldGetHandlerAndPassTheActivity(){
        Activity activity = new Activity();
        activity.setType(type);
        String uuid = "1fbb888a-0408-47b1-8c07-a0b1dd685d01";

        Activity resultActivity = new Activity();
        ActivityHandler<Activity, Activity> activityHandler = Mockito.mock(ActivityHandler.class);
        when(activityHandler.handle(activity)).thenReturn(resultActivity);
        when(handlerFactory.getHandler(type + HandlerNames.UPDATE_HANDLER)).thenReturn(activityHandler);

        Activity result = activityHelper.update(uuid, activity);

        assertThat(activity.getId().toString(), is(uuid));
        assertThat(result, is(resultActivity));
    }

    @Test
    public void getShouldGetHandlerAndPassTheActivity(){
        String uuid = "1fbb888a-0408-47b1-8c07-a0b1dd685d01";
        String cuig = "321";

        ActivityHandler<Activity, Activity> activityHandler = Mockito.mock(ActivityHandler.class);
        Activity resultActivity = new Activity();
        when(handlerFactory.getHandler(type + HandlerNames.GET_HANDLER)).thenReturn(activityHandler);

        when(activityHandler.handle(activityArgumentCaptor.capture())).thenReturn(resultActivity);
        Activity result = activityHelper.get(uuid, type, cuig);


        Activity activityToSearch = activityArgumentCaptor.getValue();

        assertThat(activityToSearch.getEstablishmentCuig(), is(cuig));
        assertThat(activityToSearch.getId().toString(), is(uuid));
        assertThat(result, is(resultActivity));
    }

    @Test
    public void searchShouldGetHandlerAndPassTheActivity(){
        Activity activity = new Activity();
        activity.setType(type);
        int page = 0;
        int size = 2;

        ActivityHandler<SearchRequest, SearchResponse> activityHandler = Mockito.mock(ActivityHandler.class);
        SearchResponse searchResponse = SearchResponse.builder().build();

        when(handlerFactory.getHandler(type + HandlerNames.SEARCH_HANDLER)).thenReturn(activityHandler);
        when(activityHandler.handle(searchRequestArgumentCaptor.capture())).thenReturn(searchResponse);

        SearchResponse resultSearchResponse = activityHelper.search(activity, page, size);

        SearchRequest resultSearchRequest = searchRequestArgumentCaptor.getValue();

        assertThat(resultSearchRequest.getActivity(), is(activity));
        assertThat(resultSearchRequest.getPage(), is(page));
        assertThat(resultSearchRequest.getSize(), is(size));
        assertThat(resultSearchResponse, is(searchResponse));
    }
}