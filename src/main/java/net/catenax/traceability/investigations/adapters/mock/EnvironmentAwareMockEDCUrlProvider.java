package net.catenax.traceability.investigations.adapters.mock;

import net.catenax.traceability.investigations.domain.ports.EDCUrlProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static net.catenax.traceability.common.config.ApplicationProfiles.NOT_TESTS;

@Component
@Profile(NOT_TESTS)
public class EnvironmentAwareMockEDCUrlProvider implements EDCUrlProvider {

	private static final Logger logger = LoggerFactory.getLogger(EnvironmentAwareMockEDCUrlProvider.class);

	private static final String ENVIRONMENT_PLACEHOLDER = "{environment}";

	private static final String DEFAULT_EDC_URL = "https://trace-x-test-edc.%s.demo.catena-x.net".formatted(ENVIRONMENT_PLACEHOLDER);

	private final String senderBpn;

	private final String applicationEnvironment;

	private final BpnToEDCProviderMappings bpnToEDCProviderMappings;

	public EnvironmentAwareMockEDCUrlProvider(@Value("${traceability.bpn}") String senderBpn, @Autowired BpnToEDCProviderMappings bpnToEDCProviderMappings, @Autowired Environment applicationEnvironment) {
		this.senderBpn = senderBpn;
		this.bpnToEDCProviderMappings = bpnToEDCProviderMappings;
		this.applicationEnvironment = Arrays.stream(applicationEnvironment.getActiveProfiles())
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("No environment found"));
	}

	public String getEdcUrl(String bpn) {
		String edcUrl = bpnToEDCProviderMappings.getBpnProviderUrlMappings().getOrDefault(bpn, defaultProviderUrl());

		logger.info("Resolved {} url for {} bpn", edcUrl, bpn);

		return edcUrl;
	}

	private String defaultProviderUrl() {
		return DEFAULT_EDC_URL.replace(ENVIRONMENT_PLACEHOLDER, applicationEnvironment);
	}

	@Override
	public String getSenderUrl() {
		return getEdcUrl(senderBpn);
	}

	public String getSenderBpn() {
		return senderBpn;
	}
}
