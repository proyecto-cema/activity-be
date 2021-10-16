package com.cema.activity.controllers;

import com.cema.activity.controllers.helpers.ActivityHelper;
import com.cema.activity.domain.Weighing;
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
@Api(produces = "application/json", value = "Allows interaction with the weighing database. V1")
@Validated
@Slf4j
public class WeighingController {

    private static final String BASE_URL = "/weightings";
    private static final String HANDLER_TYPE = "weighing";
    private final ActivityHelper activityHelper;

    public WeighingController(ActivityHelper activityHelper) {
        this.activityHelper = activityHelper;
    }

    @ApiOperation(value = "Retrieve a weighing by weighing id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found Weighing"),
            @ApiResponse(code = 404, message = "Weighing not found")
    })
    @GetMapping(value = BASE_URL + "/{activity_id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Weighing> getWeighingById(
            @ApiParam(
                    value = "The id of the weighing you are looking for.",
                    example = "123")
            @PathVariable("activity_id") String activityId,
            @ApiParam(
                    value = "The cuig of the establishment of the weighing. Only used if you are admin.",
                    example = "312")
            @RequestParam(value = "cuig", required = false) String cuig) {
        log.info("Request for get Weighing received.");

        Weighing result = (Weighing) activityHelper.get(activityId, HANDLER_TYPE, cuig);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Register a new weighing to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Weighing created successfully"),
            @ApiResponse(code = 401, message = "You are not allowed to register this weighing"),
            @ApiResponse(code = 400, message = "Incorrect weighing request body.")
    })
    @PostMapping(value = BASE_URL, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Weighing> registerWeighing(
            @ApiParam(
                    value = "Weighing data to be inserted.")
            @RequestBody @Valid Weighing weighing) {
        log.info("Request for register Weighing received.");

        Weighing result = (Weighing) activityHelper.register(weighing);

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
        log.info("Request for delete Weighing received.");

        activityHelper.delete(activityId, HANDLER_TYPE);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Modifies an existent weighing")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Weighing modified successfully"),
            @ApiResponse(code = 404, message = "The weighing you were trying to modify doesn't exists")
    })
    @PutMapping(value = BASE_URL + "/{activity_id}", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Weighing> updateWeighing(
            @ApiParam(
                    value = "The id for the activity we want to update.",
                    example = "b000bba4-229e-4b59-8548-1c26508e459c")
            @PathVariable("activity_id") String activityId,
            @ApiParam(
                    value = "The weighing data we are modifying")
            @RequestBody Weighing weighing) {
        log.info("Request for update Weighing received.");

        weighing.setType(HANDLER_TYPE);

        Weighing updated = (Weighing) activityHelper.update(activityId, weighing);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve a list of weighings matching the sent data", response = Weighing.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found weighings", responseHeaders = {
                    @ResponseHeader(name = "total-elements", response = String.class, description = "Total number of search results"),
                    @ResponseHeader(name = "total-pages", response = String.class, description = "Total number of pages to navigate"),
                    @ResponseHeader(name = "current-page", response = String.class, description = "The page being returned, zero indexed")
            })
    })
    @PostMapping(value = BASE_URL + "/search", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Weighing>> searchWeightings(
            @ApiParam(
                    value = "The page you want to retrieve.",
                    example = "1")
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @ApiParam(
                    value = "The maximum number of cows to return per page.",
                    example = "10")
            @RequestParam(value = "size", required = false, defaultValue = "3") int size,
            @ApiParam(
                    value = "The weighing data we are searching")
            @RequestBody Weighing weighing) {
        log.info("Request for search Weighing received.");

        weighing.setType(HANDLER_TYPE);
        SearchResponse<Weighing> searchResponse = activityHelper.search(weighing, page, size);

        List<Weighing> weightings = searchResponse.getActivities();

        return ResponseEntity.ok().headers(activityHelper.buildHeaders(searchResponse)).body(weightings);
    }
}
