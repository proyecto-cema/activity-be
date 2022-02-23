package com.cema.activity.controllers;

import com.cema.activity.constants.Activities;
import com.cema.activity.controllers.helpers.ActivityHelper;
import com.cema.activity.domain.Activity;
import com.cema.activity.domain.Feeding;
import com.cema.activity.domain.Inoculation;
import com.cema.activity.domain.Movement;
import com.cema.activity.domain.Ultrasound;
import com.cema.activity.domain.Weighing;
import com.cema.activity.domain.search.SearchResponse;
import com.cema.activity.services.authorization.AuthorizationService;
import com.cema.activity.services.notification.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private final NotificationService notificationService;
    private final AuthorizationService authorizationService;

    public ActivityController(ActivityHelper activityHelper, NotificationService notificationService,
                              AuthorizationService authorizationService) {
        this.activityHelper = activityHelper;
        this.notificationService = notificationService;
        this.authorizationService = authorizationService;
    }

    @ApiOperation(value = "Launch notifications")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Notifications Sent")
    })
    @GetMapping(value = BASE_URL + "/notifications/send", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> sendNotifications() {
        notificationService.notifyAllUsers();

        return ResponseEntity.ok().build();
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

        Feeding feeding = Feeding.builder()
                .establishmentCuig(activity.getEstablishmentCuig())
                .executionDate(activity.getExecutionDate())
                .description(activity.getDescription())
                .name(activity.getName())
                .type(Activities.FEEDING_TYPE)
                .build();

        SearchResponse<Activity> inoculationResponse = activityHelper.search(inoculation, 0, 9999);
        SearchResponse<Activity> weighingResponse = activityHelper.search(weighing, 0, 9999);
        SearchResponse<Activity> ultrasoundResponse = activityHelper.search(ultrasound, 0, 9999);
        SearchResponse<Activity> movementResponse = activityHelper.search(movement, 0, 9999);
        SearchResponse<Activity> feedingResponse = activityHelper.search(feeding, 0, 9999);

        activities.addAll(inoculationResponse.getActivities());
        activities.addAll(weighingResponse.getActivities());
        activities.addAll(ultrasoundResponse.getActivities());
        activities.addAll(movementResponse.getActivities());
        activities.addAll(feedingResponse.getActivities());

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

    @ApiOperation(value = "Retrieve a list of activities matching the sent data", response = Activity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found activities"),
            @ApiResponse(code = 401, message = "You are not allowed to search these activities")
    })
    @GetMapping(value = BASE_URL + "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Activity>> getActivities(
            @ApiParam(
                    value = "The max number of days in the future to return. Unlimited if not sent",
                    example = "5")
            @RequestParam(value = "futureLimit", required = false, defaultValue = "-1") int futureLimit,
            @ApiParam(
                    value = "The max number of days in the past to return. Unlimited if not sent",
                    example = "5")
            @RequestParam(value = "pastLimit", required = false, defaultValue = "-1") int pastLimit,
            @ApiParam(
                    value = "The cuig of the activity you are looking for", example = "321")
            @RequestParam(value = "cuig", required = false) String cuig,
            @ApiParam(
                    value = "The cuig of the activity you are looking for", example = "2021-11-27T22:43:32.124Z")
            @RequestParam(value = "executionDate", required = false) Date executionDate,
            @ApiParam(
                    value = "The description of the activity you are looking for", example = "Actividad realizada en invierno")
            @RequestParam(value = "description", required = false) String description,
            @ApiParam(
                    value = "The name of the activity you are looking for", example = "Actividad 1")
            @RequestParam(value = "name", required = false) String name) {

        List<Activity> activities = new ArrayList<>();

        if (!authorizationService.isAdmin()) {
            cuig = authorizationService.getCurrentUserCuig();
        }

        Inoculation inoculation = Inoculation.builder()
                .establishmentCuig(cuig)
                .executionDate(executionDate)
                .description(description)
                .name(name)
                .type(Activities.INOCULATION_TYPE)
                .build();

        Weighing weighing = Weighing.builder()
                .establishmentCuig(cuig)
                .executionDate(executionDate)
                .description(description)
                .name(name)
                .type(Activities.WEIGHING_TYPE)
                .build();

        Ultrasound ultrasound = Ultrasound.builder()
                .establishmentCuig(cuig)
                .executionDate(executionDate)
                .description(description)
                .name(name)
                .type(Activities.ULTRASOUND_TYPE)
                .build();

        Movement movement = Movement.builder()
                .establishmentCuig(cuig)
                .executionDate(executionDate)
                .description(description)
                .name(name)
                .type(Activities.MOVEMENT_TYPE)
                .build();

        Feeding feeding = Feeding.builder()
                .establishmentCuig(cuig)
                .executionDate(executionDate)
                .description(description)
                .name(name)
                .type(Activities.FEEDING_TYPE)
                .build();

        SearchResponse<Activity> inoculationResponse = activityHelper.search(inoculation, 0, 9999);
        SearchResponse<Activity> weighingResponse = activityHelper.search(weighing, 0, 9999);
        SearchResponse<Activity> ultrasoundResponse = activityHelper.search(ultrasound, 0, 9999);
        SearchResponse<Activity> movementResponse = activityHelper.search(movement, 0, 9999);
        SearchResponse<Activity> feedingResponse = activityHelper.search(feeding, 0, 9999);

        activities.addAll(inoculationResponse.getActivities());
        activities.addAll(weighingResponse.getActivities());
        activities.addAll(ultrasoundResponse.getActivities());
        activities.addAll(movementResponse.getActivities());
        activities.addAll(feedingResponse.getActivities());

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
