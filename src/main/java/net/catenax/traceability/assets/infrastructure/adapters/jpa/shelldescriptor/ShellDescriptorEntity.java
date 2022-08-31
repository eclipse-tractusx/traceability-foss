/*
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
 */

package net.catenax.traceability.assets.infrastructure.adapters.jpa.shelldescriptor;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "shell_descriptor")
public class ShellDescriptorEntity {
	private Long id;
	private ZonedDateTime created;
	private ZonedDateTime updated;
	private String shellDescriptorId;
	private String globalAssetId;
	private String rawDescriptor;

	public ShellDescriptorEntity() {}

	public ShellDescriptorEntity(ZonedDateTime created, ZonedDateTime updated, String shellDescriptorId, String globalAssetId, String rawDescriptor) {
		this.shellDescriptorId = shellDescriptorId;
		this.globalAssetId = globalAssetId;
		this.created = created;
		this.updated = updated;
		this.rawDescriptor = rawDescriptor;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getShellDescriptorId() {
		return shellDescriptorId;
	}

	public void setShellDescriptorId(String shellDescriptorId) {
		this.shellDescriptorId = shellDescriptorId;
	}

	public String getGlobalAssetId() {
		return globalAssetId;
	}

	public void setGlobalAssetId(String globalAssetId) {
		this.globalAssetId = globalAssetId;
	}

	public ZonedDateTime getCreated() {
		return created;
	}

	public void setCreated(ZonedDateTime created) {
		this.created = created;
	}

	public ZonedDateTime getUpdated() {
		return updated;
	}

	public void setUpdated(ZonedDateTime updated) {
		this.updated = updated;
	}

	public String getRawDescriptor() {
		return rawDescriptor;
	}

	public void setRawDescriptor(String rawDescriptor) {
		this.rawDescriptor = rawDescriptor;
	}
}
