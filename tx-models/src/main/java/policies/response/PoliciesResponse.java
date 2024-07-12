package policies.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;


import java.time.OffsetDateTime;

@Builder
public record PoliciesResponse(@JsonFormat(shape = JsonFormat.Shape.STRING) OffsetDateTime validUntil, PayloadResponse payload
) {
}
