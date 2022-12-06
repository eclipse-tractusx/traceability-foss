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
 * A collection of child constraints. Subclasses define the semantics for when this constraint is satisfied.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "edctype")
public abstract class MultiplicityConstraint extends Constraint {
	protected List<Constraint> constraints = new ArrayList<>();

	public List<Constraint> getConstraints() {
		return constraints;
	}

	/**
	 * Creates another instance of the constraint with the given child constraints.
	 */
	public abstract MultiplicityConstraint create(List<Constraint> constraints);

	protected abstract static class Builder<T extends MultiplicityConstraint, B extends Builder<T, B>> {
		protected T constraint;

		public B constraint(Constraint constraint) {
			this.constraint.constraints.add(constraint);
			return (B) this;
		}

		public B constraints(List<Constraint> constraints) {
			constraint.constraints.addAll(constraints);
			return (B) this;
		}

	}

}
