package com.cema.activity.controllers;

import com.cema.activity.controllers.helpers.ActivityHelper;
import com.cema.activity.domain.Activity;
import com.cema.activity.domain.Inoculation;
import com.cema.activity.domain.SearchResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Allows interaction with the inoculation database. V1")
@Validated
public class InoculationController {

    private static final String BASE_URL = "/inoculations";
    private static final String HANDLER_TYPE = "inoculation";
    private final ActivityHelper activityHelper;

    public InoculationController(ActivityHelper activityHelper) {
        this.activityHelper = activityHelper;
    }

    @ApiOperation(value = "Retrieve a batch by batch name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found Batch"),
            @ApiResponse(code = 404, message = "Batch not found")
    })
    @GetMapping(value = BASE_URL + "/{activity_id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Inoculation> lookUpInoculationById(
            @ApiParam(
                    value = "The id of the inoculation you are looking for.",
                    example = "123")
            @PathVariable("activity_id") String activityId,
            @ApiParam(
                    value = "The cuig of the establishment of the inoculation. If the user is not admin will be ignored.",
                    example = "312")
            @RequestParam(value = "cuig") String cuig) {

        Inoculation result = (Inoculation) activityHelper.get(activityId, HANDLER_TYPE, cuig);

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @ApiOperation(value = "Register a new inoculation to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Inoculation created successfully"),
            @ApiResponse(code = 401, message = "You are not allowed to register this inoculation"),
            @ApiResponse(code = 400, message = "Incorrect inoculation request body.")
    })
    @PostMapping(value = BASE_URL, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Inoculation> registerInoculation(
            @ApiParam(
                    value = "Inoculation data to be inserted.")
            @RequestBody @Valid Inoculation inoculation) {

        activityHelper.register(inoculation);

        return new ResponseEntity<>(inoculation, HttpStatus.CREATED);
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

        activityHelper.delete(activityId, HANDLER_TYPE);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Modifies an existent inoculation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Inoculation modified successfully"),
            @ApiResponse(code = 404, message = "The inoculation you were trying to modify doesn't exists")
    })
    @PutMapping(value = BASE_URL + "/{activity_id}", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Inoculation> updateInoculation(
            @ApiParam(
                    value = "The id for the activity we want to update.",
                    example = "b000bba4-229e-4b59-8548-1c26508e459c")
            @PathVariable("activity_id") String activityId,
            @ApiParam(
                    value = "The inoculation data we are modifying")
            @RequestBody Inoculation inoculation){

        inoculation.setType(HANDLER_TYPE);

        Inoculation updated = (Inoculation) activityHelper.update(activityId, inoculation);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve a list of inoculations matching the sent data", response = Inoculation.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found inoculations", responseHeaders = {
                    @ResponseHeader(name = "total-elements", response = String.class, description = "Total number of search results"),
                    @ResponseHeader(name = "total-pages", response = String.class, description = "Total number of pages to navigate"),
                    @ResponseHeader(name = "current-page", response = String.class, description = "The page being returned, zero indexed")
            })
    })
    @PostMapping(value = BASE_URL + "/search", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Inoculation>> searchInoculations(
            @ApiParam(
                    value = "The page you want to retrieve.",
                    example = "1")
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @ApiParam(
                    value = "The maximum number of cows to return per page.",
                    example = "10")
            @RequestParam(value = "size", required = false, defaultValue = "3") int size,
            @ApiParam(
                    value = "The inoculation data we are searching")
            @RequestBody Inoculation inoculation) {
        inoculation.setType(HANDLER_TYPE);
        SearchResponse<Inoculation> searchResponse = activityHelper.search(inoculation, page, size);

        List<Inoculation> inoculations = searchResponse.getActivities();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-elements", String.valueOf(searchResponse.getTotalElements()));
        responseHeaders.set("total-pages", String.valueOf(searchResponse.getTotalPages()));
        responseHeaders.set("current-page", String.valueOf(searchResponse.getCurrentPage()));

        return ResponseEntity.ok().headers(responseHeaders).body(inoculations);
    }

}
