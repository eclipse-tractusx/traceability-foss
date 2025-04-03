/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets.infrastructure.scheduler;

import org.eclipse.tractusx.traceability.shelldescriptor.domain.service.DecentralRegistryServiceImpl;
import org.eclipse.tractusx.traceability.shelldescriptor.application.AssetsRefreshJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AssetsRefreshJobTest {

    @Mock
    private DecentralRegistryServiceImpl registryFacade;

    @Test
    void refresh_shouldCallLoadShellDescriptors() {
        AssetsRefreshJob job = new AssetsRefreshJob(registryFacade);
        job.refresh();
        verify(registryFacade).synchronizeAssets();
    }

    @Test
    void refresh_shouldBeScheduledForEveryTwoHours() throws NoSuchMethodException {
        Scheduled scheduledAnnotation = AssetsRefreshJob.class.getDeclaredMethod("refresh").getAnnotation(Scheduled.class);
        String cronExpression = scheduledAnnotation.cron();
        String cronZone = scheduledAnnotation.zone();
        assertNotNull(cronExpression);
        assertNotNull(cronZone);
        assertEquals("${traceability.assetRefreshJobCronExpression}", cronExpression);
        assertEquals("${traceability.assetRefreshJobZone}", cronZone);
    }
}
