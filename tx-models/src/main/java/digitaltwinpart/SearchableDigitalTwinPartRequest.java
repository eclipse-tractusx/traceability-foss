package digitaltwinpart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SearchableDigitalTwinPartRequest {

    @NotBlank
    private String fieldName;

    private String startWith;

    @NotNull
    private Integer size;

}
