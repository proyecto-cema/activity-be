package com.cema.activity.controllers;

import com.cema.activity.controllers.helpers.ActivityHelper;
import com.cema.activity.domain.Inoculation;
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

class InoculationControllerTest {

    @Mock
    private ActivityHelper activityHelper;

    private InoculationController inoculationController;

    @BeforeEach
    public void setUp(){
        openMocks(this);
        inoculationController = new InoculationController(activityHelper);
    }

    @Test
    public void getInoculationByIdShouldPassRequestToActivityHandler(){
        String activityId = "activityId";
        String cuig = "cuig";
        Inoculation inoculation = Inoculation.builder().build();

        when(activityHelper.get(activityId, "inoculation", cuig)).thenReturn(inoculation);

        ResponseEntity<Inoculation> result = inoculationController.getInoculationById(activityId, cuig);

        assertThat(result.getBody(), is(inoculation));
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void registerInoculationShouldPassRequestToActivityHandler(){
        Inoculation inoculation = Inoculation.builder().build();
        Inoculation registeredInoculation = Inoculation.builder().build();

        when(activityHelper.register(inoculation)).thenReturn(registeredInoculation);

        ResponseEntity<Inoculation> result = inoculationController.registerInoculation(inoculation);

        assertThat(result.getBody(), is(registeredInoculation));
        assertThat(result.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void deleteActivityShouldPassRequestToActivityHandler(){
        String activityId = "activityId";

        ResponseEntity<String> result = inoculationController.deleteActivity(activityId);

        verify(activityHelper).delete(activityId, "inoculation");

        assertThat(result.getStatusCode(), is(HttpStatus.NO_CONTENT));
    }

    @Test
    public void updateInoculationShouldPassRequestToActivityHandler(){
        String activityId = "activityId";
        Inoculation inoculation = Inoculation.builder().build();
        Inoculation updated = Inoculation.builder().build();

        when(activityHelper.update(activityId, inoculation)).thenReturn(updated);

        ResponseEntity<Inoculation> result = inoculationController.updateInoculation(activityId, inoculation);

        assertThat(result.getBody(), is(updated));
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }

}