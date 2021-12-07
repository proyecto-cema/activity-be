package com.cema.activity.handlers.inoculation;

import com.cema.activity.domain.Inoculation;
import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.exceptions.ValidationException;
import com.cema.activity.mapping.impl.InoculationMapper;
import com.cema.activity.repositories.InoculationRepository;
import com.cema.activity.services.authorization.AuthorizationService;
import com.cema.activity.services.client.bovine.BovineClientService;
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

class InoculationRegisterHandlerTest {

    @Mock
    private InoculationMapper inoculationMapper;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private InoculationRepository inoculationRepository;
    @Mock
    private BovineClientService bovineClientService;

    private InoculationRegisterHandler inoculationRegisterHandler;

    private String cuig = "321";

    @BeforeEach
    public void setUp() {
        openMocks(this);
        when(authorizationService.isOnTheSameEstablishment(cuig)).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn(cuig);
        inoculationRegisterHandler = new InoculationRegisterHandler(inoculationMapper, authorizationService,
                inoculationRepository, bovineClientService);
    }

    @Test
    public void handleShouldRegisterNewActivity() {
        UUID uuid = UUID.randomUUID();
        Inoculation activity = Inoculation.builder()
                .establishmentCuig(cuig)
                .batchName("batchName")
                .build();

        CemaInoculation cemaInoculation = new CemaInoculation();
        cemaInoculation.setId(uuid);
        when(inoculationMapper.mapDomainToEntity(activity)).thenReturn(cemaInoculation);

        Inoculation result = inoculationRegisterHandler.handle(activity);

        verify(inoculationRepository).save(cemaInoculation);

        assertThat(result.getId(), is(uuid));
    }

    @Test
    public void handleShouldThrowUnauthorizedExceptionWhenOutsideEstablishment() {
        UUID uuid = UUID.fromString("1fbb888a-0408-47b1-8c07-a0b1dd685d01");
        Inoculation activity = Inoculation.builder()
                .id(uuid)
                .establishmentCuig("otherCuig")
                .batchName("batchName")
                .build();

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            inoculationRegisterHandler.handle(activity);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Error trying to access resource from a different establishment otherCuig."));
    }

    @Test
    public void handleShouldThrowValidationExceptionWhenBothBovineTagAndBatchNameArePresent() {
        UUID uuid = UUID.fromString("1fbb888a-0408-47b1-8c07-a0b1dd685d01");
        Inoculation activity = Inoculation.builder()
                .id(uuid)
                .establishmentCuig(cuig)
                .batchName("batchName")
                .bovineTag("bovineTag")
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            inoculationRegisterHandler.handle(activity);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Batch Name or Bovine Tag must be populated, never both."));
    }

    @Test
    public void handleShouldThrowValidationExceptionWhenBothBovineTagAndBatchNameAreEmpty() {
        UUID uuid = UUID.fromString("1fbb888a-0408-47b1-8c07-a0b1dd685d01");
        Inoculation activity = Inoculation.builder()
                .id(uuid)
                .establishmentCuig(cuig)
                .batchName("")
                .bovineTag("")
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            inoculationRegisterHandler.handle(activity);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Batch Name or Bovine Tag must be populated, never both."));
    }

}