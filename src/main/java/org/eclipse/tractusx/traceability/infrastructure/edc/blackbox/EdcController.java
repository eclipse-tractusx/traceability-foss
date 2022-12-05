package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox;

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import org.eclipse.tractusx.traceability.investigations.domain.service.InvestigationsReceiverService;
import org.eclipse.tractusx.traceability.common.config.FeatureFlags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Profile(FeatureFlags.NOTIFICATIONS_ENABLED_PROFILES)
@ApiIgnore
@RestController
public class EdcController {
	private static final Logger logger = LoggerFactory.getLogger(EdcController.class);

	private final InvestigationsReceiverService investigationsReceiverService;

	public EdcController(InvestigationsReceiverService investigationsReceiverService) {
		this.investigationsReceiverService = investigationsReceiverService;
	}

	/**
	 * Receiver API call for EDC Transfer
	 */
	@PostMapping("/qualitynotifications/receive")
	public void qualityNotificationReceive(@RequestBody EDCNotification edcNotification) {
		logger.info("EdcController [qualityNotificationReceive] notificationId:{}", edcNotification);

		investigationsReceiverService.handle(edcNotification);
	}
}

