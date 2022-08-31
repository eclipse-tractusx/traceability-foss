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

package net.catenax.traceability.assets.infrastructure.config.openapi;

import net.catenax.traceability.common.properties.FeignProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties("feign.default")
public class FeignDefaultProperties extends FeignProperties {

	public FeignDefaultProperties(Long connectionTimeoutMillis,
								  Long readTimeoutMillis,
								  int maxIdleConnections,
								  Long keepAliveDurationMinutes) {
		super(connectionTimeoutMillis, readTimeoutMillis, maxIdleConnections, keepAliveDurationMinutes);
	}
}
