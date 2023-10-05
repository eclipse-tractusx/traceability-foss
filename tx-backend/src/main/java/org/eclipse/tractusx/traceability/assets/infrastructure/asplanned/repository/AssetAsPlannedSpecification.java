/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaFilter;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.repository.BaseSpecification;
import org.glassfish.jersey.internal.guava.Lists;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@AllArgsConstructor
public class AssetAsPlannedSpecification extends BaseSpecification implements Specification<AssetAsPlannedEntity> {

    private SearchCriteriaFilter criteria;

    @Override
    public Predicate toPredicate(Root<AssetAsPlannedEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return createPredicate(criteria, root, builder);
    }

    public static Specification<AssetAsPlannedEntity> toSpecification(final List<AssetAsPlannedSpecification> allSpecifications, SearchCriteriaOperator searchCriteriaOperator) {
        var specifications = Lists.newArrayList(allSpecifications);
        if (specifications.isEmpty()) {
            return Specification.allOf();
        }
        Specification<AssetAsPlannedEntity> result = specifications.get(0);

        if (searchCriteriaOperator.equals(SearchCriteriaOperator.OR)) {
            List<AssetAsPlannedSpecification> ownerSpecifications = specifications.stream()
                    .filter(spec -> spec.isOwnerSpecification(spec)).toList();

            List<AssetAsPlannedSpecification> otherSpecifications = specifications.stream()
                    .filter(spec -> !spec.isOwnerSpecification(spec)).toList();
            for (int i = 1; i < ownerSpecifications.size(); i++) {
                result = Specification.where(result).and(ownerSpecifications.get(i));
            }
            if (otherSpecifications.size() == 1) {
                result = Specification.where(result).and(otherSpecifications.get(0));
            } else {
                for (int i = 1; i < otherSpecifications.size(); i++) {
                    result = Specification.where(result).or(otherSpecifications.get(i));
                }
            }

        } else {
            for (int i = 1; i < specifications.size(); i++) {
                result = Specification.where(result).and(specifications.get(i));
            }
        }

        return result;
    }

    private boolean isOwnerSpecification(AssetAsPlannedSpecification specification) {
        return "owner".equals(specification.criteria.getKey());
    }
}
