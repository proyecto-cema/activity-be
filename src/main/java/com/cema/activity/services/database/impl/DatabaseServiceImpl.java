package com.cema.activity.services.database.impl;

import com.cema.activity.domain.Activity;
import com.cema.activity.domain.Feeding;
import com.cema.activity.domain.Inoculation;
import com.cema.activity.domain.Movement;
import com.cema.activity.domain.Ultrasound;
import com.cema.activity.domain.Weighing;
import com.cema.activity.entities.CemaFeeding;
import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.entities.CemaLocation;
import com.cema.activity.entities.CemaMovement;
import com.cema.activity.entities.CemaUltrasound;
import com.cema.activity.entities.CemaWeighing;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.mapping.Mapper;
import com.cema.activity.repositories.FeedingRepository;
import com.cema.activity.repositories.InoculationRepository;
import com.cema.activity.repositories.LocationRepository;
import com.cema.activity.repositories.MovementRepository;
import com.cema.activity.repositories.UltrasoundRepository;
import com.cema.activity.repositories.WeighingRepository;
import com.cema.activity.services.database.DatabaseService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DatabaseServiceImpl implements DatabaseService {

    private final InoculationRepository inoculationRepository;
    private final WeighingRepository weighingRepository;
    private final UltrasoundRepository ultrasoundRepository;
    private final LocationRepository locationRepository;
    private final MovementRepository movementRepository;
    private final FeedingRepository feedingRepository;
    private final Mapper<Weighing, CemaWeighing> weighingMapper;
    private final Mapper<Inoculation, CemaInoculation> inoculationMapper;
    private final Mapper<Ultrasound, CemaUltrasound> ultrasoundMapper;
    private final Mapper<Movement, CemaMovement> movementMapper;
    private final Mapper<Feeding, CemaFeeding> feedingMapper;


    public DatabaseServiceImpl(InoculationRepository inoculationRepository, WeighingRepository weighingRepository,
                               UltrasoundRepository ultrasoundRepository, LocationRepository locationRepository,
                               MovementRepository movementRepository, Mapper<Weighing, CemaWeighing> weighingMapper,
                               FeedingRepository feedingRepository,
                               Mapper<Inoculation, CemaInoculation> inoculationMapper,
                               Mapper<Ultrasound, CemaUltrasound> ultrasoundMapper,
                               Mapper<Movement, CemaMovement> movementMapper,
                               Mapper<Feeding, CemaFeeding> feedingMapper) {
        this.inoculationRepository = inoculationRepository;
        this.weighingRepository = weighingRepository;
        this.ultrasoundRepository = ultrasoundRepository;
        this.locationRepository = locationRepository;
        this.movementRepository = movementRepository;
        this.weighingMapper = weighingMapper;
        this.inoculationMapper = inoculationMapper;
        this.ultrasoundMapper = ultrasoundMapper;
        this.movementMapper = movementMapper;
        this.feedingMapper = feedingMapper;
        this.feedingRepository = feedingRepository;
    }

    @Override
    public Page<CemaInoculation> searchInoculations(Inoculation inoculation, int page, int size) {
        CemaInoculation toSearch = inoculationMapper.mapDomainToEntity(inoculation);

        ExampleMatcher caseInsensitiveExampleMatcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Pageable paging = PageRequest.of(page, size, Sort.by("executionDate"));
        return inoculationRepository.findAll(Example.of(toSearch, caseInsensitiveExampleMatcher), paging);
    }

    @Override
    public Page<CemaWeighing> searchWeightings(Weighing weighing, int page, int size) {
        CemaWeighing toSearch = weighingMapper.mapDomainToEntity(weighing);

        ExampleMatcher caseInsensitiveExampleMatcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Pageable paging = PageRequest.of(page, size, Sort.by("executionDate"));
        return weighingRepository.findAll(Example.of(toSearch, caseInsensitiveExampleMatcher), paging);
    }

    @Override
    public Page<CemaUltrasound> searchUltrasounds(Ultrasound weighing, int page, int size) {
        CemaUltrasound toSearch = ultrasoundMapper.mapDomainToEntity(weighing);

        ExampleMatcher caseInsensitiveExampleMatcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Pageable paging = PageRequest.of(page, size, Sort.by("executionDate"));
        return ultrasoundRepository.findAll(Example.of(toSearch, caseInsensitiveExampleMatcher), paging);
    }

    @Override
    public Page<CemaFeeding> searchFeedings(Feeding feeding, int page, int size) {
        CemaFeeding toSearch = feedingMapper.mapDomainToEntity(feeding);

        ExampleMatcher caseInsensitiveExampleMatcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Pageable paging = PageRequest.of(page, size, Sort.by("executionDate"));
        return feedingRepository.findAll(Example.of(toSearch, caseInsensitiveExampleMatcher), paging);
    }

    @Override
    public Page<CemaMovement> searchMovements(Movement movement, int page, int size) {
        CemaMovement toSearch = movementMapper.mapDomainToEntity(movement);

        ExampleMatcher caseInsensitiveExampleMatcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Pageable paging = PageRequest.of(page, size, Sort.by("executionDate"));
        return movementRepository.findAll(Example.of(toSearch, caseInsensitiveExampleMatcher), paging);
    }

    @Override
    public void makeAllNonDefaultButThis(String cuig, UUID id){
        locationRepository.makeAllNonDefault(cuig, id);
    }

    @Override
    public void makeAllNonDefault(String cuig){
        locationRepository.makeAllNonDefault(cuig);
    }

    @Override
    public void makeFirstDefault(String cuig){
        CemaLocation first = locationRepository.findTopByEstablishmentCuig(cuig);
        if(first != null) {
            first.setDefault(true);
            locationRepository.save(first);
        }
    }

    @Override
    public void makeFirstDefaultWhenNonAreDefault(String cuig){
        CemaLocation defaultLocation = locationRepository.findTopByEstablishmentCuigAndIsDefault(cuig, true);
        if(defaultLocation == null){
            makeFirstDefault(cuig);
        }
    }

    @Override
    public void saveMovement(Movement movement){
        CemaMovement cemaMovement = movementMapper.mapDomainToEntity(movement);
        CemaLocation cemaLocation = locationRepository.findCemaLocationByNameAndEstablishmentCuigIgnoreCase(movement.getLocationName(), movement.getEstablishmentCuig());

        if (cemaLocation == null) {
            throw new NotFoundException(String.format("The location %s does not exists", movement.getLocationName()));
        }

        cemaMovement.setLocation(cemaLocation);

        cemaMovement = movementRepository.save(cemaMovement);

        movement.setId(cemaMovement.getId());
    }

    @Override
    public List<Activity> getAllUsersToNotifyToday(){
        List<Activity> activities = new ArrayList<>();
        List<CemaFeeding> feedings = feedingRepository.getAllForToday();
        List<CemaInoculation> inoculations = inoculationRepository.getAllForToday();
        List<CemaMovement> movements = movementRepository.getAllForToday();
        List<CemaUltrasound> ultrasounds = ultrasoundRepository.getAllForToday();
        List<CemaWeighing> weightings = weighingRepository.getAllForToday();

        activities.addAll(feedings.stream().map(feedingMapper::mapEntityToDomain).collect(Collectors.toList()));
        activities.addAll(inoculations.stream().map(inoculationMapper::mapEntityToDomain).collect(Collectors.toList()));
        activities.addAll(movements.stream().map(movementMapper::mapEntityToDomain).collect(Collectors.toList()));
        activities.addAll(ultrasounds.stream().map(ultrasoundMapper::mapEntityToDomain).collect(Collectors.toList()));
        activities.addAll(weightings.stream().map(weighingMapper::mapEntityToDomain).collect(Collectors.toList()));

        return activities;
    }
}
