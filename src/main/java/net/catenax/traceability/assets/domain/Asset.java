package net.catenax.traceability.assets.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public record Asset(
	String id,
	String idShort,
	String nameAtManufacturer,
	String manufacturerPartId,
	String manufacturerId,
	String manufacturerName,
	String nameAtCustomer,
	String customerPartId,
	@JsonFormat(shape = JsonFormat.Shape.STRING) Instant manufacturingDate,
	String manufacturingCountry,
	Map<String, String> specificAssetIds,
	List<ChildDescriptions> childDescriptions,
	QualityType qualityType
) {

	public Asset(
		String id,
		String idShort,
		String nameAtManufacturer,
		String manufacturerPartId,
		String manufacturerId,
		String manufacturerName,
		String nameAtCustomer,
		String customerPartId,
		Instant manufacturingDate,
		String manufacturingCountry,
		QualityType qualityType
	) {
		this(
			id,
			idShort,
			nameAtManufacturer,
			manufacturerPartId,
			manufacturerId,
			manufacturerName,
			nameAtCustomer,
			customerPartId,
			manufacturingDate,
			manufacturingCountry,
			Collections.emptySortedMap(),
			Collections.emptyList(),
			qualityType
		);
	}

	public Asset withChildDescriptions(List<ChildDescriptions> childDescriptions) {
		return new Asset(
			id,
			idShort,
			nameAtManufacturer,
			manufacturerPartId,
			manufacturerId,
			manufacturerName,
			nameAtCustomer,
			customerPartId,
			manufacturingDate,
			manufacturingCountry,
			Collections.emptySortedMap(),
			childDescriptions,
			qualityType
		);
	}

	public Asset withManufacturerName(String manufacturerName) {
		return new Asset(
			id,
			idShort,
			nameAtManufacturer,
			manufacturerPartId,
			manufacturerId,
			manufacturerName,
			nameAtCustomer,
			customerPartId,
			manufacturingDate,
			manufacturingCountry,
			specificAssetIds,
			childDescriptions,
			qualityType
		);
	}

	public Asset update(QualityType qualityType) {
		return new Asset(
			id,
			idShort,
			nameAtManufacturer,
			manufacturerPartId,
			manufacturerId,
			manufacturerName,
			nameAtCustomer,
			customerPartId,
			manufacturingDate,
			manufacturingCountry,
			specificAssetIds,
			childDescriptions,
			qualityType
		);
	}

	public record ChildDescriptions(String id, String idShort){}

}
