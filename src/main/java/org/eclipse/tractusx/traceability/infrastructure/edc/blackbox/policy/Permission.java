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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * Allows an action if its constraints are satisfied.
 */
@JsonDeserialize(builder = Permission.Builder.class)
@JsonTypeName("dataspaceconnector:permission")
public class Permission extends Rule {
	private final List<Duty> duties = new ArrayList<>();

	public List<Duty> getDuties() {
		return duties;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitPermission(this);
	}

	@Override
	public String toString() {
		return "Permission constraints: [" + getConstraints().stream().map(Object::toString).collect(joining(",")) + "]";
	}

	/**
	 * Returns a copy of this permission with the specified target.
	 *
	 * @param target the target.
	 * @return a copy with the specified target.
	 */
	public Permission withTarget(String target) {
		return Builder.newInstance()
			.uid(this.uid)
			.assigner(this.assigner)
			.assignee(this.assignee)
			.action(this.action)
			.constraints(this.constraints)
			.duties(this.duties.stream().map(d -> d.withTarget(target)).collect(Collectors.toList()))
			.target(target)
			.build();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends Rule.Builder<Permission, Builder> {

		private Builder() {
			rule = new Permission();
		}

		@JsonCreator
		public static Builder newInstance() {
			return new Builder();
		}

		public Builder duty(Duty duty) {
			duty.setParentPermission(rule);
			rule.duties.add(duty);
			return this;
		}

		public Builder uid(String uid) {
			rule.uid = uid;
			return this;
		}

		public Builder duties(List<Duty> duties) {
			for (var duty : duties) {
				duty.setParentPermission(rule);
			}
			rule.duties.addAll(duties);
			return this;
		}

		public Permission build() {
			return rule;
		}
	}
}
