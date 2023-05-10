package org.eclipse.tractusx.traceability.qualitynotification.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Describes the criticality of a notification")
public enum QualityNotificationSeverity {
    MINOR("MINOR"),
    MAJOR("MAJOR"),
    CRITICAL("CRITICAL"),
    @ApiModelProperty(name = "LIFE-THREATENING")
    LIFE_THREATENING("LIFE-THREATENING");

    QualityNotificationSeverity(String realName) {
        this.realName = realName;
    }

    public String getRealName() {
        return realName;
    }

    private final String realName;

    public static QualityNotificationSeverity fromString(String str) {
        for (QualityNotificationSeverity s : QualityNotificationSeverity.values()) {
            if (s.realName.equalsIgnoreCase(str)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No enum constant " + QualityNotificationSeverity.class.getCanonicalName() + "." + str);
    }
}
