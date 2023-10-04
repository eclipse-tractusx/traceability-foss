package bpn.request;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BpnMappingRequest(
        @NotNull(message = "BPN must be present")
        @NotEmpty(message = "BPN must be present")
        @ApiModelProperty(example = "BPNL00000003CSGV")
        @Size(max = 255)
        String bpn,

        @NotNull(message = "A valid URL must be present")
        @NotEmpty(message = "A valid URL must be present")
        @Size(max = 255)
        String url
) {
}
