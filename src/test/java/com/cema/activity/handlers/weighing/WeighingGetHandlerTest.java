package com.cema.activity.handlers.weighing;

import com.cema.activity.domain.Weighing;
import com.cema.activity.entities.CemaWeighing;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.mapping.impl.WeighingMapper;
import com.cema.activity.repositories.WeighingRepository;
import com.cema.activity.services.authorization.AuthorizationService;
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

class WeighingGetHandlerTest {

    @Mock
    private WeighingMapper weighingMapper;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private WeighingRepository weighingRepository;

    private WeighingGetHandler weighingGetHandler;

    private String cuig = "321";

    @BeforeEach
    public void setUp() {
        openMocks(this);
        when(authorizationService.isOnTheSameEstablishment(cuig)).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn(cuig);
        weighingGetHandler = new WeighingGetHandler(weighingMapper, authorizationService, weighingRepository);
    }

    @Test
    public void handleShouldGetTheWeighingFromTheDatabaseWhenItExists() {
        UUID uuid = UUID.fromString("1fbb888a-0408-47b1-8c07-a0b1dd685d01");
        Weighing activity = Weighing.builder()
                .id(uuid)
                .establishmentCuig(cuig)
                .build();

        Weighing mappedWeighing = Weighing.builder().build();

        CemaWeighing cemaWeighing = new CemaWeighing();
        Optional<CemaWeighing> cemaWeighingOptional = Optional.of(cemaWeighing);
        when(weighingRepository.findById(uuid)).thenReturn(cemaWeighingOptional);
        when(weighingMapper.mapEntityToDomain(cemaWeighing)).thenReturn(mappedWeighing);

        Weighing result = weighingGetHandler.handle(activity);

        assertThat(result, is(mappedWeighing));
    }

    @Test
    public void handleShouldThrowUnauthorizedExceptionWhenOutsideEstablishment() {
        UUID uuid = UUID.fromString("1fbb888a-0408-47b1-8c07-a0b1dd685d01");
        Weighing activity = Weighing.builder()
                .id(uuid)
                .establishmentCuig("otherCuig")
                .build();

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            weighingGetHandler.handle(activity);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Error trying to access resource from a different establishment otherCuig."));
    }

    @Test
    public void handleShouldThrowNotFoundExceptionWhenUuidNotFound() {
        UUID uuid = UUID.fromString("1fbb888a-0408-47b1-8c07-a0b1dd685d01");
        Weighing activity = Weighing.builder()
                .id(uuid)
                .establishmentCuig(cuig)
                .build();
        Optional<CemaWeighing> cemaWeighingOptional = Optional.empty();
        when(weighingRepository.findById(uuid)).thenReturn(cemaWeighingOptional);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            weighingGetHandler.handle(activity);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Weighing with id 1fbb888a-0408-47b1-8c07-a0b1dd685d01 doesn't exits"));
    }

}