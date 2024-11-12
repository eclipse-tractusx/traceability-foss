package org.eclipse.tractusx.traceability.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties("provisioning.registry")
public class RegistryProperties {
    private String urlWithPathInternal;
    private String urlWithPathExternal;
    private String shellDescriptorUrl;
    private String allowedBpns;
    private String clientId;
    private String clientSecret;
    private String tokenUrl;
    private String oauthProviderRegistrationId;
    private String edcAssetId;
}
