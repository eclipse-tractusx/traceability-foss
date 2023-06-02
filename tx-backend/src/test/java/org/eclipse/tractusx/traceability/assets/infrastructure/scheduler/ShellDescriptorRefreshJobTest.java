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

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.RegistryFacade;
import org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.scheduler.ShellDescriptorRefreshJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShellDescriptorRefreshJobTest {

    @Mock
    private RegistryFacade registryFacade;

    @Test
    void refresh_shouldCallLoadShellDescriptors() {
        ShellDescriptorRefreshJob job = new ShellDescriptorRefreshJob(registryFacade);
        job.refresh();
        verify(registryFacade).updateShellDescriptorAndSynchronizeAssets();
    }

    @Test
    void refresh_shouldBeScheduledForEveryTwoHours() throws NoSuchMethodException {
        Scheduled scheduledAnnotation = ShellDescriptorRefreshJob.class.getDeclaredMethod("refresh").getAnnotation(Scheduled.class);
        String cronExpression = scheduledAnnotation.cron();
        assertEquals("0 0 */2 * * ?", cronExpression);
    }

    @Test
    void refresh_shouldHaveSchedulerLockAnnotation() throws NoSuchMethodException {
        Scheduled scheduledAnnotation = ShellDescriptorRefreshJob.class.getDeclaredMethod("refresh").getAnnotation(Scheduled.class);
        assertNotNull(scheduledAnnotation);
        SchedulerLock schedulerLockAnnotation = ShellDescriptorRefreshJob.class.getDeclaredMethod("refresh").getAnnotation(SchedulerLock.class);
        assertNotNull(schedulerLockAnnotation);
        assertEquals("data-sync-lock", schedulerLockAnnotation.name());
        assertEquals("PT5M", schedulerLockAnnotation.lockAtLeastFor());
        assertEquals("PT15M", schedulerLockAnnotation.lockAtMostFor());
    }
}
