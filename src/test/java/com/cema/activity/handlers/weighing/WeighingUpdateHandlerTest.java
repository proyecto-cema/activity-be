package com.cema.activity.handlers.weighing;


import com.cema.activity.domain.Weighing;
import com.cema.activity.entities.CemaWeighing;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.exceptions.ValidationException;
import com.cema.activity.handlers.weighing.WeighingUpdateHandler;
import com.cema.activity.mapping.impl.WeighingMapper;
import com.cema.activity.repositories.WeighingRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import com.cema.activity.services.client.bovine.BovineClientService;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class WeighingUpdateHandlerTest {

    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private WeighingMapper weighingMapper;
    @Mock
    private WeighingRepository weighingRepository;
    @Mock
    private BovineClientService bovineClientService;

    private WeighingUpdateHandler weighingUpdateHandler;

    private String cuig = "321";

    @BeforeEach
    public void setUp(){
        openMocks(this);
        when(authorizationService.isOnTheSameEstablishment(cuig)).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn(cuig);
        weighingUpdateHandler = new WeighingUpdateHandler(authorizationService, weighingMapper,
                weighingRepository, bovineClientService);
    }

    @Test
    public void handleShouldUpdateTheWeighingToTheDatabaseWhenCorrect(){
        UUID uuid = UUID.randomUUID();

        Weighing weighing = Weighing.builder()
                .id(uuid)
                .establishmentCuig(cuig)
                .build();

        CemaWeighing cemaWeighing = new CemaWeighing();
        Optional<CemaWeighing> mockCemaWeighingOptional = Optional.of(cemaWeighing);
        when(weighingRepository.findCemaWeighingByIdAndEstablishmentCuig(uuid, cuig)).thenReturn(mockCemaWeighingOptional);
        CemaWeighing updatedCemaWeighing = new CemaWeighing();
        CemaWeighing savedCemaWeighing = new CemaWeighing();

        when(weighingMapper.updateEntityWithDomain(weighing, cemaWeighing)).thenReturn(updatedCemaWeighing);
        when(weighingRepository.save(updatedCemaWeighing)).thenReturn(savedCemaWeighing);

        Weighing updatedWeighing = Weighing.builder().build();
        when(weighingMapper.mapEntityToDomain(savedCemaWeighing)).thenReturn(updatedWeighing);

        Weighing result = weighingUpdateHandler.handle(weighing);

        assertThat(result, is(updatedWeighing));
    }

    @Test
    public void handleShouldThrowUnauthorizedExceptionWhenCuigIsFromDifferentEstablishment() {
        Weighing weighing = Weighing.builder()
                .establishmentCuig("otherCuig")
                .build();


        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            weighingUpdateHandler.handle(weighing);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Error trying to access resource from a different establishment otherCuig."));
    }

    @Test
    public void handleShouldThrowNotFoundExceptionWhenTheEntityDoesNotExistsInTheDatabase(){
        UUID uuid = UUID.fromString("1fbb888a-0408-47b1-8c07-a0b1dd685d01");

        Weighing weighing = Weighing.builder()
                .id(uuid)
                .establishmentCuig(cuig)
                .build();

        Optional<CemaWeighing> mockCemaWeighingOptional = Optional.empty();
        when(weighingRepository.findById(uuid)).thenReturn(mockCemaWeighingOptional);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            weighingUpdateHandler.handle(weighing);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Activity with id 1fbb888a-0408-47b1-8c07-a0b1dd685d01 doesn't exits"));
    }

}