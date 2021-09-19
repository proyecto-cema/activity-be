package com.cema.activity.services.database.impl;

import com.cema.activity.domain.Inoculation;
import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.mapping.ActivityMapper;
import com.cema.activity.repositories.InoculationRepository;
import com.cema.activity.services.database.DatabaseService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class DatabaseServiceImpl implements DatabaseService {

    private final InoculationRepository inoculationRepository;
    private final ActivityMapper<Inoculation, CemaInoculation> inoculationMapper;

    public DatabaseServiceImpl(InoculationRepository inoculationRepository, ActivityMapper<Inoculation, CemaInoculation> inoculationMapper) {
        this.inoculationRepository = inoculationRepository;
        this.inoculationMapper = inoculationMapper;
    }

    @Override
    public Page<CemaInoculation> searchInoculations(Inoculation inoculation, int page, int size) {
        CemaInoculation toSearch = inoculationMapper.mapDomainToEntity(inoculation);

        ExampleMatcher caseInsensitiveExampleMatcher = ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Pageable paging = PageRequest.of(page, size, Sort.by("executionDate"));
        return inoculationRepository.findAll(Example.of(toSearch, caseInsensitiveExampleMatcher), paging);
    }
}
