package com.example.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.bank.dto.CreateAccountRequest;
import com.example.bank.dto.TopUpRequest;
import com.example.bank.dto.DeductRequest;
import com.example.bank.dto.BankAccountResponse;
import com.example.bank.model.BankAccount;
import com.example.bank.repository.BankAccountRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository accountRepository;

    @Transactional
    public BankAccountResponse createAccount(CreateAccountRequest request) {
        // Check if account already exists
        if (accountRepository.findByAccountNumber(request.getAccountNumber()).isPresent()) {
            throw new RuntimeException("Account number already exists: " + request.getAccountNumber());
        }

        // Create new account
        BankAccount account = new BankAccount();
        account.setAccountNumber(request.getAccountNumber());
        account.setBalance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO);
        
        BankAccount savedAccount = accountRepository.save(account);
        
        return new BankAccountResponse(
                savedAccount.getId(),
                savedAccount.getAccountNumber(),
                savedAccount.getBalance(),
                "Account created successfully"
        );
    }

    @Transactional
    public BankAccountResponse topUpAccount(TopUpRequest request) {
        // Find account
        BankAccount account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found: " + request.getAccountNumber()));
        
        // Validate amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Top-up amount must be positive");
        }
        
        // Update balance
        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);
        
        BankAccount updatedAccount = accountRepository.save(account);
        
        return new BankAccountResponse(
                updatedAccount.getId(),
                updatedAccount.getAccountNumber(),
                updatedAccount.getBalance(),
                "Account topped up successfully"
        );
    }

    @Transactional
    public BankAccountResponse deductMoney(DeductRequest request) {
        // Find account
        BankAccount account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found: " + request.getAccountNumber()));
        
        // Validate amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deduct amount must be positive");
        }
        
        // Check sufficient balance
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance. Current balance: $" + account.getBalance());
        }
        
        // Update balance
        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(newBalance);
        
        BankAccount updatedAccount = accountRepository.save(account);
        
        return new BankAccountResponse(
                updatedAccount.getId(),
                updatedAccount.getAccountNumber(),
                updatedAccount.getBalance(),
                "Money deducted successfully"
        );
    }

    public BankAccountResponse getAccount(String accountNumber) {
        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
        
        return new BankAccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                "Account details retrieved successfully"
        );
    }
}
