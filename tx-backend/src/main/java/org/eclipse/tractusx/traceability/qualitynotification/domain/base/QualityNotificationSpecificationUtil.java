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

        Specification<T> resultAnd = null;
        Specification<T> resultOr = null;

        if (searchCriteriaOperator.equals(SearchCriteriaOperator.AND)) {
            for (BaseSpecification<T> otherSpecification : specifications) {
                resultAnd = Specification.where(resultAnd).and(otherSpecification);
            }
        } else {
            for (BaseSpecification<T> otherSpecification : specifications) {
                resultOr = Specification.where(resultOr).or(otherSpecification);
            }
        }

        return Specification.where(resultAnd).and(resultOr);
    }
}

