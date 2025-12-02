package com.example.bank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.bank.dto.CreateAccountRequest;
import com.example.bank.dto.TopUpRequest;
import com.example.bank.dto.DeductRequest;
import com.example.bank.dto.BankAccountResponse;
import com.example.bank.service.BankAccountService;

@RestController
@RequestMapping("/api/bank/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        try {
            BankAccountResponse response = bankAccountService.createAccount(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new BankAccountResponse(null, null, null, "Failed to create account: " + e.getMessage())
            );
        }
    }

    @PostMapping("/topup")
    public ResponseEntity<BankAccountResponse> topUpAccount(@RequestBody TopUpRequest request) {
        try {
            BankAccountResponse response = bankAccountService.topUpAccount(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new BankAccountResponse(null, null, null, "Failed to top up account: " + e.getMessage())
            );
        }
    }

    @PostMapping("/deduct")
    public ResponseEntity<BankAccountResponse> deductMoney(@RequestBody DeductRequest request) {
        try {
            BankAccountResponse response = bankAccountService.deductMoney(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new BankAccountResponse(null, null, null, "Failed to deduct money: " + e.getMessage())
            );
        }
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> getAccount(@PathVariable String accountNumber) {
        try {
            BankAccountResponse response = bankAccountService.getAccount(accountNumber);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
