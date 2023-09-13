package assets.response.asplanned;

import assets.response.base.DetailAspectDataResponse;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DetailAspectDataAsPlannedResponse implements DetailAspectDataResponse {

    @ApiModelProperty(example = "2022-09-26T12:43:51.079Z")
    @Size(max = 255)
    private String validityPeriodFrom;

    @ApiModelProperty(example = "20232-07-13T12:00:00.000Z")
    @Size(max = 255)
    private String validityPeriodTo;
}
