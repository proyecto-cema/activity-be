package com.cema.activity.controllers;

import com.cema.activity.constants.Activities;
import com.cema.activity.controllers.helpers.ActivityHelper;
import com.cema.activity.domain.Activity;
import com.cema.activity.domain.Inoculation;
import com.cema.activity.domain.Movement;
import com.cema.activity.domain.Ultrasound;
import com.cema.activity.domain.Weighing;
import com.cema.activity.domain.search.SearchResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Allows interaction with activities in general. V1")
@Validated
@Slf4j
public class ActivityController {

    private static final String BASE_URL = "/activities";

    private final ActivityHelper activityHelper;

    public ActivityController(ActivityHelper activityHelper) {
        this.activityHelper = activityHelper;
    }

    @ApiOperation(value = "Retrieve a list of activities matching the sent data", response = Activity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found activities"),
            @ApiResponse(code = 401, message = "You are not allowed to search these activities")
    })
    @PostMapping(value = BASE_URL + "/search", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Activity>> searchActivities(
            @ApiParam(
                    value = "The max number of days in the future to return. Unlimited if not sent",
                    example = "5")
            @RequestParam(value = "futureLimit", required = false, defaultValue = "-1") int futureLimit,
            @ApiParam(
                    value = "The max number of days in the past to return. Unlimited if not sent",
                    example = "5")
            @RequestParam(value = "pastLimit", required = false, defaultValue = "-1") int pastLimit,
            @ApiParam(
                    value = "The activity data we are searching")
            @RequestBody Activity activity) {
        log.info("Request for search Activity received.");

        List<Activity> activities = new ArrayList<>();

        Inoculation inoculation = Inoculation.builder()
                .establishmentCuig(activity.getEstablishmentCuig())
                .executionDate(activity.getExecutionDate())
                .description(activity.getDescription())
                .name(activity.getName())
                .type(Activities.INOCULATION_TYPE)
                .build();

        Weighing weighing = Weighing.builder()
                .establishmentCuig(activity.getEstablishmentCuig())
                .executionDate(activity.getExecutionDate())
                .description(activity.getDescription())
                .name(activity.getName())
                .type(Activities.WEIGHING_TYPE)
                .build();

        Ultrasound ultrasound = Ultrasound.builder()
                .establishmentCuig(activity.getEstablishmentCuig())
                .executionDate(activity.getExecutionDate())
                .description(activity.getDescription())
                .name(activity.getName())
                .type(Activities.ULTRASOUND_TYPE)
                .build();

        Movement movement = Movement.builder()
                .establishmentCuig(activity.getEstablishmentCuig())
                .executionDate(activity.getExecutionDate())
                .description(activity.getDescription())
                .name(activity.getName())
                .type(Activities.MOVEMENT_TYPE)
                .build();

        SearchResponse<Activity> inoculationResponse = activityHelper.search(inoculation, 0, 9999);
        SearchResponse<Activity> weighingResponse = activityHelper.search(weighing, 0, 9999);
        SearchResponse<Activity> ultrasoundResponse = activityHelper.search(ultrasound, 0, 9999);
        SearchResponse<Activity> movementResponse = activityHelper.search(movement, 0, 9999);

        activities.addAll(inoculationResponse.getActivities());
        activities.addAll(weighingResponse.getActivities());
        activities.addAll(ultrasoundResponse.getActivities());
        activities.addAll(movementResponse.getActivities());

        if (futureLimit != -1) {
            Calendar futureLimitCalendar = Calendar.getInstance();
            futureLimitCalendar.add(Calendar.DATE, futureLimit);
            activities = activities.stream().filter(activity1 -> activity1.getExecutionDate().before(futureLimitCalendar.getTime())).collect(Collectors.toList());
        }
        if (pastLimit != -1) {
            Calendar pastLimitCalendar = Calendar.getInstance();
            pastLimitCalendar.add(Calendar.DATE, -pastLimit);
            activities = activities.stream().filter(activity1 -> activity1.getExecutionDate().after(pastLimitCalendar.getTime())).collect(Collectors.toList());
        }

        Collections.sort(activities);

        return ResponseEntity.ok().body(activities);
    }
}
