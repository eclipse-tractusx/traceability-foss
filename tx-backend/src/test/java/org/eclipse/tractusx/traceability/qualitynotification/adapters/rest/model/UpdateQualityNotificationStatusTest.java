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

package org.eclipse.tractusx.traceability.qualitynotification.adapters.rest.model;

import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class UpdateQualityNotificationStatusTest {

    @Test
    void testToInvestigationStatusACCEPTED() {
        String accepted = "ACCEPTED";
        QualityNotificationStatus actualInvestigationStatus = QualityNotificationStatus.fromStringValue(accepted);
        assertThat(actualInvestigationStatus.name()).isEqualTo("ACCEPTED");
    }

    @Test
    void testToInvestigationStatusACKNOWLEDGED() {
        String acknowledged = "ACKNOWLEDGED";
        QualityNotificationStatus actualInvestigationStatus = QualityNotificationStatus.fromStringValue(acknowledged);
        assertThat(actualInvestigationStatus.name()).isEqualTo("ACKNOWLEDGED");
    }

    @Test
    void testToInvestigationStatusDECLINED() {
        String declined = "DECLINED";
        QualityNotificationStatus actualInvestigationStatus = QualityNotificationStatus.fromStringValue(declined);
        assertThat(actualInvestigationStatus.name()).isEqualTo("DECLINED");
    }


}
