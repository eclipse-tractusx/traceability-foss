package assets.response;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@RequiredArgsConstructor
@Data
@SuperBuilder
public class AssetBaseResponse {
    @ApiModelProperty(example = "urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70528fcbd")
    @Size(max = 255)
    private String id;
    @ApiModelProperty(example = "--")
    @Size(max = 255)
    private String idShort;
    @ApiModelProperty(example = "--")
    @Size(max = 255)
    private String semanticModelId;
    @ApiModelProperty(example = "BPNL00000003CSGV")
    @Size(max = 255)
    private String manufacturerId;
    @ApiModelProperty(example = "Tier C")
    @Size(max = 255)
    private String manufacturerName;
    private SemanticModelResponse semanticModel;
    @ApiModelProperty(example = "CUSTOMER")
    private OwnerResponse owner;
    @ArraySchema(arraySchema = @Schema(description = "Child relationships", additionalProperties = Schema.AdditionalPropertiesValue.FALSE), maxItems = Integer.MAX_VALUE)
    private List<DescriptionsResponse> childRelations;
    @ArraySchema(arraySchema = @Schema(description = "Parent relationships", additionalProperties = Schema.AdditionalPropertiesValue.FALSE), maxItems = Integer.MAX_VALUE)
    private List<DescriptionsResponse> parentRelations;

    @ApiModelProperty(example = "false")
    private boolean activeAlert;
    @ApiModelProperty(example = "false")
    private boolean underInvestigation;
    @ApiModelProperty(example = "Ok")
    private QualityTypeResponse qualityType;
    @ApiModelProperty(example = "--")
    @Size(max = 255)
    private String van;
    @ApiModelProperty(example = "BATCH")
    private SemanticDataModelResponse semanticDataModel;
    @ApiModelProperty(example = "component")
    @Size(max = 255)
    private String classification;
}
