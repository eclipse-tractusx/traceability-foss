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

package qualitynotification.base.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Schema(description = "The UpdateInvestigationStatus")
public enum UpdateQualityNotificationStatusRequest {
    ACKNOWLEDGED,
    ACCEPTED,
    DECLINED;


    @JsonCreator
    public static UpdateQualityNotificationStatusRequest fromValue(final String value) {
        return Stream.of(UpdateQualityNotificationStatusRequest.values())
                .filter(updateInvestigationStatus -> updateInvestigationStatus.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Unsupported UpdateInvestigationStatus: " + value
                        + ". Must be one of: " + supportedUpdateInvestigationStatus()));
    }

    private static String supportedUpdateInvestigationStatus() {
        return Stream.of(UpdateQualityNotificationStatusRequest.values()).map(Enum::name).collect(Collectors.joining(", "));
    }


}


