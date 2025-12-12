package com.uppcl.ewallet.pnb.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CRMUniversalMsg {
	
	@JsonProperty("EncData")
    private String encData;

    @JsonProperty("EncKey")
    private String encKey;

	@Override
	public String toString() {
		return "CRMUniversalMsg {EncData=" + encData + ", EncKey=" + encKey + "}";
	}    
}
