package net.catenax.traceability.clients.openapi.bpn;

import net.catenax.traceability.config.BpnApiConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
	name = "bpnApi",
	url = "${feign.bpnApi.url}",
	configuration = {BpnApiConfig.class}
)
public interface BpnApiClient extends BusinessPartnerControllerApi {
}
