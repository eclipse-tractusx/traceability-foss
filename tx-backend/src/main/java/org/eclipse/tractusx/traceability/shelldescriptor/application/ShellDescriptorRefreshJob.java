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

package org.eclipse.tractusx.traceability.shelldescriptor.application;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.eclipse.tractusx.traceability.common.config.ApplicationProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile(ApplicationProfiles.NOT_TESTS)
@RequiredArgsConstructor
public class ShellDescriptorRefreshJob {

    private final DecentralRegistryService decentralRegistryService;

    @Scheduled(cron = "0 0 */2 * * ?", zone = "Europe/Berlin")
    @SchedulerLock(
            name = "data-sync-lock",
            lockAtLeastFor = "PT5M",
            lockAtMostFor = "PT15M"
    )
    public void refresh() {
        decentralRegistryService.updateShellDescriptorAndSynchronizeAssets();
    }
}
