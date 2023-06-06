package org.eclipse.tractusx.traceability.assets.application.rest.response;

import org.eclipse.tractusx.traceability.assets.domain.model.SemanticDataModel;

public enum SemanticDataModelResponse {
    BATCH, SERIAL_PART_TYPIZATION;

    public static SemanticDataModelResponse from(final SemanticDataModel semanticDataModel) {
        return SemanticDataModelResponse.valueOf(semanticDataModel.name());
    }
}
