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

import org.eclipse.tractusx.traceability.common.docs.SwaggerPageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.OAuth2SchemeBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.List;

@EnableOpenApi
@Configuration
public class OpenApiConfig {

	private static final AuthorizationScope[] DEFAULT_SCOPES = {
		new AuthorizationScope("uma_authorization", "UMA authorization")
	};

	@Value("${spring.security.oauth2.client.provider.default.token-uri}")
	private String oauthTokenUrl;

	@Bean
	public Docket swaggerSpringMvcPlugin() {
		return new Docket(DocumentationType.OAS_30)
			.securitySchemes(List.of(oauthAuthenticationScheme()))
			.securityContexts(List.of(securityContext()))
			.apiInfo(apiInfo())
			.select()
			.apis(RequestHandlerSelectors.basePackage("org.eclipse.tractusx.traceability"))
			.build()
			.forCodeGeneration(true)
			.directModelSubstitute(Pageable.class, SwaggerPageable.class);
	}

	private SecurityScheme oauthAuthenticationScheme() {
		return new OAuth2SchemeBuilder("clientCredentials")
			.name("default")
			.authorizationUrl(oauthTokenUrl.replace("token", "auth"))
			.tokenUrl(oauthTokenUrl)
			.scopes(Arrays.asList(DEFAULT_SCOPES))
			.build();
	}

	private SecurityContext securityContext() {
		return SecurityContext
			.builder()
			.securityReferences(List.of(
				new SecurityReference("default", DEFAULT_SCOPES)
			))
			.operationSelector(operationContext -> HttpMethod.GET.equals(operationContext.httpMethod()))
			.build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title("Trace-FOSS - OpenAPI Documentation")
			.description("Trace-FOSS is a system for tracking parts along the supply chain. " +
				"A high level of transparency across the supplier network enables faster intervention " +
				"based on a recorded event in the supply chain. This saves costs by seamlessly tracking parts" +
				" and creates trust through clearly defined and secure data access by the companies and persons" +
				" involved in the process.")
			.version("1.0.0")
			.license("License: Apache 2.0")
			.build();
	}

}
