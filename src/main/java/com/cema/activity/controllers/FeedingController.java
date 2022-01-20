package com.cema.activity.controllers;

import com.cema.activity.controllers.helpers.ActivityHelper;
import com.cema.activity.domain.Feeding;
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
@Api(produces = "application/json", value = "Allows interaction with the feeding database. V1")
@Validated
@Slf4j
public class FeedingController {

    private static final String BASE_URL = "/feedings";
    private static final String HANDLER_TYPE = "feeding";
    private final ActivityHelper activityHelper;

    public FeedingController(ActivityHelper activityHelper) {
        this.activityHelper = activityHelper;
    }

    @ApiOperation(value = "Retrieve a feeding by feeding id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found Feeding"),
            @ApiResponse(code = 401, message = "You are not allowed to register this feeding"),
            @ApiResponse(code = 404, message = "Feeding not found")
    })
    @GetMapping(value = BASE_URL + "/{activity_id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Feeding> getFeedingById(
            @ApiParam(
                    value = "The id of the feeding you are looking for.",
                    example = "123")
            @PathVariable("activity_id") String activityId,
            @ApiParam(
                    value = "The cuig of the establishment of the feeding. Only used if you are admin.",
                    example = "312")
            @RequestParam(value = "cuig", required = false) String cuig) {
        log.info("Request for get Feeding received.");

        Feeding result = (Feeding) activityHelper.get(activityId, HANDLER_TYPE, cuig);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Register a new feeding to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Feeding created successfully"),
            @ApiResponse(code = 401, message = "You are not allowed to register this feeding"),
            @ApiResponse(code = 400, message = "Incorrect feeding request body.")
    })
    @PostMapping(value = BASE_URL, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Feeding> registerFeeding(
            @ApiParam(
                    value = "Feeding data to be inserted.")
            @RequestBody @Valid Feeding feeding) {
        log.info("Request for register Feeding received.");

        Feeding result = (Feeding) activityHelper.register(feeding);

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
        log.info("Request for delete Feeding received.");

        activityHelper.delete(activityId, HANDLER_TYPE);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Modifies an existent feeding")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Feeding modified successfully"),
            @ApiResponse(code = 401, message = "You are not allowed to register this feeding"),
            @ApiResponse(code = 404, message = "The feeding you were trying to modify doesn't exists")
    })
    @PutMapping(value = BASE_URL + "/{activity_id}", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Feeding> updateFeeding(
            @ApiParam(
                    value = "The id for the activity we want to update.",
                    example = "b000bba4-229e-4b59-8548-1c26508e459c")
            @PathVariable("activity_id") String activityId,
            @ApiParam(
                    value = "The feeding data we are modifying")
            @RequestBody Feeding feeding) {
        log.info("Request for update Feeding received.");

        feeding.setType(HANDLER_TYPE);

        Feeding updated = (Feeding) activityHelper.update(activityId, feeding);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve a list of feedings matching the sent data", response = Feeding.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found feedings", responseHeaders = {
                    @ResponseHeader(name = "total-elements", response = String.class, description = "Total number of search results"),
                    @ResponseHeader(name = "total-pages", response = String.class, description = "Total number of pages to navigate"),
                    @ResponseHeader(name = "current-page", response = String.class, description = "The page being returned, zero indexed")
            })
    })
    @PostMapping(value = BASE_URL + "/search", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Feeding>> searchWeightings(
            @ApiParam(
                    value = "The page you want to retrieve.",
                    example = "1")
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @ApiParam(
                    value = "The maximum number of cows to return per page.",
                    example = "10")
            @RequestParam(value = "size", required = false, defaultValue = "3") int size,
            @ApiParam(
                    value = "The feeding data we are searching")
            @RequestBody Feeding feeding) {
        log.info("Request for search Feeding received.");

        feeding.setType(HANDLER_TYPE);
        SearchResponse<Feeding> searchResponse = activityHelper.search(feeding, page, size);

        List<Feeding> weightings = searchResponse.getActivities();

        return ResponseEntity.ok().headers(activityHelper.buildHeaders(searchResponse)).body(weightings);
    }
}
