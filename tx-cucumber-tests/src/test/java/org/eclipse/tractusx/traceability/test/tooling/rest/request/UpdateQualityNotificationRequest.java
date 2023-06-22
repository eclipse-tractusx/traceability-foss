package org.eclipse.tractusx.traceability.test.tooling.rest.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateQualityNotificationRequest {
    private UpdateQualityNotificationStatusRequest status;
    private String reason;
}
