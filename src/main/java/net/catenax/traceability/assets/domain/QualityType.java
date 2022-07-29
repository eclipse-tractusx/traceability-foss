package net.catenax.traceability.assets.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum QualityType {
	OK("Ok"),
	MINOR("Minor"),
	MAJOR("Major"),
	CRITICAL("Critical"),
	LIFE_THREATENING("LifeThreatening");

	private final String description;

	QualityType(String description) {
		this.description = description;
	}

	@JsonValue
	public String getDescription() {
		return description;
	}
}
