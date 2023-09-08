package assets.response.asbuilt;

import assets.response.base.DetailAspectDataResponse;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DetailAspectDataAsBuiltResponse implements DetailAspectDataResponse {
    private String partId;
    private String customerPartId;
    private String nameAtCustomer;
    private String manufacturingCountry;
    private String manufacturingDate;
}
