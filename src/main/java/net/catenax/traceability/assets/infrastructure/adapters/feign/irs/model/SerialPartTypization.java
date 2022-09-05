package net.catenax.traceability.assets.infrastructure.adapters.feign.irs.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

record SerialPartTypization(
	String catenaXId,
	PartTypeInformation partTypeInformation,
	ManufacturingInformation manufacturingInformation,
	List<LocalId> localIdentifiers
) {}

record ManufacturingInformation(
	String country,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss", timezone = "CET") Date date
) {}

record PartTypeInformation(
	String nameAtManufacturer,
	String nameAtCustomer,
	String manufacturerPartID,
	String customerPartId
) {}

record LocalId(
	@JsonProperty("key") LocalIdType type,
	String value
) {}

enum LocalIdType {
	@JsonProperty("ManufacturerID")
	MANUFACTURER_ID,
	@JsonProperty("ManufacturerPartID")
	MANUFACTURER_PART_ID,
	@JsonProperty("PartInstanceID")
	PART_INSTANCE_ID,
	@JsonEnumDefaultValue UNKNOWN
}
