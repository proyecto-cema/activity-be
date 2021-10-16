package com.cema.activity.handlers.weighing;

import com.cema.activity.entities.CemaWeighing;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.repositories.WeighingRepository;
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

class WeighingDeleteHandlerTest {

    @Captor
    public ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Mock
    private WeighingRepository weighingRepository;
    @Mock
    private AuthorizationService authorizationService;

    private WeighingDeleteHandler weighingDeleteHandler;

    private String cuig = "321";

    @BeforeEach
    public void setUp() {
        openMocks(this);
        when(authorizationService.isOnTheSameEstablishment(cuig)).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn(cuig);
        weighingDeleteHandler = new WeighingDeleteHandler(weighingRepository, authorizationService);
    }

    @Test
    public void handleShouldDeleteTheWeighingFromTheDatabaseWhenItExists() {
        String uuid = "1fbb888a-0408-47b1-8c07-a0b1dd685d01";
        CemaWeighing cemaWeighing = new CemaWeighing();
        cemaWeighing.setEstablishmentCuig(cuig);
        Optional<CemaWeighing> mockCemaWeighingOptional = Optional.of(cemaWeighing);
        when(weighingRepository.findById(uuidArgumentCaptor.capture())).thenReturn(mockCemaWeighingOptional);

        weighingDeleteHandler.handle(uuid);

        verify(weighingRepository).delete(cemaWeighing);
        UUID uuidObject = uuidArgumentCaptor.getValue();
        assertThat(uuidObject.toString(), is(uuid));
    }

    @Test
    public void handleShouldThrowUnauthorizedExceptionWhenOutsideEstablishment() {
        String uuid = "1fbb888a-0408-47b1-8c07-a0b1dd685d01";
        CemaWeighing cemaWeighing = new CemaWeighing();
        cemaWeighing.setEstablishmentCuig("otherCuig");
        Optional<CemaWeighing> mockCemaWeighingOptional = Optional.of(cemaWeighing);
        when(weighingRepository.findById(uuidArgumentCaptor.capture())).thenReturn(mockCemaWeighingOptional);

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            weighingDeleteHandler.handle(uuid);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Error trying to access resource from a different establishment otherCuig."));
    }

    @Test
    public void handleShouldThrowNotFoundExceptionWhenUuidNotFoundInDatabase() {
        String uuid = "1fbb888a-0408-47b1-8c07-a0b1dd685d01";
        CemaWeighing cemaWeighing = new CemaWeighing();
        cemaWeighing.setEstablishmentCuig("otherCuig");
        Optional<CemaWeighing> mockCemaWeighingOptional = Optional.empty();
        when(weighingRepository.findById(uuidArgumentCaptor.capture())).thenReturn(mockCemaWeighingOptional);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            weighingDeleteHandler.handle(uuid);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Weighing with id 1fbb888a-0408-47b1-8c07-a0b1dd685d01 doesn't exits"));
    }

}