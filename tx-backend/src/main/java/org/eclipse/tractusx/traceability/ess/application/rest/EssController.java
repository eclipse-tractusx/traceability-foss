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

package org.eclipse.tractusx.traceability.ess.application.rest;

import com.neovisionaries.i18n.CountryCode;
import ess.request.EssRequest;
import ess.response.EssResponse;
import ess.response.EssStatus;
import ess.response.VEssResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.model.BaseRequestFieldMapper;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaRequestParam;
import org.eclipse.tractusx.traceability.ess.application.mapper.EssResponseMapper;
import org.eclipse.tractusx.traceability.ess.application.mapper.VEssResponseMapper;
import org.eclipse.tractusx.traceability.ess.application.service.EssService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER')")
@RequestMapping(path = "/ess", produces = "application/json", consumes = "application/json")
@RestController
@Slf4j
@RequiredArgsConstructor

@Tag(name = "ESS")
public class EssController {

    public static final String ESS_OFF = "ESS investigation handling has been turned off.";

    private final EssService service;
    private final BaseRequestFieldMapper fieldMapper;

    @Min(value = 0, message = "The value must be positive or zero. Zero turns off the creation of new investigations.")
    @Value("${ess.maxNumberOfNewInvestigations:0}")
    private Short essMaxNumberOfNewInvestigations;

    @GetMapping("/callback")
    public void callback(@RequestParam("jobId") String jobId, @RequestParam("jobState") String jobState) {
        if(this.essMaxNumberOfNewInvestigations > 0) {
            log.info("Callback from IRS for {}: status is {}", jobId, jobState);
            this.service.updateStatus(jobId);
        } else {
            log.info(EssController.ESS_OFF);
        }
    }

    @GetMapping("")
    public PageResult<EssResponse> getAllEss(OwnPageable pageable, SearchCriteriaRequestParam filter) {
        if(this.essMaxNumberOfNewInvestigations > 0) {
            return EssResponseMapper.fromAsPageResult(
                this.service.getAllEss(
                    OwnPageable.toPageable(pageable, fieldMapper),
                    filter.toSearchCriteria(fieldMapper)
                )
            );
        } else {
            log.info(EssController.ESS_OFF);
            return new PageResult<>(List.of());
        }
    }

    @GetMapping("/v")
    public PageResult<VEssResponse> getAllVEss(OwnPageable pageable, SearchCriteriaRequestParam filter) {
        if(this.essMaxNumberOfNewInvestigations > 0) {
            return VEssResponseMapper.fromAsPageResult(
                this.service.getAllVEss(
                    OwnPageable.toPageable(pageable, fieldMapper),
                    filter.toSearchCriteria(fieldMapper)
                )
            );
        } else {
            log.info(EssController.ESS_OFF);
            return new PageResult<>(List.of());
        }
    }

    @GetMapping("/countryCodes")
    public String getCountryCodes() {
        List<CountryCode> ccs =
            List.of(CountryCode.values()).stream()
                .filter(countryCode -> countryCode != CountryCode.UNDEFINED).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder("[");
        for (CountryCode cc : ccs) {
            sb.append(
                String.format(
                    "{ \"key2\": \"%s\", \"key3\": \"%s\", \"name\": \"%s\" }",
                    cc.getAlpha2(), cc.getAlpha3(), cc.getName()));
        }
        return sb.append("]").toString();
    }

    @PostMapping("")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public List<EssResponse> createEss(@RequestBody @Valid EssRequest request) throws UnknownHostException {
        if(this.essMaxNumberOfNewInvestigations > 0) {
            log.trace("Maximal number of new investigations: " + essMaxNumberOfNewInvestigations);
            if(essMaxNumberOfNewInvestigations > 0 && request.getPartIds().size() <= essMaxNumberOfNewInvestigations) {
                return this.service.createEss(request);
            } else {
                EssResponse response =
                    EssResponse.builder()
                        .message("The creation of new investigation has been turned off. " +
                                "Check the value of the 'ess.maxNumberOfNewInvestigations' property.")
                        .ess_status(EssStatus.OFF)
                        .build();
                log.info(response.getMessage());
                return List.of(response);
            }
        } else {
            log.info(EssController.ESS_OFF);
            return List.of();
        }
    }

}
