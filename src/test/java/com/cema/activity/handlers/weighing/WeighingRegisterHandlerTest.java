package com.cema.activity.handlers.weighing;

import com.cema.activity.domain.Weighing;
import com.cema.activity.entities.CemaWeighing;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.mapping.impl.WeighingMapper;
import com.cema.activity.repositories.WeighingRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class WeighingRegisterHandlerTest {

    @Mock
    private WeighingMapper weighingMapper;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private WeighingRepository weighingRepository;

    private WeighingRegisterHandler weighingRegisterHandler;

    private String cuig = "321";

    @BeforeEach
    public void setUp() {
        openMocks(this);
        when(authorizationService.isOnTheSameEstablishment(cuig)).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn(cuig);
        weighingRegisterHandler = new WeighingRegisterHandler(weighingMapper, authorizationService, weighingRepository);
    }

    @Test
    public void handleShouldRegisterNewActivity() {
        UUID uuid = UUID.randomUUID();
        Weighing activity = Weighing.builder()
                .establishmentCuig(cuig)
                .build();

        CemaWeighing cemaWeighing = new CemaWeighing();
        cemaWeighing.setId(uuid);
        when(weighingMapper.mapDomainToEntity(activity)).thenReturn(cemaWeighing);

        Weighing result = weighingRegisterHandler.handle(activity);

        verify(weighingRepository).save(cemaWeighing);

        assertThat(result.getId(), is(uuid));
    }

    @Test
    public void handleShouldThrowUnauthorizedExceptionWhenOutsideEstablishment() {
        UUID uuid = UUID.fromString("1fbb888a-0408-47b1-8c07-a0b1dd685d01");
        Weighing activity = Weighing.builder()
                .id(uuid)
                .establishmentCuig("otherCuig")
                .build();

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            weighingRegisterHandler.handle(activity);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Error trying to access resource from a different establishment otherCuig."));
    }

}