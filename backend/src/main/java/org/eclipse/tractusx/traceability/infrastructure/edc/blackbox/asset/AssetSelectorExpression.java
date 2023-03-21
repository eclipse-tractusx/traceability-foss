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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.asset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.query.Criterion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Selects a group of assets based on the given criteria.
 * <p>
 * If an AssetSelectorExpression does not contain any criteria, no assets are selected. If all Assets are to be selected, the {@link AssetSelectorExpression#SELECT_ALL} constant
 * must be used.
 */
@JsonDeserialize(builder = AssetSelectorExpression.Builder.class)
public final class AssetSelectorExpression {


	public static final AssetSelectorExpression SELECT_ALL = new AssetSelectorExpression();
	private List<Criterion> criteria;

	private AssetSelectorExpression() {
		criteria = new ArrayList<>();
	}

	public List<Criterion> getCriteria() {
		return criteria;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AssetSelectorExpression that = (AssetSelectorExpression) o;
		return criteria == that.criteria || criteria.equals(that.criteria);
	}

	@Override
	public int hashCode() {
		return Objects.hash(criteria);
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static final class Builder {
		private final AssetSelectorExpression expression;

		private Builder() {
			expression = new AssetSelectorExpression();
		}

		@JsonCreator
		public static Builder newInstance() {
			return new Builder();
		}

		public Builder criteria(List<Criterion> criteria) {
			expression.criteria = criteria;
			return this;
		}

		@JsonIgnore
		public Builder constraint(String left, String op, Object right) {
			expression.criteria.add(new Criterion(left, op, right));
			return this;
		}

		/**
		 * Convenience method to express equality checks. Is equivalent to {@code Builder.withConstraint(key, "=", value)}
		 *
		 * @param key   left-hand operand
		 * @param value right-hand operand
		 */
		@JsonIgnore
		public Builder whenEquals(String key, String value) {
			expression.criteria.add(new Criterion(key, "=", value));
			return this;
		}

		public AssetSelectorExpression build() {
			return expression;
		}
	}

}
