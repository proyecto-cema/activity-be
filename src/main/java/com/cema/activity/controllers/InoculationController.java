package com.cema.activity.controllers;

import com.cema.activity.controllers.helpers.ActivityHelper;
import com.cema.activity.domain.Feeding;
import com.cema.activity.domain.Inoculation;
import com.cema.activity.domain.search.SearchResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import lombok.extern.slf4j.Slf4j;
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
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Allows interaction with the inoculation database. V1")
@Validated
@Slf4j
public class InoculationController {

    private static final String BASE_URL = "/inoculations";
    private static final String HANDLER_TYPE = "inoculation";
    private final ActivityHelper activityHelper;

    public InoculationController(ActivityHelper activityHelper) {
        this.activityHelper = activityHelper;
    }

    @ApiOperation(value = "Retrieve an inoculation by inoculation id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found Inoculation"),
            @ApiResponse(code = 401, message = "You are not allowed to retrieve this inoculation"),
            @ApiResponse(code = 404, message = "Inoculation not found")
    })
    @GetMapping(value = BASE_URL + "/{activity_id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Inoculation> getInoculationById(
            @ApiParam(
                    value = "The id of the inoculation you are looking for.",
                    example = "123")
            @PathVariable("activity_id") String activityId,
            @ApiParam(
                    value = "The cuig of the establishment of the inoculation. Only used if you are admin.",
                    example = "312")
            @RequestParam(value = "cuig", required = false) String cuig) {
        log.info("Request for get Inoculation received.");

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
        log.info("Request for register Inoculation received.");

        Inoculation result = (Inoculation) activityHelper.register(inoculation);

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
        log.info("Request for delete Inoculation received.");

        activityHelper.delete(activityId, HANDLER_TYPE);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Modifies an existent inoculation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Inoculation modified successfully"),
            @ApiResponse(code = 401, message = "You are not allowed to update this inoculation"),
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
            @RequestBody Inoculation inoculation) {
        log.info("Request for update Inoculation received.");

        inoculation.setType(HANDLER_TYPE);

        Inoculation updated = (Inoculation) activityHelper.update(activityId, inoculation);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve a list of inoculations matching the sent data", response = Inoculation.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found inoculations", responseHeaders = {
                    @ResponseHeader(name = "total-elements", response = String.class, description = "Total number of search results"),
                    @ResponseHeader(name = "total-pages", response = String.class, description = "Total number of pages to navigate"),
                    @ResponseHeader(name = "current-page", response = String.class, description = "The page being returned, zero indexed")
            }),
            @ApiResponse(code = 401, message = "You are not allowed to search these inoculations")
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
        log.info("Request for search Inoculation received.");

        inoculation.setType(HANDLER_TYPE);
        SearchResponse<Inoculation> searchResponse = activityHelper.search(inoculation, page, size);

        List<Inoculation> inoculations = searchResponse.getActivities();

        return ResponseEntity.ok().headers(activityHelper.buildHeaders(searchResponse)).body(inoculations);
    }

    @ApiOperation(value = "Retrieve a list of inoculations matching the sent data", response = Inoculation.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found inoculations", responseHeaders = {
                    @ResponseHeader(name = "total-elements", response = String.class, description = "Total number of search results"),
                    @ResponseHeader(name = "total-pages", response = String.class, description = "Total number of pages to navigate"),
                    @ResponseHeader(name = "current-page", response = String.class, description = "The page being returned, zero indexed")
            }),
            @ApiResponse(code = 401, message = "You are not allowed to search these inoculations")
    })
    @GetMapping(value = BASE_URL + "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Inoculation>> getInoculations(
            @ApiParam(
                    value = "The page you want to retrieve.",
                    example = "1")
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @ApiParam(
                    value = "The maximum number of cows to return per page.",
                    example = "10")
            @RequestParam(value = "size", required = false, defaultValue = "3") int size,
            @ApiParam(
                    value = "The name of the activity",
                    example = "Actividad")
            @RequestParam(value = "name", required = false) String name,
            @ApiParam(
                    value = "The description of the activity",
                    example = "Actividad")
            @RequestParam(value = "description", required = false) String description,
            @ApiParam(
                    value = "The cuig of the activity",
                    example = "Actividad")
            @RequestParam(value = "cuig", required = false) String cuig,
            @ApiParam(
                    value = "The worker name of the activity",
                    example = "Actividad")
            @RequestParam(value = "worker", required = false) String worker,
            @ApiParam(
                    value = "The tag of the bovine innoculated",
                    example = "1234")
            @RequestParam(value = "tag", required = false) String tag,
            @ApiParam(
                    value = "The dose to be innoculated",
                    example = "1234")
            @RequestParam(value = "dose", required = false) Long dose,
            @ApiParam(
                    value = "The brand to be innoculated",
                    example = "MERCK")
            @RequestParam(value = "brand", required = false) String brand,
            @ApiParam(
                    value = "The drug to be innoculated",
                    example = "fenbendazole")
            @RequestParam(value = "drug", required = false) String drug,
            @ApiParam(
                    value = "The product to be innoculated",
                    example = "safe-guard")
            @RequestParam(value = "product", required = false) String product,
            @ApiParam(
                    value = "The batchName of the batch innoculated",
                    example = "the_batch")
            @RequestParam(value = "batchName", required = false) String batchName) {
        
        log.info("Request for search Inoculation received.");

        Inoculation inoculation = Inoculation.builder()
                .name(name)
                .description(description)
                .establishmentCuig(cuig)
                .workerUserName(worker)
                .type(HANDLER_TYPE)
                .bovineTag(tag)
                .dose(dose)
                .brand(brand)
                .drug(drug)
                .product(product)
                .batchName(batchName)
                .build();

        SearchResponse<Inoculation> searchResponse = activityHelper.search(inoculation, page, size);

        List<Inoculation> inoculations = searchResponse.getActivities();

        return ResponseEntity.ok().headers(activityHelper.buildHeaders(searchResponse)).body(inoculations);
    }

}
