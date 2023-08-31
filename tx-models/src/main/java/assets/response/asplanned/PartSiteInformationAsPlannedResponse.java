package assets.response.asplanned;

import assets.response.base.DetailAspectDataResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class PartSiteInformationAsPlannedResponse implements DetailAspectDataResponse {
    @ApiModelProperty(example = "2025-02-08T04:30:48.000Z")
    private String functionValidUntil;
    @ApiModelProperty(example = "production")
    private String function;
    @ApiModelProperty(example = "2025-02-08T04:30:48.000Z")
    private String functionValidFrom;
    @ApiModelProperty(example = "urn:uuid:0fed587c-7ab4-4597-9841-1718e9693003")
    private String catenaXSiteId;
}
