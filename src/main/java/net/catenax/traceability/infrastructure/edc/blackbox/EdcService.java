package net.catenax.traceability.infrastructure.edc.blackbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.catenax.traceability.infrastructure.edc.blackbox.catalog.Catalog;
import net.catenax.traceability.infrastructure.edc.blackbox.notification.ContractNegotiationDto;
import net.catenax.traceability.infrastructure.edc.blackbox.notification.ContractOfferDescription;
import net.catenax.traceability.infrastructure.edc.blackbox.notification.NegotiationInitiateRequestDto;
import net.catenax.traceability.infrastructure.edc.blackbox.notification.TransferId;
import net.catenax.traceability.infrastructure.edc.blackbox.offer.ContractOffer;
import net.catenax.traceability.infrastructure.edc.blackbox.policy.Policy;
import net.catenax.traceability.infrastructure.edc.blackbox.transfer.DataAddress;
import net.catenax.traceability.infrastructure.edc.blackbox.transfer.TransferRequestDto;
import net.catenax.traceability.infrastructure.edc.blackbox.transfer.TransferType;
import net.catenax.traceability.investigations.domain.model.InvestigationStatus;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class EdcService {

	private static final Logger logger = LoggerFactory.getLogger(EdcService.class);
	private final HttpCallService httpCallService;
	private final ObjectMapper objectMapper;

	@Value("${edc.negotiation}")
	String negotiationPath;

	@Value("${edc.transfer}")
	String transferPath;

	@Value("${edc.ids}")
	String idsPath;


	public EdcService(HttpCallService httpCallService,
					  ObjectMapper objectMapper) {
		this.httpCallService = httpCallService;
		this.objectMapper = objectMapper;
	}


	/**
	 * Rest call to get all contract offer and filter notification type contract
	 */
	public Optional<ContractOffer> findNotificationContractOffer(
		String consumerEdcDataManagementUrl,
		String providerConnectorControlPlaneIDSUrl,
		Map<String, String> header
	) throws IOException {
		Catalog catalog = httpCallService.getCatalogFromProvider(consumerEdcDataManagementUrl, providerConnectorControlPlaneIDSUrl, header);
		if (catalog.getContractOffers().isEmpty()) {
			logger.error("No contract found");
			throw new BadRequestException("Provider has no contract offers for us. Catalog is empty.");
		}
		logger.info(":::: Find Notification contract method[findNotificationContractOffer] total catalog ::{}",catalog.getContractOffers().size());
		return catalog.getContractOffers().stream()
			.filter(it -> it.getAsset().getProperty(Constants.ASSET_TYPE_PROPERTY_NAME) != null && it.getAsset().getProperty(Constants.ASSET_TYPE_PROPERTY_NAME).equals(Constants.ASSET_TYPE_NOTIFICATION))
			.findFirst();
	}

	/**
	 * Prepare for contract negotiation. it will wait for while till API return agreementId
	 */
	public String initializeContractNegotiation(String providerConnectorUrl, String assetId, String offerId, Policy policy, String consumerEdcUrl,
												Map<String, String> header) throws InterruptedException, IOException {
		// Initiate negotiation
		ContractOfferDescription contractOfferDescription = new ContractOfferDescription(offerId, assetId, null, policy);
		NegotiationInitiateRequestDto contractNegotiationRequest = NegotiationInitiateRequestDto.Builder.newInstance()
			.offerId(contractOfferDescription).connectorId("provider").connectorAddress(providerConnectorUrl + idsPath)
			.protocol("ids-multipart").build();

		logger.info(":::: Start Contract Negotiation method[initializeContractNegotiation] offerId :{}, assetId:{}",offerId,assetId);

		String negotiationId = initiateNegotiation(contractNegotiationRequest, consumerEdcUrl, header);
		ContractNegotiationDto negotiation = null;

		// Check negotiation state
		while (negotiation == null || !negotiation.getState().equals("CONFIRMED")) {

			logger.info(":::: waiting for contract to get confirmed");
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			ScheduledFuture<ContractNegotiationDto> scheduledFuture =
				scheduler.schedule(() -> {
					var url = consumerEdcUrl + negotiationPath + "/" + negotiationId;
					var request = new Request.Builder().url(url);
					header.forEach(request::addHeader);

					logger.info(":::: Start call for contract agreement method [initializeContractNegotiation] URL :{}",url);

					return  (ContractNegotiationDto) httpCallService.sendRequest(request.build(), ContractNegotiationDto.class);
				},1000,TimeUnit.MILLISECONDS);
			try {
				negotiation = scheduledFuture.get();
				scheduler.shutdown();
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}finally {
				if(!scheduler.isShutdown()){
					scheduler.shutdown();
				}
			}
		}
		return negotiation.getContractAgreementId();
	}


	/**
	 * Rest call for Contract negotiation and return agreementId.
	 */
	private String initiateNegotiation(NegotiationInitiateRequestDto contractOfferRequest,String consumerEdcDataManagementUrl,
									   Map<String, String> headers) throws IOException {
		var url = consumerEdcDataManagementUrl + negotiationPath;
		var requestBody = RequestBody.create(objectMapper.writeValueAsString(contractOfferRequest), Constants.JSON);
		var request = new Request.Builder().url(url).post(requestBody);

		headers.forEach(request::addHeader);
		request.build();
		TransferId negotiationId = (TransferId) httpCallService.sendRequest(request.build(), TransferId.class);
		logger.info(":::: Method [initiateNegotiation] Negotiation Id :{}",negotiationId.getId());
		return negotiationId.getId();
	}


	/**
	 * Rest call for Transfer Data with HttpProxy
	 */
	public TransferId initiateHttpProxyTransferProcess(String agreementId, String assetId, String consumerEdcDataManagementUrl, String providerConnectorControlPlaneIDSUrl, Map<String, String> headers) throws IOException {
		var url = consumerEdcDataManagementUrl + transferPath;

		DataAddress dataDestination = DataAddress.Builder.newInstance().type("HttpProxy").build();
		TransferType transferType = TransferType.Builder.transferType().contentType("application/octet-stream").isFinite(true).build();

		TransferRequestDto transferRequest = TransferRequestDto.Builder.newInstance()
			.assetId(assetId).contractId(agreementId).connectorId("provider").connectorAddress(providerConnectorControlPlaneIDSUrl)
			.protocol("ids-multipart").dataDestination(dataDestination).managedResources(false).transferType(transferType)
			.build();

		var requestBody = RequestBody.create(objectMapper.writeValueAsString(transferRequest), Constants.JSON);
		var request = new Request.Builder().url(url).post(requestBody);

		headers.forEach(request::addHeader);
		logger.info(":::: call Transfer process with http Proxy method[initiateHttpProxyTransferProcess] agreementId:{} ,assetId :{},consumerEdcDataManagementUrl :{}, providerConnectorControlPlaneIDSUrl:{}",agreementId,assetId,consumerEdcDataManagementUrl,providerConnectorControlPlaneIDSUrl );
		return (TransferId) httpCallService.sendRequest(request.build(), TransferId.class);
	}
}
