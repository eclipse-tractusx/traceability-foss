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

import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Set.of;


public enum InvestigationStatus {
	CREATED(InvestigationSide.SENDER, emptySet()),
	APPROVED(InvestigationSide.SENDER, of(InvestigationSide.SENDER)),
	SENT(InvestigationSide.SENDER, of(InvestigationSide.SENDER)),
	RECEIVED(InvestigationSide.RECEIVER, emptySet()),
	ACKNOWLEDGED(InvestigationSide.RECEIVER, of(InvestigationSide.RECEIVER)),
	ACCEPTED(InvestigationSide.RECEIVER, of(InvestigationSide.RECEIVER)),
	DECLINED(InvestigationSide.RECEIVER, of(InvestigationSide.RECEIVER)),
	CANCELED(InvestigationSide.SENDER, of(InvestigationSide.SENDER)),
	CLOSED(InvestigationSide.SENDER, of(InvestigationSide.SENDER, InvestigationSide.RECEIVER));

	private final InvestigationSide investigationSide;
	private final Set<InvestigationSide> allowedTransitionFromSide;

	InvestigationStatus(InvestigationSide investigationSide, Set<InvestigationSide> allowedTransitionFromSide) {
		this.investigationSide = investigationSide;
		this.allowedTransitionFromSide = allowedTransitionFromSide;
	}

	private static final Map<InvestigationStatus, Set<InvestigationStatus>> STATE_MACHINE;

	private static final Set<InvestigationStatus> NO_TRANSITION_ALLOWED = emptySet();

	static {
		STATE_MACHINE = Map.of(
			CREATED, of(APPROVED, CANCELED),
			APPROVED, of(SENT, CLOSED),
			SENT, of(RECEIVED, CLOSED),
			RECEIVED, of(ACKNOWLEDGED, CLOSED),
			ACKNOWLEDGED, of(DECLINED, ACCEPTED, CLOSED),
			ACCEPTED, of(CLOSED),
			DECLINED, of(CLOSED),
			CLOSED, NO_TRANSITION_ALLOWED,
			CANCELED, NO_TRANSITION_ALLOWED
		);
	}

	public boolean transitionAllowed(InvestigationStatus to) {
		Set<InvestigationStatus> allowedStatusesToTransition = STATE_MACHINE.get(this);

		if (!allowedStatusesToTransition.contains(to)) {
			return false;
		}

		return isSideEligibleForTransition(this, to);
	}

	private boolean isSideEligibleForTransition(InvestigationStatus from, InvestigationStatus to) {
		return to.allowedTransitionFromSide.contains(from.investigationSide);
	}
}
