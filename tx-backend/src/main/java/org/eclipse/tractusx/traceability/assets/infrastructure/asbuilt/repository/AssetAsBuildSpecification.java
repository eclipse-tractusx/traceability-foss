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

package org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaFilter;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.repository.BaseSpecification;
import org.glassfish.jersey.internal.guava.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;


public class AssetAsBuildSpecification extends BaseSpecification implements Specification<AssetAsBuiltEntity> {
    public AssetAsBuildSpecification(SearchCriteriaFilter criteria) {
        super(criteria);
    }

    @Override
    public Predicate toPredicate(@NotNull Root<AssetAsBuiltEntity> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder builder) {
        return createPredicate(getSearchCriteriaFilter(), root, builder);
    }

    public static Specification<AssetAsBuiltEntity> toSpecification(final List<org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.AssetAsBuildSpecification> allSpecifications, SearchCriteriaOperator searchCriteriaOperator) {
        var specifications = Lists.newArrayList(allSpecifications);
        if (specifications.isEmpty()) {
            return Specification.allOf();
        }
        Specification<AssetAsBuiltEntity> resultAnd = null;
        Specification<AssetAsBuiltEntity> resultOr = null;

        List<org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.AssetAsBuildSpecification> ownerSpecifications = specifications.stream()
                .filter(spec -> spec.isOwnerSpecification(spec)).toList();

        List<AssetAsBuildSpecification> otherSpecifications = specifications.stream()
                .filter(spec -> !spec.isOwnerSpecification(spec)).toList();

        // always add owner spec
        for (AssetAsBuildSpecification ownerSpecification : ownerSpecifications) {
            resultAnd = Specification.where(resultAnd).and(ownerSpecification);
        }

        if (searchCriteriaOperator.equals(SearchCriteriaOperator.AND)) {
            for (AssetAsBuildSpecification otherSpecification : otherSpecifications) {
                resultAnd = Specification.where(resultAnd).and(otherSpecification);
            }
        } else {
            for (AssetAsBuildSpecification otherSpecification : otherSpecifications) {
                resultOr = Specification.where(resultOr).and(otherSpecification);
            }
        }

        return Specification.where(resultAnd).and(resultOr);
    }

    private boolean isOwnerSpecification(AssetAsBuildSpecification specification) {
        return "owner".equals(specification.getSearchCriteriaFilter().getKey());
    }
}
