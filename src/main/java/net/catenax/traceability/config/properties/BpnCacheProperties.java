package net.catenax.traceability.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;

@ConstructorBinding
@ConfigurationProperties("cache.bpn")
public class BpnCacheProperties extends CacheProperties {

	public BpnCacheProperties(String name, int maximumSize, Duration expireAfterWrite) {
		super(name, maximumSize, expireAfterWrite);
	}
}
