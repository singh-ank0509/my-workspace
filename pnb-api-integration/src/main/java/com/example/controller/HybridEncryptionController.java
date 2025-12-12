package com.example.controller;

import java.security.*;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.data.EncryptedPayload;
import com.example.data.TransactionRequest;
import com.example.data.TransactionResponse;
import com.example.impl.HybridEncryptionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class HybridEncryptionController {
	
	@Autowired
	private HybridEncryptionService encryptionService;
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@GetMapping("/encrypt")
	public EncryptedPayload encrypt(@RequestBody TransactionRequest request) throws Exception {
		PublicKey key = encryptionService.publicKey;
		String plainText = convertTransactionRequestToString(request);
		EncryptedPayload encryptedData = HybridEncryptionService.encrypt(plainText, key);
		return encryptedData;
	}
	
	@GetMapping("/decrypt")
	public List<TransactionResponse> decrypt(@RequestBody EncryptedPayload payload) throws Exception {
		PrivateKey key = encryptionService.privateKey;
		String decryptedData = HybridEncryptionService.decrypt(payload, key);
		List<TransactionResponse> transactions = convertStringToTransactionResponse(decryptedData);
		return transactions;
	}
	
	public static String convertTransactionRequestToString(TransactionRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	public static List<TransactionResponse> convertStringToTransactionResponse(String request) {
        try {
            List<TransactionResponse> transactions = objectMapper.readValue(request, new TypeReference<List<TransactionResponse>>() {});
        	return transactions;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
