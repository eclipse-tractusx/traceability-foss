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

package org.eclipse.tractusx.traceability.ess.application.service;

import ess.request.EssRequest;
import ess.response.EssResponse;
import ess.response.EssStatusType;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.component.enums.JobState;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.IRSApiClient;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobDetailResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobStatus;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.RegisterJobResponse;
import org.eclipse.tractusx.traceability.bpdm.model.request.RegisterEssInvestigationJobRequest;
import org.eclipse.tractusx.traceability.bpdm.model.request.SiteSearchRequest;
import org.eclipse.tractusx.traceability.bpdm.model.response.SiteSearchResponse;
import org.eclipse.tractusx.traceability.bpdm.model.response.legal.LegalEntity;
import org.eclipse.tractusx.traceability.bpdm.service.BpdmApiClient;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.ess.EssSpecification;
import org.eclipse.tractusx.traceability.ess.domain.model.EssEntity;
import org.eclipse.tractusx.traceability.ess.domain.model.VEssEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Service
@Slf4j
public class EssService {

    @Value("#{systemProperties['feign.bpdmApi.url'] ?: false}")
    private boolean bpdmConnectionAvailable;

    @Autowired
    private ApplicationContext applicationContext;

    private final EssRepository essRepository;
    private final IRSApiClient irsClient;
    private final BpdmApiClient bpdmApiClient;
    private final VEssRepository vEssRepository;

    public EssService(EssRepository essRepository, IRSApiClient irsClient,
                      BpdmApiClient bpdmApiClient, VEssRepository vEssRepository) {
        this.essRepository = essRepository;
        this.irsClient = irsClient;
        this.bpdmApiClient = bpdmApiClient;
        this.vEssRepository = vEssRepository;
    }

    public PageResult<EssEntity> getAllEss(Pageable pageable, SearchCriteria searchCriteria) {
        List<EssSpecification> essSpecifications =
                emptyIfNull(searchCriteria.getSearchCriteriaFilterList()).stream()
                .map(EssSpecification::new)
                .toList();
        Specification<EssEntity> specification = EssSpecification.toSpecification(essSpecifications);
        return new PageResult<>(this.essRepository.findAll(pageable), EssEntity::toDomain);
    }

    public PageResult<VEssEntity> getAllVEss(Pageable pageable, SearchCriteria searchCriteria) {
        List<EssSpecification> essSpecifications =
                emptyIfNull(searchCriteria.getSearchCriteriaFilterList()).stream()
                        .map(EssSpecification::new)
                        .toList();
        return new PageResult<>(vEssRepository.findAll(pageable), VEssEntity::toDomain);
    }

    public List<EssResponse> createEss(EssRequest request) throws UnknownHostException {
        log.info("Creating new ESS investigation{} ...", (request.getPartIds().size() > 1 ? "s" : ""));
        List<EssResponse> rv = new ArrayList<>();
        for (String partId: request.getPartIds()) {
            RegisterEssInvestigationJobRequest registerEssInvestigationJobRequest =
                RegisterEssInvestigationJobRequest.buildRequest(
                    BomLifecycle.AS_PLANNED,
                    resolvCallback(
                        InetAddress.getLocalHost().getHostAddress(),
                        applicationContext.getBean(Environment.class).getProperty("server.port", Integer.class, 8080),
                    "/ess/callback?jobId={jobId}&jobState={jobState}"
                    ),
                    List.of(request.getBpns()), partId, (this.bpdmConnectionAvailable ? "" : "UNKNOWN"));
            RegisterJobResponse irsResponse = this.irsClient.registerEssInvestigationJob(registerEssInvestigationJobRequest);

            SiteSearchRequest siteSearchRequest = SiteSearchRequest.buildRequest(List.of((this.bpdmConnectionAvailable ? "" : "UNKNOWN")), List.of(request.getBpns()));
            SiteSearchResponse siteSearchResponse = this.bpdmApiClient.searchForBpnlByBpns(siteSearchRequest);
            List<LegalEntity> legalEntitySearchResponse =
                    this.bpdmApiClient.searchForLegalNameByBpnl(
                            List.of(siteSearchResponse.content().get(0).bpnLegalEntity()));
            String companyName = legalEntitySearchResponse.get(0).legalName();

            EssEntity saved = this.essRepository.save(EssEntity.builder()
                .id(UUID.randomUUID().toString())
                .partId(partId)
                .companyName(companyName)
                .bpns(request.getBpns())
                .jobId(irsResponse.id())
                .status(JobState.INITIAL.name())
                .impacted(null)
                .response(null)
                .build());
            rv.add(EssResponse.builder()
                .ess_status(EssStatusType.ON)
                .message("")
                .id(saved.getId())
                .partId(saved.getPartId())
                .company(saved.getCompanyName())
                .bpns(saved.getBpns())
                .jobId(saved.getJobId())
                .status(saved.getStatus())
                .impacted(saved.getImpacted())
                .response(saved.getResponse())
                .build());
        }
        return rv;
    }

    public void updateStatus(String jobId) {
        EssEntity savedEssEntity = this.essRepository.findByJobId(jobId);
        JobDetailResponse jobDetailResponse = this.irsClient.getEssInvestigationJobDetails(jobId);
        savedEssEntity.setStatus((jobDetailResponse.isCompleted() ? JobState.COMPLETED.name() : JobState.RUNNING.name()));
        savedEssEntity.setUpdated(jobDetailResponse.jobStatus().lastModifiedOn().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        savedEssEntity.setImpacted(jobDetailResponse.jobStatus().state());
        this.essRepository.save(savedEssEntity);
    }

    public List<EssEntity> getEssInvestigationsWithoutStatus() {
        return this.essRepository.findAllEssInvestigationsWithoutStatus();
    }

    private String resolvCallback(String host, Integer port, String uri) {
        return String.format("http://%s:%s%s", host, port, uri);
    }

}
