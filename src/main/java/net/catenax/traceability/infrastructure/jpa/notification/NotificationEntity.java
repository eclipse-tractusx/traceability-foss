package net.catenax.traceability.infrastructure.jpa.notification;

import net.catenax.traceability.assets.infrastructure.adapters.jpa.asset.AssetEntity;
import net.catenax.traceability.infrastructure.jpa.investigation.InvestigationEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "notification")
public class NotificationEntity {

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	private String id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "investigation_id")
	private InvestigationEntity investigation;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
		name = "assets_notifications",
		joinColumns = @JoinColumn(name = "notification_id"),
		inverseJoinColumns = @JoinColumn(name = "asset_id")
	)
	private List<AssetEntity> assets;

	private String senderBpnNumber;
	private String receiverBpnNumber;
	private String edcUrl;
	private String contractAgreementId;
	private String notificationReferenceId;

	public NotificationEntity() {
	}

	public NotificationEntity(InvestigationEntity investigation, String senderBpnNumber, String receiverBpnNumber, List<AssetEntity> assets, String notificationReferenceId) {
		this.investigation = investigation;
		this.senderBpnNumber = senderBpnNumber;
		this.receiverBpnNumber = receiverBpnNumber;
		this.assets = assets;
		this.notificationReferenceId = notificationReferenceId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public InvestigationEntity getInvestigation() {
		return investigation;
	}

	public void setInvestigation(InvestigationEntity investigationsId) {
		this.investigation = investigationsId;
	}

	public String getSenderBpnNumber() {
		return senderBpnNumber;
	}

	public void setSenderBpnNumber(String senderBpnNumber) {
		this.senderBpnNumber = senderBpnNumber;
	}

	public String getReceiverBpnNumber() {
		return receiverBpnNumber;
	}

	public void setReceiverBpnNumber(String bpnNumber) {
		this.receiverBpnNumber = bpnNumber;
	}

	public String getEdcUrl() {
		return edcUrl;
	}

	public void setEdcUrl(String edcUrl) {
		this.edcUrl = edcUrl;
	}

	public String getContractAgreementId() {
		return contractAgreementId;
	}

	public void setContractAgreementId(String contractAgreementId) {
		this.contractAgreementId = contractAgreementId;
	}

	public String getNotificationReferenceId() {
		return notificationReferenceId;
	}

	public void setNotificationReferenceId(String notificationReferenceId) {
		this.notificationReferenceId = notificationReferenceId;
	}

	public List<AssetEntity> getAssets() {
		return assets;
	}

	public void setAssets(List<AssetEntity> assets) {
		this.assets = assets;
	}
}
