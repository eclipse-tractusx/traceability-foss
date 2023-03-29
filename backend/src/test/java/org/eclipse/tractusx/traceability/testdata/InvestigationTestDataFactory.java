package org.eclipse.tractusx.traceability.testdata;

import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.investigations.domain.model.*;

import java.time.Instant;
import java.util.List;

public class InvestigationTestDataFactory {
	public static Investigation createInvestigationTestData(InvestigationStatus investigationStatus, InvestigationStatus notificationInvestigationStatus, final String bpnString) {
		InvestigationId investigationId = new InvestigationId(1L);
		BPN bpn = new BPN(bpnString);
		InvestigationSide investigationSide = InvestigationSide.SENDER;
		String closeReason = null;
		String acceptReason = null;
		String declineReason = null;
		String description = "Test Investigation";
		Instant createdAt = Instant.now();
		List<String> assetIds = List.of("asset123", "asset456");
		List<Notification> notifications = List.of(new Notification(
                "1",
                "notificationId",
                "senderBPN",
                "senderManufacturerName",
                "recipientBPN",
                "receiverManufacturerName",
                "senderAddress",
                "agreement",
                "Test Notification",
                notificationInvestigationStatus,
                List.of(new AffectedPart("part123")),
                Instant.now(),
                Severity.MINOR
		));

		return new Investigation(
			investigationId,
			bpn,
			investigationStatus,
			investigationSide,
			closeReason,
			acceptReason,
			declineReason,
			description,
			createdAt,
			assetIds,
			notifications
		);
	}


	public static Investigation createInvestigationTestDataWithNotificationList(InvestigationStatus investigationStatus, String bpnString, List<Notification> notifications) {
		InvestigationId investigationId = new InvestigationId(1L);
		BPN bpn = new BPN(bpnString);
		InvestigationSide investigationSide = InvestigationSide.SENDER;
		String closeReason = null;
		String acceptReason = null;
		String declineReason = null;
		String description = "Test Investigation";
		Instant createdAt = Instant.now();
		List<String> assetIds = List.of("asset123", "asset456");

		return new Investigation(
			investigationId,
			bpn,
			investigationStatus,
			investigationSide,
			closeReason,
			acceptReason,
			declineReason,
			description,
			createdAt,
			assetIds,
			notifications
		);
	}

	public static Investigation createInvestigationTestData(InvestigationStatus investigationStatus, InvestigationStatus notificationInvestigationStatus) {
		InvestigationId investigationId = new InvestigationId(1L);
		BPN bpn = new BPN("bpn123");
		InvestigationSide investigationSide = InvestigationSide.SENDER;
		String closeReason = null;
		String acceptReason = null;
		String declineReason = null;
		String description = "Test Investigation";
		Instant createdAt = Instant.now();
		List<String> assetIds = List.of("asset123", "asset456");
		List<Notification> notifications = List.of(new Notification(
                "1",
                "notificationId",
                "senderBPN",
                "senderManufacturerName",
                "recipientBPN",
                "receiverManufacturerName",
                "senderAddress",
                "agreement",
                "Test Notification",
                notificationInvestigationStatus,
                List.of(new AffectedPart("part123")),
                Instant.now(),
                Severity.MINOR
		));

		return new Investigation(
			investigationId,
			bpn,
			investigationStatus,
			investigationSide,
			closeReason,
			acceptReason,
			declineReason,
			description,
			createdAt,
			assetIds,
			notifications
		);
	}
}
