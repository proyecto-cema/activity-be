package com.cema.activity.services.database.impl;

import com.cema.activity.domain.Inoculation;
import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.mapping.ActivityMapper;
import com.cema.activity.repositories.InoculationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class DatabaseServiceImplTest {

    @Captor
    public ArgumentCaptor<Example> exampleArgumentCaptor;

    @Captor
    public ArgumentCaptor<Pageable> pageableArgumentCaptor;

    @Mock
    private InoculationRepository inoculationRepository;

    @Mock
    private ActivityMapper<Inoculation, CemaInoculation> inoculationMapper;

    private DatabaseServiceImpl databaseService;

    @BeforeEach
    public void startUp() {
        openMocks(this);
        databaseService = new DatabaseServiceImpl(inoculationRepository, inoculationMapper);
    }

    @Test
    public void searchInoculationsShouldReturnInoculationsFromTheDatabase(){
        CemaInoculation cemaInoculation = new CemaInoculation();
        Inoculation inoculation = Inoculation.builder().build();
        int page = 0;
        int size = 3;
        when(inoculationMapper.updateEntityWithDomain(inoculation)).thenReturn(cemaInoculation);
        Page<CemaInoculation> mockPage = Mockito.mock(Page.class);
        when(inoculationRepository.findAll(exampleArgumentCaptor.capture(), pageableArgumentCaptor.capture())).thenReturn(mockPage);

        Page<CemaInoculation> result = databaseService.searchInoculations(inoculation, page, size);

        assertThat(result, is(mockPage));

        Example resultExample = exampleArgumentCaptor.getValue();
        Pageable resultPageable = pageableArgumentCaptor.getValue();

        assertThat(resultPageable.getPageSize(), is(size));
        assertThat(resultPageable.getOffset(), is(0L));

        CemaInoculation search = (CemaInoculation) resultExample.getProbe();

        assertThat(search, is(cemaInoculation));
    }

}