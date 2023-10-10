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

package org.eclipse.tractusx.traceability.assets.infrastructure.base.repository;

import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.repository.BaseSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AssetSpecificationUtil {

    public static <T> Specification<T> combineSpecifications(
            ArrayList<? extends BaseSpecification<T>> specifications,
            SearchCriteriaOperator searchCriteriaOperator) {

        List<? extends BaseSpecification<T>> ownerSpecifications = specifications.stream()
                .filter(AssetSpecificationUtil::isOwnerPredicate).toList();

        List<? extends BaseSpecification<T>> semanticDataModelSpecifications = specifications.stream()
                .filter(AssetSpecificationUtil::isSemanticDataModelPredicate).toList();

        List<? extends BaseSpecification<T>> otherSpecifications = specifications.stream()
                .filter(spec -> !isOwnerPredicate(spec) && !isSemanticDataModelPredicate(spec)).toList();

        Specification<T> resultAnd = null;
        Specification<T> resultOr = null;

        // Always add owner specifications with AND
        for (BaseSpecification<T> ownerSpecification : ownerSpecifications) {
            resultAnd = Specification.where(resultAnd).and(ownerSpecification);
        }
        // Always add semanticDataModel specifications with OR
        for (BaseSpecification<T> semanticDataModelSpecification : semanticDataModelSpecifications) {
            resultOr = Specification.where(resultOr).or(semanticDataModelSpecification);
        }
        if (searchCriteriaOperator.equals(SearchCriteriaOperator.AND)) {
            for (BaseSpecification<T> otherSpecification : otherSpecifications) {
                resultAnd = Specification.where(resultAnd).and(otherSpecification);
            }
        } else {
            for (BaseSpecification<T> otherSpecification : otherSpecifications) {
                resultOr = Specification.where(resultOr).or(otherSpecification);
            }
        }

        return Specification.where(resultAnd).and(resultOr);
    }


    private static boolean isOwnerPredicate(BaseSpecification<?> baseSpecification) {
        return "owner".equals(baseSpecification.getSearchCriteriaFilter().getKey());
    }

    private static boolean isSemanticDataModelPredicate(BaseSpecification<?> baseSpecification) {
        return "semanticDataModel".equals(baseSpecification.getSearchCriteriaFilter().getKey());
    }

}

