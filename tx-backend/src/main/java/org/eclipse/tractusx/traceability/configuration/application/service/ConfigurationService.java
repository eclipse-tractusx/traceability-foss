package org.eclipse.tractusx.traceability.configuration.application.service;

import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import configuration.request.OrderConfigurationRequest;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;
import configuration.request.TriggerConfigurationRequest;

public interface ConfigurationService {

    void persistOrderConfiguration(OrderConfigurationRequest request);

    OrderConfiguration getLatestOrderConfiguration(Long orderId);

    void persistTriggerConfiguration(TriggerConfigurationRequest request);

    TriggerConfiguration getLatestTriggerConfiguration();
}
