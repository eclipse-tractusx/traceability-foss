package org.eclipse.tractusx.traceability.investigations.adapters;

import feign.FeignException;
import feign.Request;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.eclipse.tractusx.traceability.investigations.adapters.feign.portal.ConnectorDiscoveryMappingResponse;
import org.eclipse.tractusx.traceability.investigations.adapters.feign.portal.PortalAdministrationApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EDCUrlProviderDispatcherTest {

	private EDCUrlProviderDispatcher edcUrlProviderDispatcher;

	@Mock
	private PortalAdministrationApiClient portalAdministrationApiClient;

	@Mock
	private Environment environment;

	@Mock
	private EdcProperties edcProperties;

	@BeforeEach
	void setup() {
		when(environment.getActiveProfiles()).thenReturn(new String[]{"test"});

		edcUrlProviderDispatcher = new EDCUrlProviderDispatcher(portalAdministrationApiClient, environment, edcProperties);
	}

	@Test
	void testEdcUrlProviderDispatcherGetSenderUrl() {
		// given
		String senderUrl = "https://some-edc-url.com";

		// and
		when(edcProperties.getProviderEdcUrl()).thenReturn(senderUrl);

		// when
		String result = edcUrlProviderDispatcher.getSenderUrl();

		// then
		assertThat(result).isEqualTo(senderUrl);
	}

	@Test
	void testEdcUrlProviderDispatcherGetEdcUrlsFromPortal() {
		// given
		String bpn = "BPN1234";
		String connectorEndpoint = "https://some-edc-url.com";

		// and
		when(portalAdministrationApiClient.getConnectorEndpointMappings(List.of(bpn)))
			.thenReturn(List.of(new ConnectorDiscoveryMappingResponse(bpn, List.of(connectorEndpoint))));

		// when
		List<String> edcUrls = edcUrlProviderDispatcher.getEdcUrls(bpn);

		// then
		assertThat(edcUrls).isEqualTo(List.of(connectorEndpoint));
	}

	@Test
	void testEdcUrlProviderDispatcherGetEdcUrlsFromFallbackMappingOnServiceUnavailable() {
		// given
		String bpn = "BPN1234";
		String connectorEndpoint = "https://some-edc-url.com";

		when(edcProperties.getBpnProviderUrlMappings()).thenReturn(Map.of(bpn, connectorEndpoint));

		// and
		when(portalAdministrationApiClient.getConnectorEndpointMappings(List.of(bpn)))
			.thenThrow(serviceUnavailable());

		// when
		List<String> edcUrls = edcUrlProviderDispatcher.getEdcUrls(bpn);

		// then
		assertThat(edcUrls).isEqualTo(List.of(connectorEndpoint));
	}

	@Test
	void testEdcUrlProviderDispatcherGetEdcUrlsFromFallbackMappingOnNullResponse() {
		// given
		String bpn = "BPN1234";
		String connectorEndpoint = "https://some-edc-url.com";

		when(edcProperties.getBpnProviderUrlMappings()).thenReturn(Map.of(bpn, connectorEndpoint));

		// and
		when(portalAdministrationApiClient.getConnectorEndpointMappings(List.of(bpn))).thenReturn(null);

		// when
		List<String> edcUrls = edcUrlProviderDispatcher.getEdcUrls(bpn);

		// then
		assertThat(edcUrls).isEqualTo(List.of(connectorEndpoint));
	}

	@Test
	void testEdcUrlProviderDispatcherGetEdcUrlsFromFallbackDefaultMapping() {
		// given
		String bpn = "BPN1234";

		// and
		when(portalAdministrationApiClient.getConnectorEndpointMappings(List.of(bpn)))
			.thenThrow(new RuntimeException("unit-tests"));

		// when
		List<String> edcUrls = edcUrlProviderDispatcher.getEdcUrls(bpn);

		// then
		assertThat(edcUrls).isEqualTo(List.of("https://trace-x-test-edc.test.demo.catena-x.net"));
	}

	private FeignException.ServiceUnavailable serviceUnavailable() {
		return new FeignException.ServiceUnavailable(
			"unit-tests",
			Request.create(Request.HttpMethod.POST, "", Map.of(), new byte[]{}, null, null),
			null, null);
	}
}
