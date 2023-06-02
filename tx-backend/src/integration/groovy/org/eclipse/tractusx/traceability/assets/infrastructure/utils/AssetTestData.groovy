package org.eclipse.tractusx.traceability.assets.infrastructure.utils

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.tractusx.traceability.assets.domain.model.Asset
import org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model.response.JobDetailResponse

class AssetTestData {

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    List<Asset> readAndConvertAssetsForTests() {
        try {
            InputStream file = AssetTestData.class.getResourceAsStream("/data/irs_assets_v2.json")
            JobDetailResponse response = mapper.readValue(file, JobDetailResponse.class)
            return response.convertAssets()
        } catch (IOException e) {
            return Collections.emptyList()
        }
    }

}
