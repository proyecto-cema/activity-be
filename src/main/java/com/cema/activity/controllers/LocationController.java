package com.cema.activity.controllers;

import com.cema.activity.constants.Messages;
import com.cema.activity.domain.Location;
import com.cema.activity.entities.CemaLocation;
import com.cema.activity.exceptions.AlreadyExistsException;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.mapping.impl.LocationMapper;
import com.cema.activity.repositories.LocationRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Allows interaction with the location database. V1")
@Validated
@Slf4j
public class LocationController {

    private static final String BASE_URL = "/locations/";

    private final AuthorizationService authorizationService;
    private final LocationMapper locationMapper;
    private final LocationRepository locationRepository;

    public LocationController(AuthorizationService authorizationService, LocationMapper locationMapper, LocationRepository locationRepository) {
        this.authorizationService = authorizationService;
        this.locationMapper = locationMapper;
        this.locationRepository = locationRepository;
    }

    @ApiOperation(value = "Retrieve a location by location name", response = Location.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found Location"),
            @ApiResponse(code = 404, message = "Location not found")
    })
    @GetMapping(value = BASE_URL + "{name}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Location> lookUpLocationByName(
            @ApiParam(
                    value = "The name of the location you are looking for.",
                    example = "Corral 5")
            @PathVariable("name") String name,
            @ApiParam(
                    value = "The cuig of the establishment of the location. If the user is not admin will be ignored.",
                    example = "321")
            @RequestParam(value = "cuig") String cuig) {

        if (!authorizationService.isAdmin()) {
            cuig = authorizationService.getCurrentUserCuig();
        }
        log.info("Request for location with name {} and cuig {}", name, cuig);
        CemaLocation cemaLocation = locationRepository.findCemaLocationByNameAndEstablishmentCuigIgnoreCase(name, cuig);
        if (cemaLocation == null) {
            throw new NotFoundException(String.format("Location with name %s doesn't exits", name));
        }
        Location location = locationMapper.mapEntityToDomain(cemaLocation);

        return new ResponseEntity<>(location, HttpStatus.OK);
    }

    @ApiOperation(value = "Register a new location to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Location created successfully"),
            @ApiResponse(code = 409, message = "The location you were trying to create already exists"),
            @ApiResponse(code = 401, message = "You are not allowed to register this location")
    })
    @PostMapping(value = BASE_URL, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> registerLocation(
            @ApiParam(
                    value = "Location data to be inserted.")
            @RequestBody @Valid Location location) {

        String cuig = location.getEstablishmentCuig();
        log.info("Request to register new location {}", location);
        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }
        CemaLocation cemaLocation = locationRepository.findCemaLocationByNameAndEstablishmentCuigIgnoreCase(location.getName(), cuig);
        if (cemaLocation != null) {
            log.info("Location already exists");
            throw new AlreadyExistsException(String.format("The location with name %s already exists", location.getName()));
        }

        cemaLocation = locationMapper.mapDomainToEntity(location);

        locationRepository.save(cemaLocation);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "Delete an existing location by name")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Location deleted successfully"),
            @ApiResponse(code = 404, message = "The location you were trying to reach is not found")
    })
    @DeleteMapping(value = BASE_URL + "{name}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> deleteLocation(
            @ApiParam(
                    value = "The name for the location we are looking for.",
                    example = "Corral 5")
            @PathVariable("name") String locationName,
            @ApiParam(
                    value = "The cuig of the establishment of the location. If the user is not admin will be ignored.",
                    example = "321")
            @RequestParam(value = "cuig") String cuig) {

        if (!authorizationService.isAdmin()) {
            cuig = authorizationService.getCurrentUserCuig();
        }
        log.info("Request to delete location with name {} and cuig {}", locationName, cuig);
        CemaLocation cemaLocation = locationRepository.findCemaLocationByNameAndEstablishmentCuigIgnoreCase(locationName, cuig);
        if (cemaLocation != null) {
            log.info("Location exists, deleting");
            locationRepository.delete(cemaLocation);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.info("Location Not found");
        throw new NotFoundException(String.format("Location %s doesn't exits", locationName));
    }

    @ApiOperation(value = "Modifies an existent Location")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Location modified successfully"),
            @ApiResponse(code = 404, message = "The location you were trying to modify doesn't exists")
    })
    @PutMapping(value = BASE_URL + "{name}", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Location> updateLocation(
            @ApiParam(
                    value = "The name of the location we are looking for.",
                    example = "Corral 5")
            @PathVariable("name") String name,
            @ApiParam(
                    value = "The cuig of the establishment of the location. If the user is not admin will be ignored.",
                    example = "321")
            @RequestParam(value = "cuig") String cuig,
            @ApiParam(
                    value = "The location data we are modifying")
            @RequestBody Location location) {

        if (!authorizationService.isAdmin()) {
            cuig = authorizationService.getCurrentUserCuig();
        }
        log.info("Request to modify location with name {} and cuig {}", name, cuig);
        CemaLocation cemaLocation = locationRepository.findCemaLocationByNameAndEstablishmentCuigIgnoreCase(name, cuig);

        if (cemaLocation == null) {
            log.info("Location with name {} and cuig {} doesn't exists", name, cuig);
            throw new NotFoundException(String.format("Location with name %s doesn't exits", name));
        }


        cemaLocation = locationMapper.updateEntityWithDomain(location, cemaLocation);

        locationRepository.save(cemaLocation);

        Location updatedLocation = locationMapper.mapEntityToDomain(cemaLocation);

        return new ResponseEntity<>(updatedLocation, HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve all locationes for your cuig", response = Location.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Listed all locationes")
    })
    @GetMapping(value = BASE_URL + "list", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Location>> listLocations() {
        String cuig = authorizationService.getCurrentUserCuig();

        List<CemaLocation> cemaLocations;
        if (authorizationService.isAdmin()) {
            cemaLocations = locationRepository.findAll();
        } else {
            cemaLocations = locationRepository.findAllByEstablishmentCuig(cuig);
        }

        List<Location> locations = cemaLocations.stream().map(locationMapper::mapEntityToDomain).collect(Collectors.toList());

        return new ResponseEntity<>(locations, HttpStatus.OK);
    }
}
