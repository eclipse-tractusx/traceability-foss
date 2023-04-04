package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.scheduler;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.eclipse.tractusx.traceability.assets.application.RegistryFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;

@ExtendWith(MockitoExtension.class)
class ShellDescriptorRefreshJobTest {

    @Mock
    private RegistryFacade registryFacade;

    @Test
    void refresh_shouldCallLoadShellDescriptors() {
        ShellDescriptorRefreshJob job = new ShellDescriptorRefreshJob(registryFacade);
        job.refresh();
        verify(registryFacade).loadShellDescriptors();
    }

    @Test
    void refresh_shouldBeScheduledForEveryTwoHours() throws NoSuchMethodException {
        Scheduled scheduledAnnotation = ShellDescriptorRefreshJob.class.getDeclaredMethod("refresh").getAnnotation(Scheduled.class);
        String cronExpression = scheduledAnnotation.cron();
        assertEquals("0 0 */2 * * ?", cronExpression);
    }

    @Test
    void refresh_shouldHaveSchedulerLockAnnotation() throws NoSuchMethodException {
        Scheduled scheduledAnnotation = ShellDescriptorRefreshJob.class.getDeclaredMethod("refresh").getAnnotation(Scheduled.class);
        assertNotNull(scheduledAnnotation);
        SchedulerLock schedulerLockAnnotation = ShellDescriptorRefreshJob.class.getDeclaredMethod("refresh").getAnnotation(SchedulerLock.class);
        assertNotNull(schedulerLockAnnotation);
        assertEquals("data-sync-lock", schedulerLockAnnotation.name());
        assertEquals("PT5M", schedulerLockAnnotation.lockAtLeastFor());
        assertEquals("PT15M", schedulerLockAnnotation.lockAtMostFor());
    }
}
