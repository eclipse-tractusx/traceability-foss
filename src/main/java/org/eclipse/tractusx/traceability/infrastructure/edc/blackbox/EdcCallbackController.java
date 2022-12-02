package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox;

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.cache.EndpointDataReference;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.cache.InMemoryEndpointDataReferenceCache;
import org.eclipse.tractusx.traceability.common.config.FeatureFlags;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Profile(FeatureFlags.NOTIFICATIONS_ENABLED_PROFILES)
@RestController
@ApiIgnore
@RequestMapping("/callback/endpoint-data-reference")
public class EdcCallbackController {

	private final InMemoryEndpointDataReferenceCache endpointDataReferenceCache;

	public EdcCallbackController(InMemoryEndpointDataReferenceCache endpointDataReferenceCache) {
		this.endpointDataReferenceCache = endpointDataReferenceCache;
	}

	@PostMapping
	public void receiveEdcCallback(@RequestBody EndpointDataReference dataReference) {
		var contractAgreementId = dataReference.getProperties().get("cid");
		endpointDataReferenceCache.put(contractAgreementId, dataReference);
	}
}
