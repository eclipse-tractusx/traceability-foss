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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A collection of permissions, prohibitions, and obligations. Subtypes are defined by
 * {@link PolicyType}.
 * This is a value object. In order to have it identifiable and individually addressable, consider the use of PolicyDefinition.
 */
@JsonDeserialize(builder = Policy.Builder.class)
@Getter
public class Policy {

    private final List<Permission> permissions = new ArrayList<>();
    private final List<Prohibition> prohibitions = new ArrayList<>();
    private final List<Duty> obligations = new ArrayList<>();
    private final Map<String, Object> extensibleProperties = new HashMap<>();
    private String inheritsFrom;
    private String assigner;
    private String assignee;
    private String target;
    @JsonProperty("@type")
    private PolicyType type = PolicyType.SET;

    @JsonIgnore
    public boolean hasTracePolicy() {
        if (permissions.isEmpty()) {
            return false;
        }

        List<AtomicConstraint> atomicConstraints = permissions.stream()
                .map(Rule::getConstraints)
                .flatMap(Collection::stream)
                .filter(AtomicConstraint.class::isInstance)
                .map(AtomicConstraint.class::cast)
                .toList();

        if (atomicConstraints.isEmpty()) {
            return false;
        }

        return atomicConstraints.stream().anyMatch(this::hasTraceConstraints);
    }

    @JsonIgnore
    public boolean hasTraceConstraints(AtomicConstraint atomicConstraint) {
        if (atomicConstraint == null) {
            return false;
        }

        Expression leftExpression = atomicConstraint.getLeftExpression();
        Expression rightExpression = atomicConstraint.getRightExpression();

        if (leftExpression == null || rightExpression == null) {
            return false;
        }

        if (!(leftExpression instanceof LiteralExpression) || !(rightExpression instanceof LiteralExpression)) {
            return false;
        }

        LiteralExpression leftLiteralExpression = (LiteralExpression) atomicConstraint.getLeftExpression();
        LiteralExpression rightLiteralExpression = (LiteralExpression) atomicConstraint.getRightExpression();

        return matchesValue(leftLiteralExpression, "idsc:PURPOSE") && matchesValue(rightLiteralExpression, "ID 3.0 Trace");
    }

    @JsonIgnore
    private static boolean matchesValue(LiteralExpression literalExpression, String anObject) {
        if (literalExpression.getValue() instanceof String literalExpressionValue) {
            return literalExpressionValue.equals(anObject);
        }
        return false;
    }

    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitPolicy(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissions, prohibitions, obligations, extensibleProperties, inheritsFrom, assigner, assignee, target, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Policy policy = (Policy) o;
        return permissions.equals(policy.permissions) && prohibitions.equals(policy.prohibitions) && obligations.equals(policy.obligations) && extensibleProperties.equals(policy.extensibleProperties) &&
                Objects.equals(inheritsFrom, policy.inheritsFrom) && Objects.equals(assigner, policy.assigner) && Objects.equals(assignee, policy.assignee) && Objects.equals(target, policy.target) && type == policy.type;
    }

    public interface Visitor<R> {
        R visitPolicy(Policy policy);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private final Policy policy;

        private Builder() {
            policy = new Policy();
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder prohibition(Prohibition prohibition) {
            policy.prohibitions.add(prohibition);
            return this;
        }

        public Builder prohibitions(List<Prohibition> prohibitions) {
            policy.prohibitions.addAll(prohibitions);
            return this;
        }

        public Builder permission(Permission permission) {
            policy.permissions.add(permission);
            return this;
        }

        public Builder permissions(List<Permission> permissions) {
            policy.permissions.addAll(permissions);
            return this;
        }

        public Builder duty(Duty duty) {
            policy.obligations.add(duty);
            return this;
        }

        @JsonProperty("obligations")
        public Builder duties(List<Duty> duties) {
            policy.obligations.addAll(duties);
            return this;
        }

        public Builder duty(String inheritsFrom) {
            policy.inheritsFrom = inheritsFrom;
            return this;
        }

        public Builder assigner(String assigner) {
            policy.assigner = assigner;
            return this;
        }

        public Builder assignee(String assignee) {
            policy.assignee = assignee;
            return this;
        }

        public Builder target(String target) {
            policy.target = target;
            return this;
        }

        public Builder inheritsFrom(String inheritsFrom) {
            policy.inheritsFrom = inheritsFrom;
            return this;
        }

        @JsonProperty("@type")
        public Builder type(PolicyType type) {
            policy.type = type;
            return this;
        }

        public Builder extensibleProperty(String key, Object value) {
            policy.extensibleProperties.put(key, value);
            return this;
        }

        public Builder extensibleProperties(Map<String, Object> properties) {
            policy.extensibleProperties.putAll(properties);
            return this;
        }

        public Policy build() {
            return policy;
        }
    }
}
