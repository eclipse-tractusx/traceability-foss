package net.catenax.traceability.assets.domain;

public class AssetNotFoundException extends RuntimeException {

	public AssetNotFoundException(String message) {
		super(message);
	}
}
