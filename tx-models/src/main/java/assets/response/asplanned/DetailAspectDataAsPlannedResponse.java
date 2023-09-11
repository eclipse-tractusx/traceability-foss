package assets.response.asplanned;

import assets.response.base.DetailAspectDataResponse;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DetailAspectDataAsPlannedResponse implements DetailAspectDataResponse {
    private String validityPeriodFrom;
    private String validityPeriodTo;
}
