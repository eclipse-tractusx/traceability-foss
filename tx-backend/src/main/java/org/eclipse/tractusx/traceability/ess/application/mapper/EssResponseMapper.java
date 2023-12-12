/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

package org.eclipse.tractusx.traceability.ess.application.mapper;

import ess.response.EssResponse;
import java.util.List;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.ess.domain.model.EssEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class EssResponseMapper {

    public static EssResponse from(final EssEntity ess) {
        return EssResponse.builder()
            .id(ess.getId())
            .company(ess.getCompanyName())
            .bpns(ess.getBpns())
            .jobId(ess.getJobId())
            .status(ess.getStatus())
            .response(ess.getResponse())
            .build();
    }

    public static PageResult<EssResponse> from(final PageResult<EssEntity> essPageResult) {
        return new PageResult<>(
            essPageResult.content().stream().map(EssResponseMapper::from).toList(),
            essPageResult.page(),
            essPageResult.pageCount(),
            essPageResult.pageSize(),
            essPageResult.totalItems()
        );
    }

    public static PageResult<EssResponse> fromAsPageResult(PageResult<EssEntity> essEntityPageResult) {
        List<EssResponse> essResponses = essEntityPageResult.content().stream().map(EssResponseMapper::from).toList();
        int pageNumber = essEntityPageResult.page();
        int pageSize = essEntityPageResult.pageSize();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<EssResponse> essDataPage = new PageImpl<>(essResponses, pageable, essEntityPageResult.totalItems());
        return new PageResult<>(essDataPage);
    }

    public static List<EssResponse> from(final List<EssEntity> esss) {
        return esss.stream()
                .map(EssResponseMapper::from)
                .toList();
    }

}
