package org.eclipse.tractusx.traceability.cron.domain;

import lombok.Builder;
import lombok.Data;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;

@Data
@Builder
public class Config {
    private TriggerConfiguration triggerConfiguration;
    private OrderConfiguration orderConfiguration;
}
