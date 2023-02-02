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

package org.eclipse.tractusx.traceability.infrastructure.jpa.investigation;

import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.jpa.asset.AssetEntity;
import org.eclipse.tractusx.traceability.infrastructure.jpa.notification.NotificationEntity;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationSide;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "investigation")
public class InvestigationEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
		name = "assets_investigations",
		joinColumns = @JoinColumn(name = "investigation_id"),
		inverseJoinColumns = @JoinColumn(name = "asset_id")
	)
	private List<AssetEntity> assets;

	@OneToMany(mappedBy = "investigation")
	private List<NotificationEntity> notifications;

	private String bpn;
	private InvestigationStatus status;
	private InvestigationSide side;
	private String closeReason;
	private String acceptReason;
	private String declineReason;
	private String description;
	private Instant created;
	private Instant updated;

	public InvestigationEntity() {
	}

	public InvestigationEntity(Long id,
							   List<AssetEntity> assets,
							   String bpn,
							   InvestigationStatus status,
							   InvestigationSide side,
							   String closeReason,
							   String acceptReason,
							   String description,
							   Instant created,
							   Instant updated) {
		this.id = id;
		this.assets = assets;
		this.bpn = bpn;
		this.status = status;
		this.side = side;
		this.closeReason = closeReason;
		this.acceptReason = acceptReason;
		this.description = description;
		this.created = created;
		this.updated = updated;
	}

	public InvestigationEntity(List<AssetEntity> assets, String bpn, String description, InvestigationStatus status, InvestigationSide side, Instant created) {
		this(assets, bpn, status, side, "", description, created);
	}

	public InvestigationEntity(List<AssetEntity> assets, String bpn, InvestigationStatus status, InvestigationSide side, String closeReason, String description, Instant created) {
		this.assets = assets;
		this.bpn = bpn;
		this.status = status;
		this.side = side;
		this.closeReason = closeReason;
		this.description = description;
		this.created = created;
		this.updated = created;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<AssetEntity> getAssets() {
		return assets;
	}

	public void setAssets(List<AssetEntity> assets) {
		this.assets = assets;
	}

	public String getBpn() {
		return bpn;
	}

	public void setBpn(String bpn) {
		this.bpn = bpn;
	}

	public InvestigationStatus getStatus() {
		return status;
	}

	public void setStatus(InvestigationStatus status) {
		this.status = status;
	}

	public InvestigationSide getSide() {
		return side;
	}

	public void setSide(InvestigationSide side) {
		this.side = side;
	}

	public String getCloseReason() {
		return closeReason;
	}

	public void setCloseReason(String closeReason) {
		this.closeReason = closeReason;
	}

	public String getAcceptReason() {
		return acceptReason;
	}

	public void setAcceptReason(String acceptReason) {
		this.acceptReason = acceptReason;
	}

	public String getDeclineReason() {
		return declineReason;
	}

	public void setDeclineReason(String declineReason) {
		this.declineReason = declineReason;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Instant getCreated() {
		return created;
	}

	public void setCreated(Instant created) {
		this.created = created;
	}

	public Instant getUpdated() {
		return updated;
	}

	public void setUpdated(Instant updated) {
		this.updated = updated;
	}

	public List<NotificationEntity> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<NotificationEntity> notifications) {
		this.notifications = notifications;
	}
}
