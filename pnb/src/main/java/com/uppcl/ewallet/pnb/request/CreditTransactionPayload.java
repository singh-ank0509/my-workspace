package com.uppcl.ewallet.pnb.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditTransactionPayload implements Serializable {

    private String agentId;

    private Double amount;

    private String sourceType;

    private String transactionId;

    private String walletId;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    @Override
    public String toString() {
        return "CreditTransaction {" +
                "agentId='" + agentId + '\'' +
                ", amount=" + amount +
                ", sourceType=" + sourceType +
                ", transactionId='" + transactionId + '\'' +
                ", walletId='" + walletId + '\'' +
                '}';
    }
}