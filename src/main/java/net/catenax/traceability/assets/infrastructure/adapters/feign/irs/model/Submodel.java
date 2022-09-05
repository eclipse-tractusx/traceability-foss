package net.catenax.traceability.assets.infrastructure.adapters.feign.irs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

class Submodel {

	@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
		property = "aspectType")
	@JsonSubTypes({
		@JsonSubTypes.Type(value = AssemblyPartRelationship.class, name = "urn:bamm:com.catenax.assembly_part_relationship:1.0.0#AssemblyPartRelationship"),
		@JsonSubTypes.Type(value = SerialPartTypization.class, name = "urn:bamm:com.catenax.serial_part_typization:1.0.0#SerialPartTypization")
	})
	private Object payload;

	@JsonCreator
	public Submodel(@JsonProperty("payload") Object payload) {
		this.payload = payload;
	}

	public Object getPayload() {
		return payload;
	}
}



