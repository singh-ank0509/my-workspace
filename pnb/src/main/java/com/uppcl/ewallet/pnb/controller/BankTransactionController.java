package com.uppcl.ewallet.pnb.controller;

import com.uppcl.ewallet.pnb.response.BankTransactionResponse;
import com.uppcl.ewallet.pnb.services.BankTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pnb")
@RequiredArgsConstructor
public class BankTransactionController {

    private final BankTransactionService bankService;

    @GetMapping("/collection")
    public ResponseEntity<?> getDailyCollection(
            @RequestParam String accountNumberTo,
            @RequestParam String accountNumberFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws Exception {
        return bankService.getDailyCollection(accountNumberTo, accountNumberFrom, date);
    }

    @GetMapping("/status")
    public ResponseEntity<BankTransactionResponse> getTransactionStatus(
            @RequestParam String transactionId,
            @RequestParam String accountNumber) throws Exception {
        return ResponseEntity.ok(bankService.getTransactionStatus(transactionId, accountNumber));
    }
    
    @GetMapping("/triggerScheduler")
    public void triggerScheduler(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws Exception {
        bankService.triggerScheduler(date);
    }
}

