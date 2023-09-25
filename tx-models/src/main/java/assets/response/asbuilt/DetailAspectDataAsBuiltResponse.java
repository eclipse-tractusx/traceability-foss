package assets.response.asbuilt;

import assets.response.base.DetailAspectDataResponse;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DetailAspectDataAsBuiltResponse implements DetailAspectDataResponse {
    @ApiModelProperty(example = "95657762-59")
    @Size(max = 255)
    private String partId;
    @ApiModelProperty(example = "01697F7-65")
    @Size(max = 255)
    private String customerPartId;
    @ApiModelProperty(example = "Door front-left")
    @Size(max = 255)
    private String nameAtCustomer;
    @ApiModelProperty(example = "DEU")
    @Size(max = 255)
    private String manufacturingCountry;
    @ApiModelProperty(example = "2022-02-04T13:48:54Z")
    @Size(max = 255)
    private String manufacturingDate;
}
