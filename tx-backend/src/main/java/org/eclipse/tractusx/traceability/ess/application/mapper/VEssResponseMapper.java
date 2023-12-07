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

import ess.response.VEssResponse;
import java.util.List;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.ess.domain.model.VEssEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class VEssResponseMapper {

    public static VEssResponse from(final VEssEntity ess) {
        return VEssResponse.builder()
            .id(ess.getId())
            .rowNumber(ess.getRowNumber())
            .jobId(ess.getJobId())
            .manufacturerPartId(ess.getManufacturerPartId())
            .nameAtManufacturer(ess.getNameAtManufacturer())
            .catenaxSiteId(ess.getCatenaxSiteId())
            .bpns(ess.getBpns())
            .companyName(ess.getCompanyName())
            .status(ess.getStatus())
            .impacted(ess.getImpacted())
            .response(ess.getResponse())
            .created(ess.getCreated())
            .updated(ess.getUpdated())
            .build();
    }

    public static PageResult<VEssResponse> from(final PageResult<VEssEntity> vEssPageResult) {
        return new PageResult<>(
            vEssPageResult.content().stream().map(VEssResponseMapper::from).toList(),
            vEssPageResult.page(),
            vEssPageResult.pageCount(),
            vEssPageResult.pageSize(),
            vEssPageResult.totalItems()
        );
    }

    public static PageResult<VEssResponse> fromAsPageResult(PageResult<VEssEntity> vEssEntityPageResult) {
        List<VEssResponse> vEssResponses = vEssEntityPageResult.content().stream().map(VEssResponseMapper::from).toList();
        int pageNumber = vEssEntityPageResult.page();
        int pageSize = vEssEntityPageResult.pageSize();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<VEssResponse> vEssDataPage = new PageImpl<>(vEssResponses, pageable, vEssEntityPageResult.totalItems());
        return new PageResult<>(vEssDataPage);
    }

    public static List<VEssResponse> from(final List<VEssEntity> esss) {
        return esss.stream()
                .map(VEssResponseMapper::from)
                .toList();
    }

}
