package net.catenax.traceability.infrastructure.edc.blackbox;

import okhttp3.MediaType;

public class Constants {

	// validation message
	public static final String ASSET_NOT_MATCH = "Some assets are not available";
	public static final String DESCRIPTION_MUST_PRESENT = "Description must be present";
	public static final String PARTS_IDS_MUST_PRESENT = "PartIds must be present";

	public static final String STATUS_MUST_BE_PRESENT = "status must ne present";
	public static final String ID_MUST_PRESENT = "Id must be present";
	public static final String INVESTIGATION_NOT_FOUND = "Investigation not found";


	// EDC
	public static final String ASSET_TYPE_PROPERTY_NAME = "asset:prop:type";
	public static final String ASSET_TYPE_NOTIFICATION = "qualityinvestigation";
	public static final MediaType JSON = MediaType.get("application/json");


	// only for development purpose.
	public static final String PROVIDER_IDS_PORT = "8282";
	public static final String CONSUMER_DATA_PORT = "9191";


	private Constants() {
	}

}
