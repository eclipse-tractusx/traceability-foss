package net.catenax.traceability.infrastructure.edc.blackbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.catenax.traceability.infrastructure.edc.blackbox.cache.EndpointDataReference;
import net.catenax.traceability.infrastructure.edc.blackbox.cache.InMemoryEndpointDataReferenceCache;
import net.catenax.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import net.catenax.traceability.infrastructure.edc.blackbox.offer.ContractOffer;
import net.catenax.traceability.investigations.domain.model.Notification;
import net.catenax.traceability.investigations.domain.ports.EDCUrlProvider;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class InvestigationsEDCFacade {

	private static final Logger logger = LoggerFactory.getLogger(InvestigationsEDCFacade.class);

	private final EdcService edcService;

	private final HttpCallService httpCallService;

	private final ObjectMapper objectMapper;

	private final InMemoryEndpointDataReferenceCache endpointDataReferenceCache;

	private final EDCUrlProvider edcUrlProvider;

	@Value("${edc.api.auth.key}")
	private String apiAuthKey;

	@Value("${edc.ids}")
	private String idsPath;

	public InvestigationsEDCFacade(EdcService edcService, HttpCallService httpCallService,
								   ObjectMapper objectMapper, InMemoryEndpointDataReferenceCache endpointDataReferenceCache, EDCUrlProvider edcUrlProvider) {
		this.edcService = edcService;
		this.httpCallService = httpCallService;
		this.objectMapper = objectMapper;
		this.endpointDataReferenceCache = endpointDataReferenceCache;
		this.edcUrlProvider = edcUrlProvider;
	}

	public void startEDCTransfer(Notification notification) {
		Map<String, String> header = new HashMap<>();
		header.put("x-api-key", apiAuthKey);
		try {
			String receiverEdcUrl = edcUrlProvider.getEdcUrl(notification.getReceiverBpnNumber());
			String senderEdcUrl = edcUrlProvider.getSenderUrl();

			notification.setEdcUrl(receiverEdcUrl);

			logger.info(":::: Find Notification contract method[startEDCTransfer] senderEdcUrl :{}, receiverEdcUrl:{}", senderEdcUrl, receiverEdcUrl);
			Optional<ContractOffer> contractOffer = edcService.findNotificationContractOffer(
				senderEdcUrl,
				receiverEdcUrl + idsPath,
				header
			);

			if (contractOffer.isEmpty()) {
				logger.info("No Notification contractOffer found");
				throw new BadRequestException("No notification contract offer found.");
			}

			logger.info(":::: Initialize Contract Negotiation method[startEDCTransfer] senderEdcUrl :{}, receiverEdcUrl:{}",senderEdcUrl,receiverEdcUrl);
			String agreementId = edcService.initializeContractNegotiation(
				receiverEdcUrl,
				contractOffer.get().getAsset().getId(),
				contractOffer.get().getId(),
				contractOffer.get().getPolicy(),
				senderEdcUrl,
				header
			);
			logger.info(":::: Contract Agreed method[startEDCTransfer] agreementId :{}", agreementId);

			if (StringUtils.hasLength(agreementId)) {
				notification.setContractAgreementId(agreementId);
			}

			EndpointDataReference dataReference = endpointDataReferenceCache.get(agreementId);
			boolean validDataReference = dataReference != null && InMemoryEndpointDataReferenceCache.endpointDataRefTokenExpired(dataReference);
			if (!validDataReference) {
				logger.info(":::: Invalid Data Reference :::::" );
				if (dataReference != null) {
					endpointDataReferenceCache.remove(agreementId);
				}

				logger.info(":::: initialize Transfer process with http Proxy :::::" );
				// Initiate transfer process
				edcService.initiateHttpProxyTransferProcess(agreementId, contractOffer.get().getAsset().getId(),
					senderEdcUrl,
					receiverEdcUrl + idsPath,
					header
				);
				dataReference = getDataReference(agreementId);
			}

			EDCNotification edcNotification = new EDCNotification(senderEdcUrl, notification);
			String body = objectMapper.writeValueAsString(edcNotification);
			HttpUrl url = httpCallService.getUrl(dataReference.getEndpoint(), null, null);
			Request request = new Request.Builder()
				.url(url)
				.addHeader(dataReference.getAuthKey(), dataReference.getAuthCode())
				.addHeader("Content-Type", Constants.JSON.type())
				.post(RequestBody.create(body, Constants.JSON))
				.build();

			logger.info(":::: Send notification Data  body :{}, dataReferenceEndpoint :{}",body,dataReference.getEndpoint());
			httpCallService.sendRequest(request);

			logger.info(":::: EDC Data Transfer Completed :::::");
		} catch (IOException e) {
			logger.error("EDC Data Transfer fail", e);

			throw new BadRequestException("EDC Data Transfer fail");
		} catch (InterruptedException e) {
			logger.error("Exception", e);
			Thread.currentThread().interrupt();
		}
	}

	private EndpointDataReference getDataReference(String agreementId) throws InterruptedException {
		EndpointDataReference dataReference = null;
		var waitTimeout = 20;
		while (dataReference == null && waitTimeout > 0) {
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			ScheduledFuture<EndpointDataReference> scheduledFuture =
				scheduler.schedule(() -> endpointDataReferenceCache.get(agreementId),30, TimeUnit.SECONDS);
			try {
				dataReference = scheduledFuture.get();
				waitTimeout--;
				scheduler.shutdown();
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}finally {
				if(!scheduler.isShutdown()){
					scheduler.shutdown();
				}
			}
		}
		if (dataReference == null) {
			throw new BadRequestException("Did not receive callback within 30 seconds from consumer edc.");
		}
		return dataReference;
	}


}
