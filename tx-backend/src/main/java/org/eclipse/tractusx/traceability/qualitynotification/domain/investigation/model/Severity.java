package org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Describes the criticality of a notification")
public enum Severity {
    MINOR("MINOR"),
    MAJOR("MAJOR"),
    CRITICAL("CRITICAL"),
    @ApiModelProperty(name = "LIFE-THREATENING")
    LIFE_THREATENING("LIFE-THREATENING");

    Severity(String realName) {
        this.realName = realName;
    }

    public String getRealName() {
        return realName;
    }

    private final String realName;

    public static Severity fromString(String str) {
        for (Severity s : Severity.values()) {
            if (s.realName.equalsIgnoreCase(str)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No enum constant " + Severity.class.getCanonicalName() + "." + str);
    }
}
