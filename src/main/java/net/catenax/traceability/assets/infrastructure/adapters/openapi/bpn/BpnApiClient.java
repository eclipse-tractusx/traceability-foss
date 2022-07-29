package net.catenax.traceability.assets.infrastructure.adapters.openapi.bpn;

import net.catenax.traceability.assets.infrastructure.config.openapi.bpn.BpnApiConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
	name = "bpnApi",
	url = "${feign.bpnApi.url}",
	configuration = {BpnApiConfig.class}
)
public interface BpnApiClient extends BusinessPartnerControllerApi {
}
