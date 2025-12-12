package com.uppcl.ewallet.pnb.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankTransactionResponse {
    private String transactionId;
    private Double amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private String transactionDate;

    private String agencyAccountNumber;
    private String uppclAccountNumber;

    @JsonProperty("Status")
    private String status;
}
