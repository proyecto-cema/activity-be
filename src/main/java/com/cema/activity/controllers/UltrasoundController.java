package com.cema.activity.controllers;

import com.cema.activity.controllers.helpers.ActivityHelper;
import com.cema.activity.domain.Ultrasound;
import com.cema.activity.domain.search.SearchResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Allows interaction with the ultrasound database. V1")
@Validated
@Slf4j
public class UltrasoundController {

    private static final String BASE_URL = "/ultrasounds";
    private static final String HANDLER_TYPE = "ultrasound";
    private final ActivityHelper activityHelper;

    public UltrasoundController(ActivityHelper activityHelper) {
        this.activityHelper = activityHelper;
    }

    @ApiOperation(value = "Retrieve a ultrasound by ultrasound id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found Ultrasound"),
            @ApiResponse(code = 401, message = "You are not allowed to register this ultrasound"),
            @ApiResponse(code = 404, message = "Ultrasound not found")
    })
    @GetMapping(value = BASE_URL + "/{activity_id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Ultrasound> getUltrasoundById(
            @ApiParam(
                    value = "The id of the ultrasound you are looking for.",
                    example = "123")
            @PathVariable("activity_id") String activityId,
            @ApiParam(
                    value = "The cuig of the establishment of the ultrasound. Only used if you are admin.",
                    example = "312")
            @RequestParam(value = "cuig", required = false) String cuig) {
        log.info("Request for get Ultrasound received.");

        Ultrasound result = (Ultrasound) activityHelper.get(activityId, HANDLER_TYPE, cuig);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Register a new ultrasound to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Ultrasound created successfully"),
            @ApiResponse(code = 401, message = "You are not allowed to register this ultrasound"),
            @ApiResponse(code = 400, message = "Incorrect ultrasound request body.")
    })
    @PostMapping(value = BASE_URL, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Ultrasound> registerUltrasound(
            @ApiParam(
                    value = "Ultrasound data to be inserted.")
            @RequestBody @Valid Ultrasound ultrasound) {
        log.info("Request for register Ultrasound received.");

        Ultrasound result = (Ultrasound) activityHelper.register(ultrasound);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Delete an existing activity by name")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Activity deleted successfully"),
            @ApiResponse(code = 404, message = "The activity you were trying to delete is not found"),
            @ApiResponse(code = 401, message = "You are not authorized to delete this activity")
    })
    @DeleteMapping(value = BASE_URL + "/{activity_id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> deleteActivity(
            @ApiParam(
                    value = "The id for the activity we want to delete.",
                    example = "123")
            @PathVariable("activity_id") String activityId) {
        log.info("Request for delete Ultrasound received.");

        activityHelper.delete(activityId, HANDLER_TYPE);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Modifies an existent ultrasound")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ultrasound modified successfully"),
            @ApiResponse(code = 401, message = "You are not allowed to register this ultrasound"),
            @ApiResponse(code = 404, message = "The ultrasound you were trying to modify doesn't exists")
    })
    @PutMapping(value = BASE_URL + "/{activity_id}", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Ultrasound> updateUltrasound(
            @ApiParam(
                    value = "The id for the activity we want to update.",
                    example = "b000bba4-229e-4b59-8548-1c26508e459c")
            @PathVariable("activity_id") String activityId,
            @ApiParam(
                    value = "The ultrasound data we are modifying")
            @RequestBody Ultrasound ultrasound) {
        log.info("Request for update Ultrasound received.");

        ultrasound.setType(HANDLER_TYPE);

        Ultrasound updated = (Ultrasound) activityHelper.update(activityId, ultrasound);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve a list of ultrasounds matching the sent data", response = Ultrasound.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found ultrasounds", responseHeaders = {
                    @ResponseHeader(name = "total-elements", response = String.class, description = "Total number of search results"),
                    @ResponseHeader(name = "total-pages", response = String.class, description = "Total number of pages to navigate"),
                    @ResponseHeader(name = "current-page", response = String.class, description = "The page being returned, zero indexed")
            })
    })
    @PostMapping(value = BASE_URL + "/search", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Ultrasound>> searchWeightings(
            @ApiParam(
                    value = "The page you want to retrieve.",
                    example = "1")
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @ApiParam(
                    value = "The maximum number of cows to return per page.",
                    example = "10")
            @RequestParam(value = "size", required = false, defaultValue = "3") int size,
            @ApiParam(
                    value = "The ultrasound data we are searching")
            @RequestBody Ultrasound ultrasound) {
        log.info("Request for search Ultrasound received.");

        ultrasound.setType(HANDLER_TYPE);
        SearchResponse<Ultrasound> searchResponse = activityHelper.search(ultrasound, page, size);

        List<Ultrasound> weightings = searchResponse.getActivities();

        return ResponseEntity.ok().headers(activityHelper.buildHeaders(searchResponse)).body(weightings);
    }
}
