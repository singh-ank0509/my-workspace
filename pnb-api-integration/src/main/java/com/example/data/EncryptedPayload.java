package com.example.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EncryptedPayload {

	@JsonProperty("EncKey")
	private String encryptedKey;
	
	@JsonProperty("EncData")
	private String encryptedData;

	public String getEncryptedKey() {
		return encryptedKey;
	}

	public void setEncryptedKey(String encryptedKey) {
		this.encryptedKey = encryptedKey;
	}

	public String getEncryptedData() {
		return encryptedData;
	}

	public void setEncryptedData(String encryptedData) {
		this.encryptedData = encryptedData;
	}
}
