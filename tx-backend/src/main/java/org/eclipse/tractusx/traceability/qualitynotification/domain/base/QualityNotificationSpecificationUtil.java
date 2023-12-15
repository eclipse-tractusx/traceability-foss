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

package org.eclipse.tractusx.traceability.qualitynotification.domain.base;

import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.repository.BaseSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class QualityNotificationSpecificationUtil {

    public static <T> Specification<T> combineSpecifications(
            List<? extends BaseSpecification<T>> specifications,
            SearchCriteriaOperator searchCriteriaOperator) {

        List<? extends BaseSpecification<T>> sideSpecifications = specifications.stream()
                .filter(QualityNotificationSpecificationUtil::isSidePredicate).toList();

        List<? extends BaseSpecification<T>> statusSpecifications = specifications.stream()
                .filter(QualityNotificationSpecificationUtil::isStatusPredicate).toList();

        List<? extends BaseSpecification<T>> severitySpecifications = specifications.stream()
                .filter(QualityNotificationSpecificationUtil::isSeverityPredicate).toList();

        List<? extends BaseSpecification<T>> otherSpecifications = specifications.stream()
                .filter(spec -> !isSidePredicate(spec) && !isStatusPredicate(spec) && !isSeverityPredicate(spec)).toList();

        Specification<T> resultAnd = null;
        Specification<T> resultOr = null;
        Specification<T> resultStatus = null;
        Specification<T> resultSeverity = null;
        Specification<T> resultSide = null;

        for (BaseSpecification<T> sideSpecification : sideSpecifications) {
            resultSide = Specification.where(resultStatus).or(sideSpecification);
        }

        for (BaseSpecification<T> statusSpecification : statusSpecifications) {
            resultStatus = Specification.where(resultStatus).or(statusSpecification);
        }

        for (BaseSpecification<T> severitySpecification : severitySpecifications) {
            resultSeverity = Specification.where(resultSeverity).or(severitySpecification);
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

        return Specification.where(resultAnd).and(resultStatus).and(resultSeverity).and(resultOr).and(resultSide);
    }

    private static boolean isStatusPredicate(BaseSpecification<?> baseSpecification) {
        return "status".equals(baseSpecification.getSearchCriteriaFilter().getKey());
    }

    private static boolean isSeverityPredicate(BaseSpecification<?> baseSpecification) {
        return "severity".equals(baseSpecification.getSearchCriteriaFilter().getKey());
    }

    private static boolean isSidePredicate(BaseSpecification<?> baseSpecification) {
        return "side".equals(baseSpecification.getSearchCriteriaFilter().getKey());
    }

}

