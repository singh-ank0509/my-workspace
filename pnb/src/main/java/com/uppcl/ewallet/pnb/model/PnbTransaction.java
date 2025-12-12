package com.uppcl.ewallet.pnb.model;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pnb_transactions_master", uniqueConstraints = { @UniqueConstraint(columnNames = "transaction_id") })
@AllArgsConstructor
@Builder
public class PnbTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", length = 100, unique = true, nullable = false)
    private String transactionId;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_date")
    private String transactionDate;

    @Column(name = "agency_account_number", length = 30)
    private String agencyAccountNumber;

    @Column(name = "uppcl_account_number", length = 30)
    private String uppclAccountNumber;

    @Column(name = "req_id", length = 100)
    private String reqId;
    
    @Column(name = "agency_van_number", length = 20)
    private String agencyVanNumber;

    @Column(name = "agency_id")
    private String agencyId;

    @Column(name = "blk_location", length = 100)
    private String blkLocation;

    @Column(name = "req_hash", columnDefinition = "TEXT")
    private String reqHash;

    @Column(name = "res_hash", columnDefinition = "TEXT")
    private String resHash;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "source_system", length = 50)
    private String sourceSystem;

    @Column(name = "remarks", length = 500)
    private String remarks;
    
    @Column(name = "ewallet_updated", length = 1)
    private String ewalletUpdated;
    
    public PnbTransaction() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getAgencyAccountNumber() {
		return agencyAccountNumber;
	}

	public void setAgencyAccountNumber(String agencyAccountNumber) {
		this.agencyAccountNumber = agencyAccountNumber;
	}

	public String getUppclAccountNumber() {
		return uppclAccountNumber;
	}

	public void setUppclAccountNumber(String uppclAccountNumber) {
		this.uppclAccountNumber = uppclAccountNumber;
	}
	
	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public String getAgencyVanNumber() {
		return agencyVanNumber;
	}

	public void setAgencyVanNumber(String agencyVanNumber) {
		this.agencyVanNumber = agencyVanNumber;
	}

	public String getAgencyId() {
		return agencyId;
	}

	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}

	public String getBlkLocation() {
		return blkLocation;
	}

	public void setBlkLocation(String blkLocation) {
		this.blkLocation = blkLocation;
	}

	public String getReqHash() {
		return reqHash;
	}

	public void setReqHash(String reqHash) {
		this.reqHash = reqHash;
	}

	public String getResHash() {
		return resHash;
	}

	public void setResHash(String resHash) {
		this.resHash = resHash;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(LocalDate requestDate) {
		this.requestDate = requestDate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public String getEwalletUpdated() {
		return ewalletUpdated;
	}

	public void setEwalletUpdated(String ewalletUpdated) {
		this.ewalletUpdated = ewalletUpdated;
	}

	@Override
	public String toString() {
		return "PnbTransaction {id=" + id + ", transactionId=" + transactionId + ", amount=" + amount
				+ ", transactionDate=" + transactionDate + ", agencyAccountNumber=" + agencyAccountNumber
				+ ", uppclAccountNumber=" + uppclAccountNumber + ", reqId=" + reqId + ", agencyVanNumber="
				+ agencyVanNumber + ", agencyId=" + agencyId + ", blkLocation=" + blkLocation + ", reqHash=" + reqHash
				+ ", resHash=" + resHash + ", status=" + status + ", requestDate=" + requestDate + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + ", sourceSystem=" + sourceSystem + ", remarks=" + remarks
				+ ", ewalletUpdated=" + ewalletUpdated + "}";
	}
}