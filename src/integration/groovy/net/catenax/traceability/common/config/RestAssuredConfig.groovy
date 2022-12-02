/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

package net.catenax.traceability.common.config

import io.restassured.RestAssured
import net.catenax.traceability.investigations.domain.ports.EDCUrlProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.context.WebServerInitializedEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.event.EventListener

@TestConfiguration
class RestAssuredConfig {

	private static final String DEFAULT_EDC_URL = "http://localhost:9999/api/edc";

	@EventListener(WebServerInitializedEvent.class)
	void onServletContainerInitialized(WebServerInitializedEvent event) {
		RestAssured.port = event.webServer.port
	}

	@Bean
	EDCUrlProvider mockEDCUrlProvider(@Value('${traceability.bpn}') String defaultBPN) {
		return new EDCUrlProvider() {
			@Override
			String getEdcUrl(String bpn) {
				return DEFAULT_EDC_URL
			}

			@Override
			String getSenderUrl() {
				return DEFAULT_EDC_URL
			}

			@Override
			String getSenderBpn() {
				return defaultBPN
			}
		}
	}
}
