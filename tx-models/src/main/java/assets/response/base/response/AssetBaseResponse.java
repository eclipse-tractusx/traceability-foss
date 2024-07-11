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
package assets.response.base.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@RequiredArgsConstructor
@Data
@SuperBuilder
public class AssetBaseResponse {
    @Schema(example = "urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70528fcbd", maxLength = 255)
    @Size(max = 255)
    private String id;
    @Schema(example = "assembly-part-relationship", maxLength = 255)
    @Size(max = 255)
    private String idShort;
    @Schema(example = "NO-246880451848384868750731", maxLength = 255)
    @Size(max = 255)
    private String semanticModelId;
    @Schema(example = "BPNL00000003CSGV", maxLength = 255)
    @Size(max = 255)
    private String businessPartner;
    @Schema(example = "Tier C", maxLength = 255)
    @Size(max = 255)
    private String manufacturerName;
    @Schema(example = "Tier C", maxLength = 255)
    @Size(max = 255)
    private String nameAtManufacturer;
    @Schema(example = "Tier C", maxLength = 255)
    @Size(max = 255)
    private String manufacturerPartId;
    @Schema(example = "CUSTOMER")
    private OwnerResponse owner;
    @ArraySchema(arraySchema = @Schema(description = "Child relationships", additionalProperties = Schema.AdditionalPropertiesValue.FALSE), maxItems = Integer.MAX_VALUE)
    private List<DescriptionsResponse> childRelations;
    @ArraySchema(arraySchema = @Schema(description = "Parent relationships", additionalProperties = Schema.AdditionalPropertiesValue.FALSE), maxItems = Integer.MAX_VALUE)
    private List<DescriptionsResponse> parentRelations;
    @Schema(example = "Ok")
    private QualityTypeResponse qualityType;
    @Schema(example = "OMAYSKEITUGNVHKKX", maxLength = 255)
    @Size(max = 255)
    private String van;
    @Schema(example = "BATCH")
    private SemanticDataModelResponse semanticDataModel;
    @Schema(example = "component", maxLength = 255)
    @Size(max = 255)
    private String classification;
    private List<DetailAspectModelResponse> detailAspectModels;
    @Schema(type = "List", example = "1")
    private List<Long> sentQualityAlertIdsInStatusActive;
    @Schema(type = "List", example = "1")
    private List<Long> receivedQualityAlertIdsInStatusActive;
    @Schema(type = "List", example = "2")
    private List<Long> sentQualityInvestigationIdsInStatusActive;
    @Schema(type = "List", example = "2")
    private List<Long> receivedQualityInvestigationIdsInStatusActive;
    @Schema(example = "TRANSIENT")
    private ImportStateResponse importState;
    @Schema(example = "Asset created successfully in transient state")
    private String importNote;
    @Schema(example = """
                    {
                        "catenaXId": "urn:uuid:7e4541ea-bb0f-464c-8cb3-021abccbfaf5",
                        "endpointURL": "https://irs-provider-dataplane3.dev.demo.catena-x.net/api/public/data/urn:uuid:c7b3ea3d-97ea-41e4-960d-12fb166e1da1",
                        "processingError": {
                            "processStep": "SubmodelRequest",
                            "errorDetail": "org.springframework.web.client.HttpServerErrorException$InternalServerError: 500 : "{"errors":[]}"",
                            "lastAttempt": "2024-02-07T12:06:34.400493282Z",
                            "retryCounter": 0
                        },
                        "policy": null
                    }
            """)
    private String tombstone;


    @Schema(type = "List", example = "1,2,3")
    private final List<String> contractAgreementIds;
}
