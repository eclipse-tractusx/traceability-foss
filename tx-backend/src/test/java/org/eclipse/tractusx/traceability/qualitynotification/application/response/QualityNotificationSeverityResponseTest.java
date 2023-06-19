package org.eclipse.tractusx.traceability.qualitynotification.application.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

class QualityNotificationSeverityResponseTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void givenSeverityResponse_whenCreateJson_thenSerializeProperly() throws JsonProcessingException {
        // given
        QualityNotificationSeverityResponse input = QualityNotificationSeverityResponse.LIFE_THREATENING;

        // when
        String result = objectMapper.writeValueAsString(input);

        // then
        assertThat(result).isEqualTo("\"LIFE-THREATENING\"");
    }

}
