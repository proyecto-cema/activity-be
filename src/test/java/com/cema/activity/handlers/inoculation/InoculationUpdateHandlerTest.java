package com.cema.activity.handlers.inoculation;


import com.cema.activity.domain.Inoculation;
import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.exceptions.ValidationException;
import com.cema.activity.mapping.impl.InoculationMapper;
import com.cema.activity.repositories.InoculationRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

class InoculationUpdateHandlerTest {

    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private InoculationMapper inoculationMapper;
    @Mock
    private InoculationRepository inoculationRepository;

    private InoculationUpdateHandler inoculationUpdateHandler;

    private String cuig = "321";

    @BeforeEach
    public void setUp(){
        openMocks(this);
        when(authorizationService.isOnTheSameEstablishment(cuig)).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn(cuig);
        inoculationUpdateHandler = new InoculationUpdateHandler(authorizationService, inoculationMapper, inoculationRepository);
    }

    @Test
    public void handleShouldUpdateTheInoculationToTheDatabaseWhenCorrect(){
        UUID uuid = UUID.randomUUID();
        String batchName = "batchName";

        Inoculation inoculation = Inoculation.builder()
                .id(uuid)
                .establishmentCuig(cuig)
                .batchName(batchName)
                .build();

        CemaInoculation cemaInoculation = new CemaInoculation();
        Optional<CemaInoculation> mockCemaInoculationOptional = Optional.of(cemaInoculation);
        when(inoculationRepository.findById(uuid)).thenReturn(mockCemaInoculationOptional);
        CemaInoculation updatedCemaInoculation = new CemaInoculation();
        CemaInoculation savedCemaInoculation = new CemaInoculation();

        when(inoculationMapper.mapDomainToEntity(inoculation, cemaInoculation)).thenReturn(updatedCemaInoculation);
        when(inoculationRepository.save(updatedCemaInoculation)).thenReturn(savedCemaInoculation);

        Inoculation updatedInoculation = Inoculation.builder().build();
        when(inoculationMapper.mapEntityToDomain(savedCemaInoculation)).thenReturn(updatedInoculation);

        Inoculation result = inoculationUpdateHandler.handle(inoculation);

        assertThat(result, is(updatedInoculation));
    }

    @Test
    public void handleShouldThrowUnauthorizedExceptionWhenCuigIsFromDifferentEstablishment() {
        Inoculation inoculation = Inoculation.builder()
                .establishmentCuig("otherCuig")
                .build();


        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            inoculationUpdateHandler.handle(inoculation);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Error trying to access resource from a different establishment otherCuig."));
    }

    @Test
    public void handleShouldThrowValidationExceptionWhenBothBatchNameAndBovineTagArePopulated() {
        String batchName = "batchName";
        String bovineTag = "bovineTag";
        Inoculation inoculation = Inoculation.builder()
                .establishmentCuig(cuig)
                .batchName(batchName)
                .bovineTag(bovineTag)
                .build();


        Exception exception = assertThrows(ValidationException.class, () -> {
            inoculationUpdateHandler.handle(inoculation);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Batch Name or Bovine Tag must be populated, never both."));
    }

    @Test
    public void handleShouldThrowNotFoundExceptionWhenTheEntityDoesNotExistsInTheDatabase(){
        UUID uuid = UUID.fromString("1fbb888a-0408-47b1-8c07-a0b1dd685d01");
        String batchName = "batchName";

        Inoculation inoculation = Inoculation.builder()
                .id(uuid)
                .establishmentCuig(cuig)
                .batchName(batchName)
                .build();

        Optional<CemaInoculation> mockCemaInoculationOptional = Optional.empty();
        when(inoculationRepository.findById(uuid)).thenReturn(mockCemaInoculationOptional);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            inoculationUpdateHandler.handle(inoculation);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Activity with id 1fbb888a-0408-47b1-8c07-a0b1dd685d01 doesn't exits"));
    }

}