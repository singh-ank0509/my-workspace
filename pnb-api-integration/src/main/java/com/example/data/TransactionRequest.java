package com.example.data;

public class TransactionRequest {

    private String reqid;
    private String credit_account;
    private String debit_account;
    private String date;

	public String getReqid() {
		return reqid;
	}

	public void setReqid(String reqid) {
		this.reqid = reqid;
	}

	public String getCredit_account() {
		return credit_account;
	}

	public void setCredit_account(String credit_account) {
		this.credit_account = credit_account;
	}

	public String getDebit_account() {
		return debit_account;
	}

	public void setDebit_account(String debit_account) {
		this.debit_account = debit_account;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
