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

package org.eclipse.tractusx.traceability.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
public class OpenApiConfig {

    @Bean
    public OpenAPI baseOpenAPI() {
        Components components = new Components();
        components.addSecuritySchemes("oAuth2", new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows().clientCredentials(
                        new OAuthFlow().scopes(
                                        new Scopes().addString(
                                                "profile email", "")))));
        return new OpenAPI()
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList("oAuth2", "profile email"))
                .info(new Info()
                        .title("Trace-FOSS - OpenAPI Documentation")
                        .version("1.0.0")
                        .description("Trace-FOSS is a system for tracking parts along the supply chain. " +
                                "A high level of transparency across the supplier network enables faster intervention " +
                                "based on a recorded event in the supply chain. This saves costs by seamlessly tracking parts" +
                                " and creates trust through clearly defined and secure data access by the companies and persons" +
                                " involved in the process.")
                        .license(new License().name("License: Apache 2.0"))
                );
    }
}
