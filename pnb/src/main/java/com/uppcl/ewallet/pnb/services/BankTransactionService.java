package com.uppcl.ewallet.pnb.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.uppcl.ewallet.pnb.model.Agency;
import com.uppcl.ewallet.pnb.model.PnbTransaction;
import com.uppcl.ewallet.pnb.repository.AgencyRepository;
import com.uppcl.ewallet.pnb.repository.PnbTransactionRepository;
import com.uppcl.ewallet.pnb.request.CreditTransactionPayload;
import com.uppcl.ewallet.pnb.request.TokenRequest;
import com.uppcl.ewallet.pnb.response.AcceptedResponse;
import com.uppcl.ewallet.pnb.response.BankTransactionResponse;
import com.uppcl.ewallet.pnb.response.ServiceMessage;
import com.uppcl.ewallet.pnb.util.JsonUtil;
import com.uppcl.ewallet.pnb.util.CRMUniversalMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankTransactionService {

    private final RestTemplate restTemplate;
    private final EncryptionService encryptionService;
//    private final RSAService rsaService;
    private final PnbTransactionRepository pnbTransactionRepository; 
    private final AgencyRepository agencyRepository;
    
    @Value("${bank.api.baseurl}")
    private String bankApiBaseUrl;

    @Value("${bank.api.token-url}")
    private String tokenUrl;

    @Value("${bank.api.client-id}")
    private String clientId;

    @Value("${bank.api.username}")
    private String username;

    @Value("${bank.api.password}")
    private String password;

    @Value("${bank.api.service-name}")
    private String serviceName;
    
    @Value("${bank.api.source-system}")
    private String sourceSystem;
    
    @Value("${admin.api.credit-url}")
    private String CREDIT_URL;
    
    @Value("${admin.api.base64}")
    private String base64;
    
    @Value("${admin.api.access-token-api}")
    private String ACCESS_TOKEN_URL;
    
    @Value("${admin.api.source-type}")
    private String sourceType;
    
    @Value("${admin.api.api-key}")
    private String apiKey;

    private String accessToken;

    public ResponseEntity<?> getDailyCollection(String to, String from, LocalDate date) throws Exception {
    	try {
    		generateTokenIfNeeded();

            Map<String, String> plainRequest = new HashMap<>();
            plainRequest.put("reqid", UUID.randomUUID().toString());
            plainRequest.put("credit_account", to);
            plainRequest.put("debit_account", from);
            plainRequest.put("date", date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

//            String aesKey = encryptionService.generateAESKey();
//            String encryptedData = encryptionService.encrypt(JsonUtil.toJson(plainRequest), aesKey);
//            String encryptedAESKey = rsaService.encryptAESKey(aesKey);
            
            PublicKey publicKey = encryptionService.publicKey;
            CRMUniversalMsg encrypt = encryptionService.encrypt(JsonUtil.toJson(plainRequest), publicKey);
//            System.err.println("encrypt " + encrypt);
//            Map<String, String> requestPayload = new HashMap<>();
//            requestPayload.put("EncData", encrypt.getEncData());
//            requestPayload.put("EncKey", encrypt.getEncKey());

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.set("Client-Id", clientId);
//            headers.set("ServiceName", serviceName);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CRMUniversalMsg> request = new HttpEntity<>(encrypt, headers);

            ResponseEntity<CRMUniversalMsg> response = restTemplate.exchange(
                    bankApiBaseUrl + "/AccountEnquiry",
                    HttpMethod.POST,
                    request,
                    CRMUniversalMsg.class
            );

//            String decryptedAES = rsaService.decryptAESKey(response.getBody().getEncKey());
//            String decryptedJson = encryptionService.decrypt(response.getBody().getEncData(), decryptedAES);
//            System.err.println("response1 " + response.getBody());
            if(response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            	PrivateKey privateKey = encryptionService.privateKey;
                String decryptedJson = encryptionService.decrypt(response.getBody(), privateKey);
                System.err.println("decryptedJson " + decryptedJson);
                List<BankTransactionResponse> bankTxnsResponse = JsonUtil.fromJson(decryptedJson, new TypeReference<List<BankTransactionResponse>>() {});
                BankTransactionResponse bankTxnResponse = bankTxnsResponse.get(0);
                Optional<PnbTransaction> optionalTxn = pnbTransactionRepository.findByTransactionId(bankTxnResponse.getTransactionId());
                PnbTransaction pnbTransaction;
                if (optionalTxn.isPresent()) {
                    pnbTransaction = optionalTxn.get();
                    pnbTransaction.setAmount(BigDecimal.valueOf(bankTxnResponse.getAmount()));
                    pnbTransaction.setEwalletUpdated("N");
                } else {
                	pnbTransaction = PnbTransaction.builder()
    						.agencyAccountNumber(bankTxnResponse.getAgencyAccountNumber())
    						.uppclAccountNumber(bankTxnResponse.getUppclAccountNumber())
    						.transactionId(bankTxnResponse.getTransactionId())
    						.amount(BigDecimal.valueOf(bankTxnResponse.getAmount()))
//    						.transactionDate(bankTxnResponse.getTransactionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
    						.status(bankTxnResponse.getStatus())
    						.requestDate(date)
    						.reqHash(encrypt.toString())
    						.resHash(response.getBody().toString())
    						.sourceSystem(sourceSystem)
    						.ewalletUpdated("N")
    						.build();
                }
				
//				PnbTransaction savedPnbTransaction = pnbTransactionRepository.save(pnbTransaction);
				return ResponseEntity.ok(JsonUtil.fromJson(decryptedJson, new TypeReference<List<BankTransactionResponse>>() {}));
//				return ResponseEntity.ok(pnbTransaction);
            } else {
            	log.info("No transaction exists for the input date.");
//            	PnbTransaction pnbTransaction = PnbTransaction.builder()
//            												.agencyAccountNumber(to)
//            												.uppclAccountNumber(from)
//            												.transactionDate(date)
//            												.status("FAILED")
//            												.requestDate(date)
//            												.createdAt(LocalDateTime.now())
//            												.updatedAt(LocalDateTime.now())
//            												.sourceSystem("PNB_API")
//            												.remarks("No transaction exists for the input date.")
//            												.build();
//            	pnbTransactionRepository.save(pnbTransaction);
            	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No transaction exists for the input date.");
            }
    	} catch(Exception ex) {
    		log.info("Exception occured fetching daily collection...");
    		throw new RuntimeException(ex.getMessage());
    	}        
    }
    
    public BankTransactionResponse getTransactionStatus(String txnId, String accNo) throws Exception {
    	try {
    		generateTokenIfNeeded();

            Map<String, String> plainRequest = new HashMap<>();
            plainRequest.put("transactionId", txnId);
            plainRequest.put("accountNumber", accNo);

//            String aesKey = encryptionService.generateAESKey();
//            String encryptedData = encryptionService.encrypt(JsonUtil.toJson(plainRequest), aesKey);
//            String encryptedAESKey = rsaService.encryptAESKey(aesKey);
            
            PublicKey publicKey = encryptionService.publicKey;
            CRMUniversalMsg encrypt = encryptionService.encrypt(JsonUtil.toJson(plainRequest), publicKey);

//            Map<String, String> requestPayload = new HashMap<>();
//            requestPayload.put("EncData", encryptedData);
//            requestPayload.put("EncAesKey", encryptedAESKey);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.set("Client-Id", clientId);
//            headers.set("ServiceName", serviceName);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CRMUniversalMsg> request = new HttpEntity<>(encrypt, headers);

            ResponseEntity<CRMUniversalMsg> response = restTemplate.exchange(
                    bankApiBaseUrl + "/StatusCheck",
                    HttpMethod.POST,
                    request,
                    CRMUniversalMsg.class
            );

//            String decryptedAES = rsaService.decryptAESKey(response.getBody().getEncKey());
//            String decryptedJson = encryptionService.decrypt(response.getBody().getEncData(), decryptedAES);
            
            PrivateKey privateKey = encryptionService.privateKey;
            String decryptedJson = encryptionService.decrypt(response.getBody(), privateKey);

            return JsonUtil.fromJson(decryptedJson, BankTransactionResponse.class);
    	} catch(Exception ex) {
    		log.info("Exception occured checking transaction status...");
    		throw new RuntimeException(ex.getMessage());
    	}      
    }

    private void generateTokenIfNeeded() {
        if (accessToken != null) return;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        accessToken = (String) response.getBody().get("access_token");
    }
    
    public void updateDailyCollection(String creditAccount, String debitAccount, LocalDate date, String agencyVan, String agencyId) {
    	try {
    		generateTokenIfNeeded();
            Map<String, String> plainRequest = new HashMap<>();
            plainRequest.put("reqid", UUID.randomUUID().toString());
            plainRequest.put("credit_account", creditAccount);
            plainRequest.put("debit_account", debitAccount);
            plainRequest.put("date", date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            
            PublicKey publicKey = encryptionService.publicKey;
            CRMUniversalMsg encrypt = encryptionService.encrypt(JsonUtil.toJson(plainRequest), publicKey);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.set("Client-Id", clientId);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CRMUniversalMsg> request = new HttpEntity<>(encrypt, headers);

            ResponseEntity<CRMUniversalMsg> response = restTemplate.exchange(
                    bankApiBaseUrl + "/AccountEnquiry",
                    HttpMethod.POST,
                    request,
                    CRMUniversalMsg.class
            );

            if(response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            	PrivateKey privateKey = encryptionService.privateKey;
                String decryptedJson = encryptionService.decrypt(response.getBody(), privateKey);
//                System.err.println("decryptedJson " + decryptedJson);
                List<BankTransactionResponse> bankTxnsResponse = JsonUtil.fromJson(decryptedJson, new TypeReference<List<BankTransactionResponse>>() {});
                log.info("Bank Transaction Response: {}", bankTxnsResponse); //
                AtomicInteger count = new AtomicInteger(1);
                bankTxnsResponse.forEach(bankTxnResponse -> { 
                	try {
                		log.info("Txn: {}, id: {}", count.getAndIncrement(), bankTxnResponse.getTransactionId());
//                		BankTransactionResponse bankTxnResponse = bankTxnsResponse.get(0);
                        Optional<PnbTransaction> optionalTxn = pnbTransactionRepository.findByAgencyAccountNumberAndUppclAccountNumberAndTransactionDate(debitAccount, creditAccount, bankTxnResponse.getTransactionDate());
                        PnbTransaction pnbTransaction;
                        if (optionalTxn.isPresent() && optionalTxn.get().getTransactionId().equals(bankTxnResponse.getTransactionId())) {
                        	log.info("Existing Txn id: {}", bankTxnResponse.getTransactionId());
                            pnbTransaction = optionalTxn.get();
                            // condition to avoid updation if ewallet is already updated
                            if(pnbTransaction.getEwalletUpdated().equalsIgnoreCase("Y")) {
                                log.info("E-wallet is already updated...");
                            	return;
                            }
                            pnbTransaction.setReqId(plainRequest.get("reqid")); // added reqId
                            pnbTransaction.setAmount(BigDecimal.valueOf(bankTxnResponse.getAmount()));
                            pnbTransaction.setReqHash(encrypt.toString());
                            pnbTransaction.setResHash(response.getBody().toString());
                            pnbTransaction.setEwalletUpdated("N");
                        } else {
                        	pnbTransaction = PnbTransaction.builder()
                                    .reqId(plainRequest.get("reqid"))
            						.agencyAccountNumber(bankTxnResponse.getAgencyAccountNumber())
            						.uppclAccountNumber(bankTxnResponse.getUppclAccountNumber())
            						.transactionId(bankTxnResponse.getTransactionId())
            						.amount(BigDecimal.valueOf(bankTxnResponse.getAmount()))
            						.transactionDate(bankTxnResponse.getTransactionDate())
            						.status(bankTxnResponse.getStatus())
            						.requestDate(date)
            						.reqHash(encrypt.toString())
            						.resHash(response.getBody().toString())
            						.agencyId(agencyId)
            						.agencyVanNumber(agencyVan)
            						.sourceSystem(sourceSystem)
            						.ewalletUpdated("N")
            						.build();
                        }
        				                
                        CreditTransactionPayload creditTransactionPayload = new CreditTransactionPayload();
                        creditTransactionPayload.setAgentId(agencyId);
                        creditTransactionPayload.setTransactionId(pnbTransaction.getTransactionId());
                        creditTransactionPayload.setSourceType(sourceType);
                        creditTransactionPayload.setAmount(pnbTransaction.getAmount().doubleValue());
                        
                        ResponseEntity<?> creditApiResponse = callCreditApi(creditTransactionPayload);
                        log.info("Credit API Status: " + creditApiResponse.getStatusCode() + " Body: " + creditApiResponse.getBody());
                        if (creditApiResponse.getStatusCode() == HttpStatus.ACCEPTED) {
        					AcceptedResponse acceptedResponse = (AcceptedResponse) creditApiResponse.getBody();
        					pnbTransaction.setBlkLocation(acceptedResponse.getLocation());
        					pnbTransaction.setEwalletUpdated("Y");
                        } else {
                            log.error("Credit API call failed: Status = {}, Body = {}", creditApiResponse.getStatusCode(), creditApiResponse.getBody());
                            Object body = creditApiResponse.getBody();
                            
                            if (body instanceof ServiceMessage) {
                            	ServiceMessage serviceMessage = (ServiceMessage) body;
                            	String message = serviceMessage.getMessage();
                                pnbTransaction.setRemarks(message);
                                if (message != null && message.contains("Payment for transaction id")
                                        && message.contains("is already committed to agent")) {
                                    pnbTransaction.setEwalletUpdated("Y");
                                }
                            } else {
                            	log.info("Error: API call failed...");
                            	if (body.toString() != null && body.toString().contains("Payment for transaction id")
                                        && body.toString().contains("is already committed to agent")) {
                                    pnbTransaction.setEwalletUpdated("Y");
                                }
                                pnbTransaction.setRemarks(body.toString());
                            }
                        }
                        
        				PnbTransaction savedPnbTransaction = pnbTransactionRepository.save(pnbTransaction);
        				log.info("Saved Pnb Transaction: {}", savedPnbTransaction);
                	} catch(Exception ex) {
                        log.error("Error processing BankTransactionResponses: {}", ex.getMessage(), ex);
                        return;
                	}
                });                
            } else {
            	log.info("No transaction exists for the credit_account: {}, debit_account: {} and input date: {}", creditAccount, debitAccount, date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            	return;
            }
    	} catch(Exception ex) {
    		log.info("Exception occured fetching daily collection...");
    		throw new RuntimeException(ex.getMessage());
    	}   
    }

    private ResponseEntity<?> callCreditApi(CreditTransactionPayload creditTransactionPayload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apikey", apiKey);
            TokenRequest request = new TokenRequest();
            request.setBase64(base64);
            
            HttpEntity<TokenRequest> tokenRequest = new HttpEntity<>(request, headers);
            ResponseEntity<String> tokenResponse = restTemplate.exchange(ACCESS_TOKEN_URL, HttpMethod.POST, tokenRequest, String.class);
            if (tokenResponse.getStatusCode() == HttpStatus.OK) {
            	JsonElement jsonElement = new Gson().fromJson(tokenResponse.getBody(), JsonElement.class);
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				String access_token = null;
				if (jsonObject.has("access_token")) { 
					access_token = jsonObject.get("access_token").getAsString();
				}
				
				headers.setBearerAuth(access_token);
            	HttpEntity<CreditTransactionPayload> creditRequest = new HttpEntity<>(creditTransactionPayload, headers);
                ResponseEntity<String> creditResponse = restTemplate.exchange(CREDIT_URL, HttpMethod.POST, creditRequest, String.class);
    			String responseBody = creditResponse.getBody();
    			if (creditResponse.getStatusCode() == HttpStatus.ACCEPTED) {
    				AcceptedResponse acceptedResponse = new Gson().fromJson(responseBody, AcceptedResponse.class);
    				return ResponseEntity.accepted().body(acceptedResponse);
    			} else {
    				ServiceMessage serviceMessage = new Gson().fromJson(responseBody, ServiceMessage.class);
    				return ResponseEntity.status(creditResponse.getStatusCode()).body(serviceMessage);
    			}
            } else {
				return ResponseEntity.status(tokenResponse.getStatusCode()).body(tokenResponse.getBody());
            }
        } catch (HttpClientErrorException ex) {
            log.error("HttpClientErrorException: Failed to call credit API", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            log.error("Exception: Failed to call credit API", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
        }
    }
    
    @Scheduled(cron = "${scheduler.dailyCollection}", zone = "Asia/Kolkata")
    public void scheduleDailyCollection() {
    	log.info("Daily Collection API scheduler started: {}", LocalDateTime.now());
    	List<Agency> agencies = agencyRepository.findAll();
    	for(Agency agency: agencies) {
    		log.info("agency: {}, agencyVan: {}", agency.getId(), agency.getAgencyVan());
    		try {
    			updateDailyCollection(agency.getUppclAccountNumber(), agency.getAgencyAccountNumber(), LocalDate.now(), agency.getAgencyVan(), agency.getAgencyId());
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Exception - Daily collection scheduler failed: ", e.getMessage());
			}
    	}
    	log.info("Daily Collection API scheduler completed: {}", LocalDateTime.now());
    }

	public void triggerScheduler(LocalDate date) {
		triggerDailyCollectionScheduler(date);
	}
	
	public void triggerDailyCollectionScheduler(LocalDate date) {
    	log.info("Daily Collection API scheduler triggered manually: {}", LocalDateTime.now());
    	List<Agency> agencies = agencyRepository.findAll();
    	for(Agency agency: agencies) {
    		log.info("agency: {}, agencyVan: {}", agency.getId(), agency.getAgencyVan());
    		try {
    			updateDailyCollection(agency.getUppclAccountNumber(), agency.getAgencyAccountNumber(), date, agency.getAgencyVan(), agency.getAgencyId());
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Exception - Daily collection scheduler triggered manually failed: ", e.getMessage());
			}
    	}
    	log.info("Daily Collection API scheduler triggered manually completed: {}", LocalDateTime.now());
    }
}
