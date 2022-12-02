package net.catenax.traceability.investigations.domain.service

import net.catenax.traceability.IntegrationSpecification
import net.catenax.traceability.common.support.AssetsSupport
import net.catenax.traceability.common.support.BpnSupport
import net.catenax.traceability.common.support.InvestigationsSupport
import net.catenax.traceability.common.support.IrsApiSupport
import net.catenax.traceability.common.support.NotificationsSupport
import net.catenax.traceability.infrastructure.edc.blackbox.model.EDCNotification
import net.catenax.traceability.investigations.domain.model.AffectedPart
import net.catenax.traceability.investigations.domain.model.InvestigationStatus
import net.catenax.traceability.investigations.domain.model.Notification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class InvestigationsPublisherServiceIT extends IntegrationSpecification implements IrsApiSupport, AssetsSupport, InvestigationsSupport, NotificationsSupport, BpnSupport {

	@Autowired
	InvestigationsReceiverService investigationsReceiverService

	@Transactional
	def "should receive notification"() {
		given:
			defaultAssetsStored()

		and:
			EDCNotification notification = new EDCNotification(
				"it",
				new Notification(
					"some-id",
					null,
					"bpn-a",
					"BPNL00000003AXS3",
					"edcUrl",
					null,
					"description",
					InvestigationStatus.APPROVED,
					[new AffectedPart("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb")]
				)
			)

		when:
			investigationsReceiverService.handle(notification)

		then:
			assertInvestigationsSize(1)
			assertNotificationsSize(1)
	}

}
