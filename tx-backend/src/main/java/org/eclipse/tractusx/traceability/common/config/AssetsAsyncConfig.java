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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AssetsAsyncConfig {

    public static final String SYNCHRONIZE_ASSETS_EXECUTOR = "synchronizeAssetsExecutor";
    public static final String LOAD_SHELL_DESCRIPTORS_EXECUTOR = "loadShellDescriptorsExecutor";
    public static final String UPDATE_NOTIFICATION_EXECUTOR = "updateNotificationExecutor";
    public static final String PUBLISH_ASSETS_EXECUTOR = "publishAssetsExecutor";

    @Bean(name = PUBLISH_ASSETS_EXECUTOR)
    public ThreadPoolTaskExecutor publishAssetsExecutor() {
        return buildExecutor(PUBLISH_ASSETS_EXECUTOR, 0);
    }

    @Bean(name = SYNCHRONIZE_ASSETS_EXECUTOR)
    public ThreadPoolTaskExecutor synchronizeAssetsExecutor() {
        return buildExecutor(SYNCHRONIZE_ASSETS_EXECUTOR, 0);
    }

    @Bean(name = LOAD_SHELL_DESCRIPTORS_EXECUTOR)
    public ThreadPoolTaskExecutor loadShellDescriptorsExecutor() {
        return buildExecutor(LOAD_SHELL_DESCRIPTORS_EXECUTOR, 50);
    }

    @Bean(name = UPDATE_NOTIFICATION_EXECUTOR)
    public ThreadPoolTaskExecutor updateNotificationExecutor() {
        return buildExecutor(UPDATE_NOTIFICATION_EXECUTOR, 50);
    }

    private ThreadPoolTaskExecutor buildExecutor(String name, int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(name + "-");
        return executor;
    }
}
