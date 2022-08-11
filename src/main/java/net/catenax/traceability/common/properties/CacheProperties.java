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

package net.catenax.traceability.common.properties;

import java.time.Duration;

public class CacheProperties {

	private final String name;
	private final int maximumSize;
	private final Duration expireAfterWrite;

	public CacheProperties(String name, int maximumSize, Duration expireAfterWrite) {
		this.name = name;
		this.maximumSize = maximumSize;
		this.expireAfterWrite = expireAfterWrite;
	}

	public String getName() {
		return name;
	}

	public int getMaximumSize() {
		return maximumSize;
	}

	public Duration getExpireAfterWrite() {
		return expireAfterWrite;
	}
}
