package com.cema.activity.controllers;

import com.cema.activity.controllers.helpers.ActivityHelper;
import com.cema.activity.domain.Weighing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class WeighingControllerTest {

    @Mock
    private ActivityHelper activityHelper;

    private WeighingController weighingController;

    @BeforeEach
    public void setUp(){
        openMocks(this);
        weighingController = new WeighingController(activityHelper);
    }

    @Test
    public void getWeighingByIdShouldPassRequestToActivityHandler(){
        String activityId = "activityId";
        String cuig = "cuig";
        Weighing weighing = Weighing.builder().build();

        when(activityHelper.get(activityId, "weighing", cuig)).thenReturn(weighing);

        ResponseEntity<Weighing> result = weighingController.getWeighingById(activityId, cuig);

        assertThat(result.getBody(), is(weighing));
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void registerWeighingShouldPassRequestToActivityHandler(){
        Weighing weighing = Weighing.builder().build();
        Weighing registeredWeighing = Weighing.builder().build();

        when(activityHelper.register(weighing)).thenReturn(registeredWeighing);

        ResponseEntity<Weighing> result = weighingController.registerWeighing(weighing);

        assertThat(result.getBody(), is(registeredWeighing));
        assertThat(result.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void deleteActivityShouldPassRequestToActivityHandler(){
        String activityId = "activityId";

        ResponseEntity<String> result = weighingController.deleteActivity(activityId);

        verify(activityHelper).delete(activityId, "weighing");

        assertThat(result.getStatusCode(), is(HttpStatus.NO_CONTENT));
    }

    @Test
    public void updateWeighingShouldPassRequestToActivityHandler(){
        String activityId = "activityId";
        Weighing weighing = Weighing.builder().build();
        Weighing updated = Weighing.builder().build();

        when(activityHelper.update(activityId, weighing)).thenReturn(updated);

        ResponseEntity<Weighing> result = weighingController.updateWeighing(activityId, weighing);

        assertThat(result.getBody(), is(updated));
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }

}