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

package org.eclipse.tractusx.traceability.bpn.domain.model;

import java.util.List;
import java.util.Objects;
import lombok.Builder;
import lombok.Data;
import org.eclipse.edc.spi.types.domain.edr.EndpointDataReference;
import org.springframework.http.HttpHeaders;

@Data
@Builder
public class BpdmRequest {

    private static final String LEGAL_ENTITIES_ENDPOINT = "/members/legal-entities/search";

    private String url;
    private HttpHeaders headers;
    private Body body;

    @Data
    @Builder
    public static class Body {
        List<String> bpnLs;
    }

    public static BpdmRequest toBpdmRequest(EndpointDataReference dataReference, String bpn) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(Objects.requireNonNull(dataReference.getAuthKey()), dataReference.getAuthCode());
        headers.set("Content-Type", "application/json");

        return BpdmRequest.builder()
                .headers(headers)
                .body(Body.builder().bpnLs(List.of(bpn)).build())
                .url(dataReference.getEndpoint() + LEGAL_ENTITIES_ENDPOINT)
                .build();
    }

}
