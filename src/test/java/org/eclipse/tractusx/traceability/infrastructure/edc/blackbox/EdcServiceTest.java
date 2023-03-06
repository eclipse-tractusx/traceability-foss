package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox;

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.asset.Asset;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.catalog.Catalog;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.offer.ContractOffer;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy.Policy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.asset.Asset.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EdcServiceTest {

	@Mock
	private HttpCallService httpCallService;

	@InjectMocks
	private EdcService edcService;

	private static final String CONSUMER_EDC_DATA_MANAGEMENT_URL = "http://consumer-edc-data-management.com";
	private static final String PROVIDER_CONNECTOR_CONTROL_PLANE_IDS_URL = "http://provider-connector-control-plane-ids.com";

	@Test
	void testFindNotificationContractOffer() throws IOException {
		// given
		Map<String, Object> properties = new HashMap<>();
		properties.put(PROPERTY_ID, 123);
		properties.put(PROPERTY_NAME, "My Asset");
		properties.put(PROPERTY_DESCRIPTION, "This is a description of my asset.");
		properties.put(PROPERTY_VERSION, 1.0);
		properties.put(PROPERTY_CONTENT_TYPE, "image/jpeg");
		properties.put(PROPERTY_NOTIFICATION_TYPE, "qualityinvestigation");
		properties.put(PROPERTY_NOTIFICATION_METHOD, "update");

		Policy policy = Policy.Builder.newInstance().build();
		Asset asset = Builder.newInstance().properties(properties).build();
		ContractOffer expectedContractOffer = ContractOffer.Builder.newInstance().id("123").policy(policy).asset(asset).build();
		Catalog catalog = Catalog.Builder.newInstance().id("234").contractOffers(List.of(expectedContractOffer)).build();


		Map<String, String> header = new HashMap<>();
		when(httpCallService.getCatalogFromProvider(CONSUMER_EDC_DATA_MANAGEMENT_URL, PROVIDER_CONNECTOR_CONTROL_PLANE_IDS_URL, header)).thenReturn(catalog);

		// when
		Optional<ContractOffer> contractOfferResult = edcService.findNotificationContractOffer(CONSUMER_EDC_DATA_MANAGEMENT_URL, PROVIDER_CONNECTOR_CONTROL_PLANE_IDS_URL, header);

		// then
		assertTrue(contractOfferResult.isPresent());
		assertEquals(expectedContractOffer, contractOfferResult.get());
	}

	@Test
	void testFindNotificationContractOfferWrongNotificationType() throws IOException {
		// given
		Map<String, Object> properties = new HashMap<>();
		properties.put(PROPERTY_ID, 123);
		properties.put(PROPERTY_NAME, "My Asset");
		properties.put(PROPERTY_DESCRIPTION, "This is a description of my asset.");
		properties.put(PROPERTY_VERSION, 1.0);
		properties.put(PROPERTY_CONTENT_TYPE, "image/jpeg");
		properties.put(PROPERTY_NOTIFICATION_TYPE, "qualityinvestigation");
		properties.put(PROPERTY_NOTIFICATION_METHOD, "receive");

		Policy policy = Policy.Builder.newInstance().build();
		Asset asset = Builder.newInstance().properties(properties).build();
		ContractOffer expectedContractOffer = ContractOffer.Builder.newInstance().id("123").policy(policy).asset(asset).build();
		Catalog catalog = Catalog.Builder.newInstance().id("234").contractOffers(List.of(expectedContractOffer)).build();


		Map<String, String> header = new HashMap<>();
		when(httpCallService.getCatalogFromProvider(CONSUMER_EDC_DATA_MANAGEMENT_URL, PROVIDER_CONNECTOR_CONTROL_PLANE_IDS_URL, header)).thenReturn(catalog);

		// when
		Optional<ContractOffer> contractOfferResult = edcService.findNotificationContractOffer(CONSUMER_EDC_DATA_MANAGEMENT_URL, PROVIDER_CONNECTOR_CONTROL_PLANE_IDS_URL, header);

		// then
		assertTrue(contractOfferResult.isEmpty());
	}

}
