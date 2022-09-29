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

package net.catenax.traceability.assets.infrastructure.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AssetsAsyncConfig {

	public static final String SYNCHRONIZE_ASSETS_EXECUTOR = "synchronize-assets-executor";
	public static final String LOAD_SHELL_DESCRIPTORS_EXECUTOR = "load-shell-descriptors-executor";

	@Bean(name = SYNCHRONIZE_ASSETS_EXECUTOR)
	public ThreadPoolTaskExecutor synchronizeAssetsExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(100);
		executor.setThreadNamePrefix("%s-".formatted(SYNCHRONIZE_ASSETS_EXECUTOR));

		return executor;
	}

	@Bean(name = LOAD_SHELL_DESCRIPTORS_EXECUTOR)
	public ThreadPoolTaskExecutor loadShellDescriptorsExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(100);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("%s-".formatted(LOAD_SHELL_DESCRIPTORS_EXECUTOR));

		return executor;
	}
}
