package org.eclipse.tractusx.traceability.investigations.domain.service


import org.eclipse.tractusx.traceability.common.support.AssetsSupport
import org.eclipse.tractusx.traceability.common.support.BpnSupport
import org.eclipse.tractusx.traceability.common.support.InvestigationsSupport
import org.eclipse.tractusx.traceability.common.support.IrsApiSupport
import org.eclipse.tractusx.traceability.common.support.NotificationsSupport
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification
import org.eclipse.tractusx.traceability.investigations.domain.model.AffectedPart
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus
import org.eclipse.tractusx.traceability.investigations.domain.model.Notification
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
