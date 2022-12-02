package net.catenax.traceability.assets.infrastructure.rest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import net.catenax.traceability.infrastructure.edc.blackbox.Constants;
import net.catenax.traceability.infrastructure.edc.blackbox.asset.Asset;
import net.catenax.traceability.infrastructure.edc.blackbox.cache.EndpointDataReference;
import net.catenax.traceability.infrastructure.edc.blackbox.catalog.Catalog;
import net.catenax.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import net.catenax.traceability.infrastructure.edc.blackbox.notification.ContractNegotiationDto;
import net.catenax.traceability.infrastructure.edc.blackbox.notification.NegotiationInitiateRequestDto;
import net.catenax.traceability.infrastructure.edc.blackbox.notification.TransferId;
import net.catenax.traceability.infrastructure.edc.blackbox.offer.ContractOffer;
import net.catenax.traceability.infrastructure.edc.blackbox.policy.Policy;
import net.catenax.traceability.infrastructure.edc.blackbox.transfer.TransferRequestDto;
import org.apache.groovy.util.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@ApiIgnore
@RequestMapping("/edc/")
public class EDCReceiverController {

	private static final Logger logger = LoggerFactory.getLogger(EDCReceiverController.class);

	private final TestRestTemplate restTemplate;

	@Value("${server.port}")
	private int port;

	public EDCReceiverController(TestRestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@GetMapping("/data/catalog")
	public Catalog getDataCatalog(@RequestParam String providerUrl) {
		logger.info("Returning data catalog for provider {}", providerUrl);
		return Catalog.Builder.newInstance()
			.id("contract-id")
			.contractOffers(List.of(ContractOffer.Builder.newInstance()
					.id("contract-id")
					.asset(Asset.Builder.newInstance()
						.id("asset-id")
						.property(Constants.ASSET_TYPE_PROPERTY_NAME, Constants.ASSET_TYPE_NOTIFICATION)
						.build()
					).policy(Policy.Builder.newInstance()
						.build()
					).build())
			).build();
	}

	@PostMapping("/data/contractnegotiations")
	public TransferId contractNegotiations(@RequestBody NegotiationInitiateRequestDto request) {
		logger.info("Initializing contract negotiations");
		return TransferId.Builder.newInstance()
			.id("transfer-id-1")
			.build();
	}

	@PostMapping("/data/transferprocess")
	public TransferId transferProcess(@RequestBody TransferRequestDto transferRequest) {
		logger.info("Processing transfer");
		return TransferId.Builder.newInstance()
			.id("transfer-id-2")
			.build();
	}

	@PostMapping("/receive-notification")
	public void receiveNotification(@RequestBody EDCNotification notification) {
		logger.info("Notification received");
	}

	@GetMapping("/data/contractnegotiations/{transferId}")
	public ContractNegotiationDto getContractNegotiations(@PathVariable String transferId) {
		logger.info("Returning contract negotiations");
		String contractAgreementId = "contract-agreement-id";

		restTemplate.postForEntity("/callback/endpoint-data-reference",
			EndpointDataReference.Builder.newInstance()
			.authCode(JWT.create()
				.withExpiresAt(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
				.sign(Algorithm.HMAC256("test-token")))
			.authKey("auth-key")
			.endpoint("http://localhost:" + port + "/api/edc/receive-notification")
			.properties(Maps.of("cid", contractAgreementId))
			.build(),
			Void.class
		);

		return ContractNegotiationDto.Builder.newInstance()
			.contractAgreementId(contractAgreementId)
			.state("CONFIRMED")
			.build();
	}

}
