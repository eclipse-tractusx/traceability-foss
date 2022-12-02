package net.catenax.traceability.investigations.adapters.mock;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

import java.util.Map;

import static net.catenax.traceability.common.config.ApplicationProfiles.NOT_TESTS;

@Profile(NOT_TESTS)
@ConfigurationProperties(prefix = "edc")
public class BpnToEDCProviderMappings {

	private Map<String, String> bpnProviderUrlMappings;

	public Map<String, String> getBpnProviderUrlMappings() {
		return bpnProviderUrlMappings;
	}

	public void setBpnProviderUrlMappings(Map<String, String> bpnProviderUrlMappings) {
		this.bpnProviderUrlMappings = bpnProviderUrlMappings;
	}
}
