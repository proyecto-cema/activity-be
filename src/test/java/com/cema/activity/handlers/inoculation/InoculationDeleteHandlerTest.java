package com.cema.activity.handlers.inoculation;

import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.repositories.InoculationRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class InoculationDeleteHandlerTest {

    @Captor
    public ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Mock
    private InoculationRepository inoculationRepository;
    @Mock
    private AuthorizationService authorizationService;

    private InoculationDeleteHandler inoculationDeleteHandler;

    private String cuig = "321";

    @BeforeEach
    public void setUp() {
        openMocks(this);
        when(authorizationService.isOnTheSameEstablishment(cuig)).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn(cuig);
        inoculationDeleteHandler = new InoculationDeleteHandler(inoculationRepository, authorizationService);
    }

    @Test
    public void handleShouldDeleteTheInoculationFromTheDatabaseWhenItExists() {
        String uuid = "1fbb888a-0408-47b1-8c07-a0b1dd685d01";
        CemaInoculation cemaInoculation = new CemaInoculation();
        cemaInoculation.setEstablishmentCuig(cuig);
        Optional<CemaInoculation> mockCemaInoculationOptional = Optional.of(cemaInoculation);
        when(inoculationRepository.findById(uuidArgumentCaptor.capture())).thenReturn(mockCemaInoculationOptional);

        inoculationDeleteHandler.handle(uuid);

        verify(inoculationRepository).delete(cemaInoculation);
        UUID uuidObject = uuidArgumentCaptor.getValue();
        assertThat(uuidObject.toString(), is(uuid));
    }

    @Test
    public void handleShouldThrowUnauthorizedExceptionWhenOutsideEstablishment() {
        String uuid = "1fbb888a-0408-47b1-8c07-a0b1dd685d01";
        CemaInoculation cemaInoculation = new CemaInoculation();
        cemaInoculation.setEstablishmentCuig("otherCuig");
        Optional<CemaInoculation> mockCemaInoculationOptional = Optional.of(cemaInoculation);
        when(inoculationRepository.findById(uuidArgumentCaptor.capture())).thenReturn(mockCemaInoculationOptional);

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            inoculationDeleteHandler.handle(uuid);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Error trying to access resource from a different establishment otherCuig."));
    }

    @Test
    public void handleShouldThrowNotFoundExceptionWhenUuidNotFoundInDatabase() {
        String uuid = "1fbb888a-0408-47b1-8c07-a0b1dd685d01";
        CemaInoculation cemaInoculation = new CemaInoculation();
        cemaInoculation.setEstablishmentCuig("otherCuig");
        Optional<CemaInoculation> mockCemaInoculationOptional = Optional.empty();
        when(inoculationRepository.findById(uuidArgumentCaptor.capture())).thenReturn(mockCemaInoculationOptional);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            inoculationDeleteHandler.handle(uuid);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Inoculation with id 1fbb888a-0408-47b1-8c07-a0b1dd685d01 doesn't exits"));
    }

}