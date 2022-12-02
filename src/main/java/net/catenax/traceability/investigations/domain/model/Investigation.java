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

package net.catenax.traceability.investigations.domain.model;

import net.catenax.traceability.common.model.BPN;
import net.catenax.traceability.investigations.adapters.rest.model.InvestigationData;
import net.catenax.traceability.investigations.domain.model.exception.InvestigationIllegalUpdate;
import net.catenax.traceability.investigations.domain.model.exception.InvestigationStatusTransitionNotAllowed;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Investigation {

	public static final Comparator<Investigation> COMPARE_BY_NEWEST_INVESTIGATION_CREATION_TIME = (o1, o2) -> {
		Instant o1CreationTime = o1.createdAt;
		Instant o2CreationTime = o2.createdAt;

		if (o1CreationTime.equals(o2CreationTime)) {
			return 0;
		}

		if (o1CreationTime.isBefore(o2CreationTime)) {
			return 1;
		}

		return -1;
	};

	public static final Set<InvestigationStatus> CREATED_STATUSES = Set.of(InvestigationStatus.CREATED, InvestigationStatus.APPROVED, InvestigationStatus.SENT);
	public static final Set<InvestigationStatus> RECEIVED_STATUSES = Set.of(InvestigationStatus.RECEIVED);

	private final InvestigationId investigationId;
	private final BPN bpn;
	private InvestigationStatus investigationStatus;
	private final String description;
	private final Instant createdAt;
	private final List<String> assetIds;
	private final Map<String, Notification> notifications;
	private String closeReason;

	public Investigation(InvestigationId investigationId,
						 BPN bpn,
						 InvestigationStatus investigationStatus,
						 String closeReason,
						 String description,
						 Instant createdAt,
						 List<String> assetIds,
						 List<Notification> notifications
	) {
		this.investigationId = investigationId;
		this.bpn = bpn;
		this.investigationStatus = investigationStatus;
		this.closeReason = closeReason;
		this.description = description;
		this.createdAt = createdAt;
		this.assetIds = assetIds;
		this.notifications = notifications.stream()
			.collect(Collectors.toMap(Notification::getId, Function.identity()));;
	}

	public static Investigation startInvestigation(Instant createDate, BPN bpn, String description) {
		return new Investigation(null,
			bpn,
			InvestigationStatus.CREATED,
			null,
			description,
			createDate,
			new ArrayList<>(),
			new ArrayList<>()
		);
	}

	public static Investigation receiveInvestigation(Instant createDate, BPN bpn, String description) {
		return new Investigation(
			null,
			bpn,
			InvestigationStatus.RECEIVED,
			null,
			description,
			createDate,
			new ArrayList<>(),
			new ArrayList<>()
		);
	}

	public List<String> getAssetIds() {
		return Collections.unmodifiableList(assetIds);
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public String getDescription() {
		return description;
	}

	public InvestigationData toData() {
		return new InvestigationData(
			investigationId.value(),
			investigationStatus.name(),
			description,
			bpn.value(),
			createdAt.toString(),
			Collections.unmodifiableList(assetIds)
		);
	}

	public boolean hasIdentity() {
		return investigationId != null;
	}

	public String getBpn() {
		return bpn.value();
	}

	public void cancel(BPN callerBpn) {
		validateBPN(callerBpn);

		changeStatusTo(InvestigationStatus.CANCELED);

		this.closeReason = "canceled";
	}

	public void close(BPN callerBpn, String reason) {
		validateBPN(callerBpn);

		changeStatusTo(InvestigationStatus.CLOSED);

		this.closeReason = reason;
	}

	public void approve(BPN callerBpn) {
		validateBPN(callerBpn);

		changeStatusTo(InvestigationStatus.APPROVED);
	}

	private void validateBPN(BPN callerBpn) {
		if (!callerBpn.equals(this.bpn)) {
			throw new InvestigationIllegalUpdate("%s bpn has no permissions to update investigation with %s id.".formatted(callerBpn.value(), investigationId.value()));
		}
	}

	private void changeStatusTo(InvestigationStatus to) {
		notifications.values()
			.forEach(notification -> notification.changeStatusTo(to));

		boolean transitionAllowed = investigationStatus.transitionAllowed(to);

		if (!transitionAllowed) {
			throw new InvestigationStatusTransitionNotAllowed(investigationId, investigationStatus, to);
		}

		this.investigationStatus = to;
	}

	public InvestigationId getId() {
		return investigationId;
	}

	public Instant getCreationTime() {
		return createdAt;
	}

	public List<Notification> getNotifications() {
		return new ArrayList<>(notifications.values());
	}

	public Optional<Notification> getNotification(String notificationId) {
		return Optional.ofNullable(notifications.get(notificationId));
	}

	public void addNotification(Notification notification) {
		notifications.put(notification.getId(), notification);

		notification.getAffectedParts().stream()
			.map(AffectedPart::assetId)
			.forEach(assetIds::add);
	}
}
