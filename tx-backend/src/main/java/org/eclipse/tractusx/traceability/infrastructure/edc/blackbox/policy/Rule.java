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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * A permission, prohibition, or duty contained in a {@link Policy}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "edctype")
public abstract class Rule extends Identifiable {

	protected String target;
	protected Action action;
	protected String assignee;
	protected String assigner;
	protected List<Constraint> constraints = new ArrayList<>();

	public String getTarget() {
		return target;
	}

	public Action getAction() {
		return action;
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}

	public String getAssigner() {
		return assigner;
	}

	public String getAssignee() {
		return assignee;
	}

	public abstract <R> R accept(Visitor<R> visitor);

	public interface Visitor<R> {
		R visitPermission(Permission policy);

		R visitProhibition(Prohibition policy);

		R visitDuty(Duty policy);
	}

	@SuppressWarnings("unchecked")
	protected abstract static class Builder<T extends Rule, B extends Builder<T, B>> {
		protected T rule;

		public B target(String target) {
			rule.target = target;
			return (B) this;
		}

		public B assigner(String assigner) {
			rule.assigner = assigner;
			return (B) this;
		}

		public B assignee(String assignee) {
			rule.assignee = assignee;
			return (B) this;
		}

		public B action(Action action) {
			rule.action = action;
			return (B) this;
		}

		public B constraint(Constraint constraint) {
			rule.constraints.add(constraint);
			return (B) this;
		}

		public B constraints(List<Constraint> constraints) {
			rule.constraints.addAll(constraints);
			return (B) this;
		}

	}

}
