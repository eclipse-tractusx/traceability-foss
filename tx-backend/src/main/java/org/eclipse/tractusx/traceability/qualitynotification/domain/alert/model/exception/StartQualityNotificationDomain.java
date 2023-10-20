/********************************************************************************
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

package org.eclipse.tractusx.traceability.qualitynotification.domain.alert.model.exception;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import qualitynotification.base.request.StartQualityNotificationRequest;

import java.time.Instant;
import java.util.List;

@Builder
@Getter
@Data
public class StartQualityNotificationDomain {

    private List<String> partIds;

    private String description;

    private Instant targetDate;

    private QualityNotificationSeverity severity;

    private boolean isAsBuilt;

    private String receiverBpn;



    public static StartQualityNotificationDomain from(StartQualityNotificationRequest startQualityNotificationRequest) {
        return StartQualityNotificationDomain.builder()
                .partIds(startQualityNotificationRequest.getPartIds())
                .description(startQualityNotificationRequest.getDescription())
                .targetDate(startQualityNotificationRequest.getTargetDate())
                .severity(QualityNotificationSeverity.from(startQualityNotificationRequest.getSeverity()))
                .receiverBpn(startQualityNotificationRequest.getReceiverBpn())
                .isAsBuilt(startQualityNotificationRequest.isAsBuilt())
                .build();
    }


}
