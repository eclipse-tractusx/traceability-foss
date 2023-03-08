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

import static java.util.stream.Collectors.joining;

/**
 * Disallows an action if its constraints are satisfied.
 */
public class Prohibition extends Rule {

	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitProhibition(this);
	}

	@Override
	public String toString() {
		return "Prohibition constraints: [" + getConstraints().stream().map(Object::toString).collect(joining(",")) + "]";
	}

	/**
	 * Returns a copy of this prohibition with the specified target.
	 *
	 * @param target the target.
	 * @return a copy with the specified target.
	 */
	public Prohibition withTarget(String target) {
		return Builder.newInstance()
			.uid(this.uid)
			.assigner(this.assigner)
			.assignee(this.assignee)
			.action(this.action)
			.constraints(this.constraints)
			.target(target)
			.build();
	}

	public static class Builder extends Rule.Builder<Prohibition, Builder> {

		private Builder() {
			rule = new Prohibition();
		}

		public static Builder newInstance() {
			return new Builder();
		}

		public Builder uid(String uid) {
			rule.uid = uid;
			return this;
		}

		public Prohibition build() {
			return rule;
		}
	}
}
