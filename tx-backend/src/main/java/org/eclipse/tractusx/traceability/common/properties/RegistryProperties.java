package org.eclipse.tractusx.traceability.common.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Configuration
@Data
@ConfigurationProperties("provisioning.registry")
public class RegistryProperties {
    private String urlWithPathInternal;
    private String urlWithPathExternal;
    private String shellDescriptorUrl;
    private String allowedBpns;
}
