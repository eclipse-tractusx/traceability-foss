/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.traceability.shelldescriptor.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.service.AssetAsBuiltServiceImpl;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.service.AssetAsPlannedServiceImpl;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.ProcessingState;
import org.eclipse.tractusx.traceability.configuration.application.service.ConfigurationService;
import org.eclipse.tractusx.traceability.configuration.application.service.OrderService;
import org.eclipse.tractusx.traceability.configuration.domain.model.Order;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;

@ExtendWith(MockitoExtension.class)
class DecentralRegistryServiceImplTest {

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private AssetAsBuiltRepository assetAsBuiltRepository;
    @Mock
    private AssetAsPlannedRepository assetAsPlannedRepository;
    @Mock
    private TaskScheduler taskScheduler;
    @Mock
    private AssetAsBuiltServiceImpl assetAsBuiltService;
    @Mock
    private AssetAsPlannedServiceImpl assetAsPlannedService;
    @Captor
    private ArgumentCaptor<List<AssetBase>> assetsCaptor;
    @Mock
    private OrderService orderService;

    @InjectMocks
    private DecentralRegistryServiceImpl decentralRegistryService;

    @Test
    void shouldSynchronizeAssets() {
        // given
        final String cronExpression = "*/60 * * * * *";
        final TriggerConfiguration triggerConfiguration = TriggerConfiguration.builder()
                .cronExpressionRegisterOrderTTLReached(cronExpression).build();

        final String responseId = "responseId";

        final OrderConfiguration orderConfiguration = OrderConfiguration.builder().timeoutMs(100).build();

        when(configurationService.getLatestTriggerConfiguration()).thenReturn(Optional.of(triggerConfiguration));
        when(assetAsBuiltRepository.findAllExpired()).thenReturn(List.of(AssetBase.builder().id("1").importState(ImportState.UNSET).build()));
        when(assetAsBuiltService.syncAssetsUsingIRSOrderAPI(any(), any())).thenReturn(responseId);
        when(configurationService.getLatestOrderConfiguration()).thenReturn(Optional.of(orderConfiguration));

        // when
        decentralRegistryService.synchronizeAssets();

        // then
        verify(taskScheduler).schedule(any(Runnable.class), eq(new CronTrigger(cronExpression)));

        // when
        decentralRegistryService.executeJob();

        // then
        verify(orderService).persistOrder(eq(Order.builder()
                .id(responseId)
                .status(ProcessingState.INITIALIZED)
                .orderConfiguration(orderConfiguration)
                .build()));

        verify(assetAsBuiltRepository).saveAll(assetsCaptor.capture());

        List<AssetBase> assets = assetsCaptor.getValue();
        assertThat(assets.stream().allMatch(assetBase -> assetBase.getImportState().equals(ImportState.IN_SYNCHRONIZATION)))
                .isTrue();
    }

    @Test
    void shouldOverwritePreviousSchedule() {
        // given
        final String cronExpression1 = "* * * * * *";
        final String cronExpression2 = "0 0 20 * * *";

        final TriggerConfiguration triggerConfiguration1 = TriggerConfiguration.builder()
                .cronExpressionRegisterOrderTTLReached(cronExpression1).build();
        final TriggerConfiguration triggerConfiguration2 = TriggerConfiguration.builder()
                .cronExpressionRegisterOrderTTLReached(cronExpression2).build();

        when(configurationService.getLatestTriggerConfiguration())
                .thenReturn(Optional.of(triggerConfiguration1))
                .thenReturn(Optional.of(triggerConfiguration2));

        ScheduledFuture currentSchedule = Mockito.mock(ScheduledFuture.class);
        when(taskScheduler.schedule(any(), any(Trigger.class))).thenReturn(currentSchedule);

        // when
        decentralRegistryService.synchronizeAssets();
        decentralRegistryService.synchronizeAssets();

        // then
        verify(taskScheduler).schedule(any(Runnable.class), eq(new CronTrigger(cronExpression1)));
        verify(taskScheduler).schedule(any(Runnable.class), eq(new CronTrigger(cronExpression2)));
        verify(currentSchedule).cancel(false);
    }
}
