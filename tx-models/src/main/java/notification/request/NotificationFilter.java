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

package notification.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import common.FilterAttribute;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationFilter {
    private final FilterAttribute id;
    private final FilterAttribute description;
    private final FilterAttribute status;
    private final FilterAttribute severity;
    private final FilterAttribute createdDate;
    private final FilterAttribute createdBy;
    private final FilterAttribute createdByName;
    private final FilterAttribute sendTo;
    private final FilterAttribute sendToName;
    private final FilterAttribute reason;
    private final FilterAttribute assetId;
    private final FilterAttribute channel;
    private final FilterAttribute targetDate;
    private final FilterAttribute bpn;
    private final FilterAttribute errorMessage;
    private final FilterAttribute title;
    private final FilterAttribute contractAgreementId;
    private final FilterAttribute type;
}
