/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.assets.domain.importpoc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.tractusx.irs.component.assetadministrationshell.AssetAdministrationShellDescriptor;
import org.eclipse.tractusx.irs.registryclient.decentral.DigitalTwinRegistryCreateShellService;
import org.eclipse.tractusx.irs.registryclient.decentral.exception.CreateDtrShellException;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.repository.SubmodelPayloadRepository;
import org.eclipse.tractusx.traceability.common.properties.EdcProperties;
import org.eclipse.tractusx.traceability.common.properties.RegistryProperties;
import org.eclipse.tractusx.traceability.submodel.domain.repository.SubmodelServerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DtrServiceTest {

  @Mock
  private DigitalTwinRegistryCreateShellService dtrCreateShellService;

  @Mock
  private EdcProperties edcProperties;

  @Mock
  private RegistryProperties registryProperties;

  @Mock
  private SubmodelPayloadRepository submodelPayloadRepository;

  @Mock
  private SubmodelServerRepository submodelServerRepository;

  @Captor
  private ArgumentCaptor<AssetAdministrationShellDescriptor> assetAdministrationShellDescriptorArgumentCaptor;

  private DtrService dtrService;

  @BeforeEach
  void setUp() {
    dtrService = new DtrService(
        dtrCreateShellService,
        submodelPayloadRepository,
        submodelServerRepository,
        edcProperties,
        registryProperties,
        new ObjectMapper());
  }

  @Test
  void shouldCreateShellInDtr() throws CreateDtrShellException {
    // given
    final String assetBaseId = "assetId";
    final String shortId = "shortId";
    final String manufacturerId = "manufacturerId";
    final String manufacturerPartId = "manufacturerPartId";
    final String digitalTwinType = "digitalTwinType";

    AssetBase assetBase = AssetBase.builder()
        .id(assetBaseId)
        .idShort(shortId)
        .manufacturerId(manufacturerId)
        .digitalTwinType(digitalTwinType)
        .manufacturerPartId(manufacturerPartId)
        .build();

    String submodelPayload = """
            {"catenaXId":"urn:uuid:1310c43c-a8c8-4221-9a6b-1b86533e0c55","localIdentifiers":[{"key":"manufacturerId","value":"BPNL000000000IAX"},{"key":"partInstanceId","value":"NO-313869652971440618042264"}],"manufacturingInformation":{"date":"2022-02-04T14:48:54.000Z","country":"DEU","sites":[]},"partTypeInformation":{"manufacturerPartId":"22782277-50","customerPartId":"22782277-50","nameAtManufacturer":"Example Car 1","nameAtCustomer":"Example Car 1","partClassification":[{"classificationStandard":"GIN 20510-21513","classificationID":"1004712","classificationDescription":"Generic standard for classification of parts in the automotive industry."},{"classificationStandard":"OEM Part Classification 1022-102","classificationID":"Electric vehicle","classificationDescription":"OEM standard for classification of parts."}]}}""";

    when(submodelPayloadRepository.getAspectTypesAndPayloadsByAssetId(eq(assetBaseId))).thenReturn(
        Map.of("urn:samm:io.catenax.serial_part:3.0.0#SerialPart", submodelPayload));
    when(submodelServerRepository.saveSubmodel(any())).thenReturn(UUID.randomUUID().toString());
    when(registryProperties.getAllowedBpns()).thenReturn("BPNL000000000001");

    // when
    String result = dtrService.createShellInDtr(assetBase, null);

    // then
    assertThat(assetBaseId).isEqualTo(result);
    verify(dtrCreateShellService).createShell(assetAdministrationShellDescriptorArgumentCaptor.capture());

    AssetAdministrationShellDescriptor createdAas = assetAdministrationShellDescriptorArgumentCaptor.getValue();

    assertThat(createdAas.getGlobalAssetId()).isEqualTo(assetBaseId);
    assertThat(createdAas.getIdShort()).isEqualTo(shortId);
    assertThat(createdAas.getSpecificAssetIds().size()).isEqualTo(3);
    assertTrue(createdAas.getSpecificAssetIds().stream().anyMatch(specificAssetId -> specificAssetId.getValue().equals(manufacturerId)));
    assertTrue(createdAas.getSpecificAssetIds().stream().anyMatch(specificAssetId -> specificAssetId.getValue().equals(manufacturerPartId)));
    assertTrue(createdAas.getSpecificAssetIds().stream().anyMatch(specificAssetId -> specificAssetId.getValue().equals(digitalTwinType)));
  }

}
