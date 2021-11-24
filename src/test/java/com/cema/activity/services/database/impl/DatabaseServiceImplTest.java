package com.cema.activity.services.database.impl;

import com.cema.activity.domain.Inoculation;
import com.cema.activity.domain.Movement;
import com.cema.activity.domain.Ultrasound;
import com.cema.activity.domain.Weighing;
import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.entities.CemaMovement;
import com.cema.activity.entities.CemaUltrasound;
import com.cema.activity.entities.CemaWeighing;
import com.cema.activity.mapping.Mapper;
import com.cema.activity.repositories.InoculationRepository;
import com.cema.activity.repositories.LocationRepository;
import com.cema.activity.repositories.MovementRepository;
import com.cema.activity.repositories.UltrasoundRepository;
import com.cema.activity.repositories.WeighingRepository;
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
    private UltrasoundRepository ultrasoundRepository;

    @Mock
    private Mapper<Inoculation, CemaInoculation> inoculationMapper;

    @Mock
    private WeighingRepository weighingRepository;

    @Mock
    private Mapper<Weighing, CemaWeighing> weighingMapper;

    @Mock
    private Mapper<Ultrasound, CemaUltrasound> ultrasoundMapper;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private Mapper<Movement, CemaMovement> movementMapper;

    private DatabaseServiceImpl databaseService;

    @BeforeEach
    public void startUp() {
        openMocks(this);
        databaseService = new DatabaseServiceImpl(inoculationRepository, weighingRepository, ultrasoundRepository,
                weighingMapper, inoculationMapper, ultrasoundMapper, locationRepository, movementMapper, movementRepository);
    }

    @Test
    public void searchInoculationsShouldReturnInoculationsFromTheDatabase() {
        CemaInoculation cemaInoculation = new CemaInoculation();
        Inoculation inoculation = Inoculation.builder().build();
        int page = 0;
        int size = 3;
        when(inoculationMapper.mapDomainToEntity(inoculation)).thenReturn(cemaInoculation);
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

    @Test
    public void searchWeightingsShouldReturnInoculationsFromTheDatabase() {
        CemaWeighing cemaWeighing = new CemaWeighing();
        Weighing weighing = Weighing.builder().build();
        int page = 0;
        int size = 3;
        when(weighingMapper.mapDomainToEntity(weighing)).thenReturn(cemaWeighing);
        Page<CemaWeighing> mockPage = Mockito.mock(Page.class);
        when(weighingRepository.findAll(exampleArgumentCaptor.capture(), pageableArgumentCaptor.capture())).thenReturn(mockPage);

        Page<CemaWeighing> result = databaseService.searchWeightings(weighing, page, size);

        assertThat(result, is(mockPage));

        Example resultExample = exampleArgumentCaptor.getValue();
        Pageable resultPageable = pageableArgumentCaptor.getValue();

        assertThat(resultPageable.getPageSize(), is(size));
        assertThat(resultPageable.getOffset(), is(0L));

        CemaWeighing search = (CemaWeighing) resultExample.getProbe();

        assertThat(search, is(cemaWeighing));
    }

}