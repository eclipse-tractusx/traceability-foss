package net.catenax.traceability.infrastructure.edc.blackbox.model;

import java.util.List;

public class GetSdHubResponse {

	private String id;
	private List<VerifiableCredential> verifiableCredential;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<VerifiableCredential> getVerifiableCredential() {
		return verifiableCredential;
	}

	public void setVerifiableCredential(List<VerifiableCredential> verifiableCredential) {
		this.verifiableCredential = verifiableCredential;
	}

	public record VerifiableCredential(String id, List<String> type, CredentialSubject credentialSubject) {
	}

	public record CredentialSubject(String bpn, String service_provider) {
	}
}
