package policies.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.eclipse.tractusx.irs.edc.client.policy.OperatorType;

import java.util.Arrays;
@Builder
@Schema
public record OperatorResponse(
        @JsonProperty("@id")
        @Schema(
                implementation = OperatorType.class,
                example = "odrl:eq"
        )
       OperatorTypeResponse operatorType
) {
    public static OperatorType toDomain(OperatorTypeResponse operatorTypeResponse) {
        if (operatorTypeResponse == null) {
            return null;
        }

        return Arrays.stream(OperatorType.values())
                .filter(type -> type.getCode().equals(operatorTypeResponse.code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown code: " + operatorTypeResponse.code));
    }
}
