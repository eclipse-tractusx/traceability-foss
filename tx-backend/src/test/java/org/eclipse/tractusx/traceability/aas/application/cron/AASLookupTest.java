package org.eclipse.tractusx.traceability.aas.application.cron;

import org.eclipse.tractusx.traceability.aas.infrastructure.model.DigitalTwinType;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.eclipse.tractusx.irs.registryclient.exceptions.RegistryServiceException;
import org.eclipse.tractusx.traceability.aas.application.service.AASService;
import org.eclipse.tractusx.traceability.aas.application.service.DTRService;
import org.eclipse.tractusx.traceability.aas.domain.model.DTR;
import org.eclipse.tractusx.traceability.aas.domain.model.TwinType;
import org.eclipse.tractusx.traceability.common.properties.AASProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AASLookupTest {

    @Mock
    private AASService aasService;

    @Mock
    private DTRService dtrService;

    @Mock
    private AASProperties aasProperties;

    @InjectMocks
    private AASLookup aasLookup;

    @BeforeEach
    void setUp() {
        when(aasProperties.getLimit()).thenReturn(10);
    }

    @Test
    void shouldPerformAutomaticDTRLookupSuccessfully() throws RegistryServiceException {
        DTR mockDTR = DTR.builder()
                .nextCursor(null)
                .limit(10)
                .digitalTwinType(DigitalTwinType.PART_TYPE)
                .build();

        when(dtrService.lookupAASShells(any(), any(), anyInt())).thenReturn(mockDTR);

        aasLookup.automaticDTRLookup();

        verify(dtrService, times(1)).lookupAASShells(eq(TwinType.PART_TYPE), isNull(), eq(10));
        verify(dtrService, times(1)).lookupAASShells(eq(TwinType.PART_INSTANCE), isNull(), eq(10));
        verify(aasService, times(2)).upsertAASList(mockDTR);
    }

   @Test
    void shouldContinuePaginationUntilCursorIsNull() throws RegistryServiceException {

       DTR firstResult = DTR.builder()
               .nextCursor("nextCursor")
               .limit(10)
               .digitalTwinType(DigitalTwinType.PART_TYPE)
               .build();
       DTR secondResult = DTR.builder()
               .nextCursor(null)
               .limit(10)
               .digitalTwinType(DigitalTwinType.PART_TYPE)
               .build();


        when(dtrService.lookupAASShells(any(), eq(null), anyInt())).thenReturn(firstResult);
        when(dtrService.lookupAASShells(any(), eq("nextCursor"), anyInt())).thenReturn(secondResult);

        aasLookup.aasLookupByType(TwinType.PART_TYPE);

        verify(dtrService, times(2)).lookupAASShells(any(), any(), anyInt());
        verify(aasService, times(2)).upsertAASList(any(DTR.class));
    }

    @Test
    void shouldThrowExceptionWhenDTRServiceFails() throws RegistryServiceException {
        when(dtrService.lookupAASShells(any(), any(), anyInt())).thenThrow(new RegistryServiceException("Service error"));

        assertThrows(RegistryServiceException.class, () -> aasLookup.aasLookupByType(TwinType.PART_TYPE));

        verify(dtrService, times(1)).lookupAASShells(any(), any(), anyInt());
        verify(aasService, never()).upsertAASList(any(DTR.class));
    }

}
