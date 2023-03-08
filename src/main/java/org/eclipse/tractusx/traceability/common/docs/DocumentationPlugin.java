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

package org.eclipse.tractusx.traceability.common.docs;

import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Collection;
import java.util.List;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class DocumentationPlugin implements OperationBuilderPlugin, ParameterBuilderPlugin {

	private final Environment environment;

	public DocumentationPlugin(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void apply(OperationContext context) {
		String operationKey = getOperationKey(context);

		context.operationBuilder()
			.notes(getProperty(operationKey + ".notes"))
			.summary(getProperty(operationKey + ".summary"));
	}

	@Override
	public void apply(ParameterContext parameterContext) {
		String operationKey = getOperationKey(parameterContext.getOperationContext());
		String parameterKey = operationKey + ".params." + parameterContext
			.resolvedMethodParameter()
			.defaultName()
			.orElse("default");

		parameterContext.requestParameterBuilder()
			.description(getProperty(parameterKey));
	}

	private String getOperationKey(OperationContext context) {
		return "operations." + context.requestMappingPattern()
			.replaceAll("/", ".")
			.replaceAll("\\{", "")
			.replaceAll("\\}", "")
			.substring(1);
	}

	private String getProperty(String key) {
		return environment.getProperty("swagger." + key);
	}

	private Collection<SecurityReference> buildJWTAuthentication(String operationKey) {
		String authKey = operationKey + ".authorizations";

		String authProperty = getProperty(authKey);

		return List.of(new SecurityReference(authProperty, new AuthorizationScope[]{}));
	}

	@Override
	public boolean supports(DocumentationType delimiter) {
		return true;
	}
}
