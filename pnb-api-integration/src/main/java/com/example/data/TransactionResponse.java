package com.example.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionResponse {

	private String transactionId;
    private String amount;
    private String transactionDate;
    private String agencyAccountNumber;
    private String uppclAccountNumber;
    
    @JsonProperty("Status")
    private String status;
    
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
