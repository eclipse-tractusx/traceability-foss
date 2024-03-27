/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.ess.application.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.ess.domain.model.EssEntity;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class EssScheduledJob {

    private final EssService service;

    public EssScheduledJob(EssService service) {
        this.service = service;
    }

    @Scheduled(
        fixedDelayString = "${ess.fixedDelayForIrsJobStatusCheck:1000}",
        initialDelayString = "${ess.initialDelayForIrsJobStatusCheck:1000}"
    )
    public void checkIrsEssInvestigationTask() {
        List<EssEntity> jobs = this.service.getEssInvestigationsWithoutStatus();
        for(EssEntity job : jobs) {
            log.info("Checking the status for ESS investigation with ID {} ...", job.getJobId());
            this.service.updateStatus(job.getJobId());
        }
    }

}
