package net.catenax.traceability.assets.infrastructure.adapters.openapi.bpn;

import net.catenax.traceability.assets.infrastructure.config.openapi.CatenaApiConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
	name = "bpnApi",
	url = "${feign.bpnApi.url}",
	configuration = {CatenaApiConfig.class}
)
public interface BpnApiClient extends BusinessPartnerControllerApi {
}
