package org.eclipse.tractusx.traceability.test.validator;

import lombok.Getter;
import org.eclipse.tractusx.traceability.test.tooling.rest.response.QualityNotificationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.test.validator.InvestigationValidator.SupportedFields.ACCEPT_REASON;
import static org.eclipse.tractusx.traceability.test.validator.InvestigationValidator.SupportedFields.CHANNEL;
import static org.eclipse.tractusx.traceability.test.validator.InvestigationValidator.SupportedFields.CLOSE_REASON;
import static org.eclipse.tractusx.traceability.test.validator.InvestigationValidator.SupportedFields.DECLINE_REASON;
import static org.eclipse.tractusx.traceability.test.validator.InvestigationValidator.SupportedFields.DESCRIPTION;
import static org.eclipse.tractusx.traceability.test.validator.InvestigationValidator.SupportedFields.SEVERITY;
import static org.eclipse.tractusx.traceability.test.validator.InvestigationValidator.SupportedFields.STATUS;
import static org.eclipse.tractusx.traceability.test.validator.InvestigationValidator.SupportedFields.TARGET_DATE;

public class InvestigationValidator {

    private final static String UNSUPPORTED_VALIDATION_MESSAGE = "Test validation for following fields %s is not supported currently supported fields are %s. Request developers to implement missing validation :)";

    public static void validateInvestigation(QualityNotificationResponse investigation, Map<String, String> fieldsToCheck) {
        checkIfMapHasSupportedValidationRequest(fieldsToCheck);

        if (fieldsToCheck.containsKey(STATUS.getFieldName())) {
            assertThat(investigation.getStatus().name())
                    .isEqualTo(fieldsToCheck.get(STATUS.getFieldName()));
        }

        if (fieldsToCheck.containsKey(DESCRIPTION.getFieldName())) {
            assertThat(StringUtils.unWrapStringWithTimestamp(investigation.getDescription()))
                    .isEqualTo(fieldsToCheck.get(DESCRIPTION.getFieldName()));
        }

        if (fieldsToCheck.containsKey(CHANNEL.getFieldName())) {
            assertThat(investigation.getChannel().name())
                    .isEqualTo(fieldsToCheck.get(CHANNEL.getFieldName()));
        }

        if (fieldsToCheck.containsKey(CLOSE_REASON.getFieldName())) {
            assertThat(investigation.getReason().close())
                    .isEqualTo(fieldsToCheck.get(CLOSE_REASON.getFieldName()));
        }

        if (fieldsToCheck.containsKey(ACCEPT_REASON.getFieldName())) {
            assertThat(investigation.getReason().accept())
                    .isEqualTo(fieldsToCheck.get(ACCEPT_REASON.getFieldName()));
        }

        if (fieldsToCheck.containsKey(DECLINE_REASON.getFieldName())) {
            assertThat(investigation.getReason().decline())
                    .isEqualTo(fieldsToCheck.get(DECLINE_REASON.getFieldName()));
        }

        if (fieldsToCheck.containsKey(SEVERITY.getFieldName())) {
            assertThat(investigation.getSeverity().getRealName())
                    .isEqualTo(fieldsToCheck.get(SEVERITY.getFieldName()));
        }

        if (fieldsToCheck.containsKey(TARGET_DATE.getFieldName())) {
            assertThat(investigation.getTargetDate())
                    .isEqualTo(fieldsToCheck.get(TARGET_DATE.getFieldName()));
        }
    }

    private static void checkIfMapHasSupportedValidationRequest(Map<String, String> fieldsToCheck) {
        final List<String> supportedFieldValidations = Arrays.stream(SupportedFields.values())
                .map(SupportedFields::getFieldName)
                .toList();
        final List<String> unsupportedFields = fieldsToCheck.keySet().stream()
                .filter(fieldName ->
                        !supportedFieldValidations
                                .contains(fieldName))
                .toList();
        if (!unsupportedFields.isEmpty()) {
            throw new UnsupportedOperationException(
                    UNSUPPORTED_VALIDATION_MESSAGE.formatted(unsupportedFields, supportedFieldValidations)
            );
        }
    }

    @Getter
    enum SupportedFields {
        STATUS("status"),
        DESCRIPTION("description"),
        CHANNEL("channel"),
        CLOSE_REASON("closeReason"),
        ACCEPT_REASON("acceptReason"),
        DECLINE_REASON("declineReason"),
        SEVERITY("severity"),
        TARGET_DATE("targetDate");

        private final String fieldName;

        SupportedFields(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}
