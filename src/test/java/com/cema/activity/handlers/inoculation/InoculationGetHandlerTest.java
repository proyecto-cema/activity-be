package com.cema.activity.handlers.inoculation;

import com.cema.activity.domain.Inoculation;
import com.cema.activity.entities.CemaInoculation;
import com.cema.activity.exceptions.NotFoundException;
import com.cema.activity.exceptions.UnauthorizedException;
import com.cema.activity.mapping.impl.InoculationMapper;
import com.cema.activity.repositories.InoculationRepository;
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

class InoculationGetHandlerTest {

    @Mock
    private InoculationMapper inoculationMapper;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private InoculationRepository inoculationRepository;

    private InoculationGetHandler inoculationGetHandler;

    private String cuig = "321";

    @BeforeEach
    public void setUp() {
        openMocks(this);
        when(authorizationService.isOnTheSameEstablishment(cuig)).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn(cuig);
        inoculationGetHandler = new InoculationGetHandler(inoculationMapper, authorizationService, inoculationRepository);
    }

    @Test
    public void handleShouldGetTheInoculationFromTheDatabaseWhenItExists() {
        UUID uuid = UUID.fromString("1fbb888a-0408-47b1-8c07-a0b1dd685d01");
        Inoculation activity = Inoculation.builder()
                .id(uuid)
                .establishmentCuig(cuig)
                .build();

        Inoculation mappedInoculation = Inoculation.builder().build();

        CemaInoculation cemaInoculation = new CemaInoculation();
        Optional<CemaInoculation> cemaInoculationOptional = Optional.of(cemaInoculation);
        when(inoculationRepository.findById(uuid)).thenReturn(cemaInoculationOptional);
        when(inoculationMapper.mapEntityToDomain(cemaInoculation)).thenReturn(mappedInoculation);

        Inoculation result = inoculationGetHandler.handle(activity);

        assertThat(result, is(mappedInoculation));
    }

    @Test
    public void handleShouldThrowUnauthorizedExceptionWhenOutsideEstablishment() {
        UUID uuid = UUID.fromString("1fbb888a-0408-47b1-8c07-a0b1dd685d01");
        Inoculation activity = Inoculation.builder()
                .id(uuid)
                .establishmentCuig("otherCuig")
                .build();

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            inoculationGetHandler.handle(activity);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Error trying to access resource from a different establishment otherCuig."));
    }

    @Test
    public void handleShouldThrowNotFoundExceptionWhenUuidNotFound() {
        UUID uuid = UUID.fromString("1fbb888a-0408-47b1-8c07-a0b1dd685d01");
        Inoculation activity = Inoculation.builder()
                .id(uuid)
                .establishmentCuig(cuig)
                .build();
        Optional<CemaInoculation> cemaInoculationOptional = Optional.empty();
        when(inoculationRepository.findById(uuid)).thenReturn(cemaInoculationOptional);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            inoculationGetHandler.handle(activity);
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, Is.is("Inoculation with id 1fbb888a-0408-47b1-8c07-a0b1dd685d01 doesn't exits"));
    }

}