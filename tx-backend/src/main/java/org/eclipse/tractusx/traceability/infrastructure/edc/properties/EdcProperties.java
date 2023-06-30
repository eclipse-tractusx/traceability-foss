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
package org.eclipse.tractusx.traceability.infrastructure.edc.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Set;

@Getter
@ConfigurationProperties("edc")
public class EdcProperties {

    @NotBlank
    @Value("${edc.ids}")
    private String idsPath;

    @NotBlank
    @Value("${edc.negotiation}")
    private String negotiationPath;

    @NotBlank
    @Value("${edc.transfer}")
    private String transferPath;

    @NotBlank
    @Value("${edc.catalog}")
    private String catalogPath;

    @NotBlank
    @Value("${edc.policydefinitions}")
    private String policyDefinitionsPath;

    @NotBlank
    @Value("${edc.assets}")
    private String assetsPath;

    @NotBlank
    private String providerEdcUrl;

    @NotBlank
    private String apiAuthKey;

    private Map<String, String> bpnProviderUrlMappings;

    private Set<String> callbackUrls;

    public EdcProperties(String idsPath,
                         String negotiationPath,
                         String transferPath,
                         String catalogPath,
                         String providerEdcUrl,
                         String apiAuthKey,
                         Map<String, String> bpnProviderUrlMappings,
                         Set<String> callbackUrls) {
        this.idsPath = idsPath;
        this.negotiationPath = negotiationPath;
        this.transferPath = transferPath;
        this.catalogPath = catalogPath;
        this.providerEdcUrl = providerEdcUrl;
        this.apiAuthKey = apiAuthKey;
        this.bpnProviderUrlMappings = bpnProviderUrlMappings;
        this.callbackUrls = callbackUrls;
    }
}
