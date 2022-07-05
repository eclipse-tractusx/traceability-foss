package net.catenax.traceability.assets;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import net.catenax.traceability.assets.Asset.ChildDescriptions;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AssetsReader {

	private static final String EMPTY_TEXT = "--";

	private final Faker faker = new Faker();

	public static Map<String, Asset> readAssets()  {
		return new AssetsReader().readAndConvertAssets();
	}

	private Map<String, Asset> readAndConvertAssets()  {
		try {
			InputStream file = AssetsReader.class.getResourceAsStream("/data/assets.json");

			ObjectMapper mapper = new ObjectMapper()
				.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			Map<String, RawAsset> rawAssets = mapper.readValue(file, RawAssets.class).data().stream()
				.collect(Collectors.toMap(RawAsset::catenaXId, Function.identity()));

			return rawAssets.values().stream()
				.filter(raw -> Objects.nonNull(raw.serialPartTypization))
				.map(raw -> new Asset(
					raw.catenaXId(),
					raw.shortId(),
					raw.nameAtManufacturer(),
					raw.manufacturerPartId(),
					raw.manufacturerId(),
					"--",
					raw.nameAtCustomer(),
					raw.customerPartId(),
					raw.manufacturingDate(),
					faker.country().countryCode3().toUpperCase(),
					Collections.emptySortedMap(),
					raw.childParts().stream()
						.map(child -> new ChildDescriptions(child.childCatenaXId(), rawAssets.get(child.childCatenaXId).shortId()))
						.collect(Collectors.toList()),
					QualityType.OK
				))
				.collect(Collectors.toConcurrentMap(Asset::id, Function.identity()));
		} catch (IOException e) {
			return Collections.emptyMap();
		}
	}

	public record PartTypeInformation(
		String nameAtManufacturer,
		String nameAtCustomer,
		String manufacturerPartID,
		String customerPartId
	) {}

	public record ChildPart(
		String childCatenaXId
	) {}

	public record SerialPartTypization(
		PartTypeInformation partTypeInformation,
		ManufacturingInformation manufacturingInformation,
		List<LocalId> localIdentifiers
	) {

		public String manufacturerId() {
			if (localIdentifiers == null) {
				return EMPTY_TEXT;
			}
			return localIdentifiers.stream()
				.filter(localId -> localId.type == LocalIdType.ManufacturerID)
				.findFirst()
				.map(LocalId::value)
				.orElse(EMPTY_TEXT);
		}

		public String manufacturingCountry() {
			if (manufacturingInformation == null) {
				return EMPTY_TEXT;
			}
			return manufacturingInformation.country;
		}

		public Instant manufacturingDate() {
			if (manufacturingInformation == null) {
				return null;
			}
			return Optional.ofNullable(manufacturingInformation.date)
				.map(Date::toInstant)
				.orElse(null);
		}
	}

	public record AssemblyPartRelationship(
		List<ChildPart> childParts
	) {}

	public record ManufacturingInformation(
		String country,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss", timezone = "CET") Date date
	) {}

	public enum LocalIdType {
		ManufacturerID,
		ManufacturerPartID,
		PartInstanceID,
		@JsonEnumDefaultValue Unknown
	}

	public record LocalId(
		@JsonProperty("key") LocalIdType type,
		String value
	) {}

	public record AASData(
		String identification,
		String idShort
	) {}

	public record RawAssets(
		@JsonProperty("https://catenax.io/schema/TestDataContainer/1.0.0") List<RawAsset> data
	) {}

	public record RawAsset(
		String catenaXId,
		@JsonProperty("https://catenax.io/schema/SerialPartTypization/1.0.0") List<SerialPartTypization> serialPartTypization,
		@JsonProperty("https://catenax.io/schema/AssemblyPartRelationship/1.0.0") List<AssemblyPartRelationship> assemblyPartRelationships,
		@JsonProperty("https://catenax.io/schema/AAS/3.0") List<AASData> aasData
	) {

		public List<ChildPart> childParts() {
			if (assemblyPartRelationships == null) {
				return Collections.emptyList();
			}
			return assemblyPartRelationships.stream()
				.findFirst()
				.map(AssemblyPartRelationship::childParts)
				.orElse(Collections.emptyList());
		}

		public String nameAtManufacturer() {
			if (serialPartTypization == null) {
				return EMPTY_TEXT;
			}
			return serialPartTypization.stream().findFirst()
				.map(SerialPartTypization::partTypeInformation)
				.map(PartTypeInformation::nameAtManufacturer)
				.orElse(EMPTY_TEXT);
		}

		public String manufacturerPartId() {
			if (serialPartTypization == null) {
				return EMPTY_TEXT;
			}
			return serialPartTypization.stream().findFirst()
				.map(SerialPartTypization::partTypeInformation)
				.map(PartTypeInformation::manufacturerPartID)
				.orElse(EMPTY_TEXT);
		}

		public String manufacturerId() {
			if (serialPartTypization == null) {
				return EMPTY_TEXT;
			}
			return serialPartTypization.stream().findFirst()
				.map(SerialPartTypization::manufacturerId)
				.orElse(EMPTY_TEXT);
		}

		public String nameAtCustomer() {
			if (serialPartTypization == null) {
				return EMPTY_TEXT;
			}
			return serialPartTypization.stream().findFirst()
				.map(SerialPartTypization::partTypeInformation)
				.map(PartTypeInformation::nameAtCustomer)
				.orElse(EMPTY_TEXT);
		}

		public String customerPartId() {
			if (serialPartTypization == null) {
				return EMPTY_TEXT;
			}
			return serialPartTypization.stream().findFirst()
				.map(SerialPartTypization::partTypeInformation)
				.map(PartTypeInformation::customerPartId)
				.orElse(EMPTY_TEXT);
		}

		public Instant manufacturingDate() {
			if (serialPartTypization == null) {
				return null;
			}
			return serialPartTypization.stream().findFirst()
				.map(SerialPartTypization::manufacturingDate)
				.orElse(null);
		}

		public String manufacturingCountry() {
			if (serialPartTypization == null) {
				return EMPTY_TEXT;
			}
			return serialPartTypization.stream().findFirst()
				.map(SerialPartTypization::manufacturingCountry)
				.orElse(EMPTY_TEXT);
		}

		public String shortId() {
			if (aasData == null) {
				return EMPTY_TEXT;
			}
			return aasData.stream().findFirst()
				.map(AASData::idShort)
				.orElse(EMPTY_TEXT);
		}
	}

}
