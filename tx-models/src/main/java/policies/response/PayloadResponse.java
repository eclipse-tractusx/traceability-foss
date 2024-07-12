package policies.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import policies.request.Context;

public record PayloadResponse(
    @JsonProperty("@context")
    Context context,
    @JsonProperty("@id") String policyId,
    PolicyResponse policy
){}
