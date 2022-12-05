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

package org.eclipse.tractusx.traceability.common.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer

@TestConfiguration
class PostgreSQLConfig {

	private static final DATABASE_NAME = "traceability-integration-tests"
	private static final DATABASE_USERNAME = "test"
	private static final DATABASE_PASSWORD = "test"

	private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER

	static {
		POSTGRE_SQL_CONTAINER = new PostgreSQLContainer("postgres:14.4")
			.withDatabaseName(DATABASE_NAME)
			.withUsername(DATABASE_USERNAME)
			.withPassword(DATABASE_PASSWORD)

		POSTGRE_SQL_CONTAINER.start()
	}

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues.of(
				"spring.datasource.url=${POSTGRE_SQL_CONTAINER.jdbcUrl}",
				"spring.datasource.username=${DATABASE_USERNAME}",
				"spring.datasource.password=${DATABASE_PASSWORD}",
			).applyTo(configurableApplicationContext.getEnvironment())
		}
	}
}
