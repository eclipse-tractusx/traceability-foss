package assets.response.asplanned;

import assets.response.base.DetailAspectDataResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartSiteInformationAsPlannedResponse implements DetailAspectDataResponse {
    @ApiModelProperty(example = "2025-02-08T04:30:48.000Z")
    private String functionValidUntil;
    @ApiModelProperty(example = "production")
    private String function;

    @ApiModelProperty(example = "2023-10-13T14:30:45+01:00")
    private String functionValidFrom;
    @ApiModelProperty(example = "urn:uuid:0fed587c-7ab4-4597-9841-1718e9693003")
    private String catenaXSiteId;
}
