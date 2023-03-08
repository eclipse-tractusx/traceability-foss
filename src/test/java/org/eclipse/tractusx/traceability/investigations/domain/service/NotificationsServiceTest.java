package org.eclipse.tractusx.traceability.investigations.domain.service;

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.InvestigationsEDCFacade;
import org.eclipse.tractusx.traceability.investigations.domain.model.Notification;
import org.eclipse.tractusx.traceability.investigations.domain.ports.EDCUrlProvider;
import org.eclipse.tractusx.traceability.investigations.domain.ports.InvestigationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationsServiceTest {

	@InjectMocks
	private NotificationsService notificationsService;

	@Mock
	private InvestigationsEDCFacade edcFacade;

	@Mock
	private InvestigationsRepository repository;

	@Mock
	private EDCUrlProvider edcUrlProvider;

	@Test
	void testNotificationsServiceUpdateAsync() {
		// given
		String bpn = "BPN1234";
		String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
		String edcSenderUrl = "https://not-real-edc-sender-url.com";

		// and
		when(edcUrlProvider.getEdcUrls(bpn)).thenReturn(List.of(edcReceiverUrl));
		when(edcUrlProvider.getSenderUrl()).thenReturn(edcSenderUrl);

		// and
		Notification notification = new Notification(
			null,
			null,
			null,
			bpn,
			null,
			null,
			null,
			null,
			null
		);

		// when
		notificationsService.updateAsync(notification);

		// then
		verify(edcFacade).startEDCTransfer(any(Notification.class), eq(edcReceiverUrl), eq(edcSenderUrl));
		verify(repository).update(any(Notification.class));
	}
}
