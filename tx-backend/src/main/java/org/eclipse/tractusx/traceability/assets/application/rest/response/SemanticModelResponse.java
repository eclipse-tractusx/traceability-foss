package org.eclipse.tractusx.traceability.assets.application.rest.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.time.Instant;

public class SemanticModelResponse {
    @ApiModelProperty(example = "2022-02-04T13:48:54Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant manufacturingDate;
    @ApiModelProperty(example = "DEU")
    private String manufacturingCountry;
    @ApiModelProperty(example = "33740332-54")
    private String manufacturerPartId;
    @ApiModelProperty(example = "33740332-54")
    private String customerPartId;
    @ApiModelProperty(example = "Door f-r")
    private String nameAtManufacturer;
    @ApiModelProperty(example = "Door front-right")
    private String nameAtCustomer;
}
