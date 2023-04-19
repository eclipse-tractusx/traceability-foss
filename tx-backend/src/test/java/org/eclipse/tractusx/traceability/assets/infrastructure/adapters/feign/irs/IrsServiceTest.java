package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs;

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.ports.BpnRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.AssetsConverter;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.JobResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.JobStatus;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.StartJobRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.StartJobResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IrsServiceTest {
    @InjectMocks
    private IrsService irsService;

    @Mock
    private IRSApiClient irsClient;

    @Mock
    private BpnRepository bpnRepository;
    @Mock
    private AssetsConverter assetsConverter;

    @Test
    void testFindAssets_completedJob_returnsConvertedAssets() {

        // Given
        StartJobResponse startJobResponse = mock(StartJobResponse.class);
        when(irsClient.registerJob(any(StartJobRequest.class))).thenReturn(startJobResponse);
        JobResponse jobResponse = mock(JobResponse.class);
        when(irsClient.getJobDetails(startJobResponse.id())).thenReturn(jobResponse);
        JobStatus jobStatus = mock(JobStatus.class);
        when(jobResponse.jobStatus()).thenReturn(jobStatus);
        when(jobStatus.lastModifiedOn()).thenReturn(new Date());
        when(jobStatus.startedOn()).thenReturn(new Date());
        when(jobResponse.isCompleted()).thenReturn(true);
        Asset asset = mock(Asset.class);
        List<Asset> expectedAssets = List.of(asset);
        when(assetsConverter.convertAssets(jobResponse)).thenReturn(expectedAssets);

        // When
        List<Asset> result = irsService.findAssets("1");

        // Then
        assertThat(result).isEqualTo(expectedAssets);

    }

    @Test
    void testFindAssets_uncompletedJob_returnsEmptyListOfAssets() {

        // Given
        StartJobResponse startJobResponse = mock(StartJobResponse.class);
        when(irsClient.registerJob(any(StartJobRequest.class))).thenReturn(startJobResponse);
        JobResponse jobResponse = mock(JobResponse.class);
        when(irsClient.getJobDetails(startJobResponse.id())).thenReturn(jobResponse);
        JobStatus jobStatus = mock(JobStatus.class);
        when(jobResponse.jobStatus()).thenReturn(jobStatus);
        when(jobStatus.lastModifiedOn()).thenReturn(new Date());
        when(jobStatus.startedOn()).thenReturn(new Date());
        when(jobResponse.isCompleted()).thenReturn(false);

        // When
        List<Asset> result = irsService.findAssets("1");

        // Then
        assertThat(result).isEqualTo(Collections.EMPTY_LIST);
        Mockito.verify(assetsConverter, never()).convertAssets(any(JobResponse.class));

    }


}
