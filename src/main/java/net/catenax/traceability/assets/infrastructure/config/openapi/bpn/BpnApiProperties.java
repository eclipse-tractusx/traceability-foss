package net.catenax.traceability.assets.infrastructure.config.openapi.bpn;

import net.catenax.traceability.common.properties.FeignProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties("feign.bpn-api")
public class BpnApiProperties extends FeignProperties {

	public BpnApiProperties(Long connectionTimeoutMillis,
							Long readTimeoutMillis,
							int maxIdleConnections,
							Long keepAliveDurationMinutes) {
		super(connectionTimeoutMillis, readTimeoutMillis, maxIdleConnections, keepAliveDurationMinutes);
	}
}
