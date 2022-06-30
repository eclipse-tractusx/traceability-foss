package net.catenax.traceability.config.properties;

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
