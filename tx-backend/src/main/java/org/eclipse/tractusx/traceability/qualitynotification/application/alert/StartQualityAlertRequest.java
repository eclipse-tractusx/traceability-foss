package org.eclipse.tractusx.traceability.qualitynotification.application.alert;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.tractusx.traceability.qualitynotification.application.request.QualityNotificationSeverityRequest;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartQualityAlertRequest {
    @Size(min = 1, max = 100, message = "Specify at least 1 and at most 100 partIds")
    @ApiModelProperty(example = "[\"urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978\"]")
    private List<String> partIds;
    @Size(min = 15, max = 1000, message = "Description should have at least 15 characters and at most 1000 characters")
    @ApiModelProperty(example = "The description")
    private String description;
    @ApiModelProperty(example = "2099-03-11T22:44:06.333826952Z")
    @Future(message = "Specify at least the current day or a date in future")
    private Instant targetDate;
    @NotNull
    @ApiModelProperty(example = "MINOR")
    private QualityNotificationSeverityRequest severity;
    @NotNull
    @ApiModelProperty(example = "BPN00001123123AS")
    private String bpn;
}
