package com.example.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.bank.dto.PaymentRequestMessage;
import com.example.bank.dto.RefundRequestMessage;
import com.example.bank.model.BankAccount;
import com.example.bank.model.BankTransaction;
import com.example.bank.model.TransactionStatus;
import com.example.bank.repository.BankAccountRepository;
import com.example.bank.repository.BankTransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BankAccountRepository accountRepository;
    private final BankTransactionRepository transactionRepository;
    private final RabbitMQService rabbitMQService;

    @Transactional
    public PaymentResult processPayment(PaymentRequestMessage request) {
        // 1. get accounts
        BankAccount fromAccount = accountRepository.findByAccountNumber(request.getFromAccount())
                .orElseThrow(() -> new RuntimeException("From account not found: " + request.getFromAccount()));
        
        BankAccount toAccount = accountRepository.findByAccountNumber(request.getToAccount())
                .orElseThrow(() -> new RuntimeException("To account not found: " + request.getToAccount()));

        // 2. check balance
        BigDecimal amount = BigDecimal.valueOf(request.getAmount());
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            PaymentResult result = new PaymentResult("FAILED", null, "Insufficient balance");
            sendPaymentResponse(request.getOrderId(), result.getStatus(), result.getTransactionId(), result.getMessage());
            return result;
        }

        // 3. transfer
        try {
            // deduct from sender
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            accountRepository.save(fromAccount);
            
            // add to receiver
            toAccount.setBalance(toAccount.getBalance().add(amount));
            accountRepository.save(toAccount);
            
            // 4. create transaction record
            BankTransaction transaction = new BankTransaction();
            transaction.setFromAccount(request.getFromAccount());
            transaction.setToAccount(request.getToAccount());
            transaction.setAmount(amount);
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setOrderId(request.getOrderId());
            
            BankTransaction savedTransaction = transactionRepository.save(transaction);
            
            // 5. send success message
            PaymentResult result = new PaymentResult("SUCCESS", savedTransaction.getId().toString(), "Payment successful");
            sendPaymentResponse(request.getOrderId(), result.getStatus(), result.getTransactionId(), result.getMessage());
            return result;
            
        } catch (Exception e) {
            PaymentResult result = new PaymentResult("FAILED", null, "Payment failed: " + e.getMessage());
            sendPaymentResponse(request.getOrderId(), result.getStatus(), result.getTransactionId(), result.getMessage());
            return result;
        }
    }
    
    // Inner class to hold payment result
    public static class PaymentResult {
        private String status;
        private String transactionId;
        private String message;
        
        public PaymentResult(String status, String transactionId, String message) {
            this.status = status;
            this.transactionId = transactionId;
            this.message = message;
        }
        
        public String getStatus() { return status; }
        public String getTransactionId() { return transactionId; }
        public String getMessage() { return message; }
    }

    @Transactional
    public void processRefund(RefundRequestMessage request) {
        // 1. get accounts
        BankAccount fromAccount = accountRepository.findByAccountNumber(request.getFromAccount())
                .orElseThrow(() -> new RuntimeException("From account not found: " + request.getFromAccount()));
        
        BankAccount toAccount = accountRepository.findByAccountNumber(request.getToAccount())
                .orElseThrow(() -> new RuntimeException("To account not found: " + request.getToAccount()));

        // 2. check balance
        BigDecimal amount = BigDecimal.valueOf(request.getAmount());
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            sendPaymentResponse(request.getOrderId(), "FAILED", null, "Insufficient balance for refund");
            return;
        }

        // 3. transfer (refund: from store to customer)
        try {
            // deduct from store account
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            accountRepository.save(fromAccount);
            
            // add to customer account
            toAccount.setBalance(toAccount.getBalance().add(amount));
            accountRepository.save(toAccount);
            
            // 4. create transaction record
            BankTransaction transaction = new BankTransaction();
            transaction.setFromAccount(request.getFromAccount());
            transaction.setToAccount(request.getToAccount());
            transaction.setAmount(amount);
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setOrderId(request.getOrderId());
            
            BankTransaction savedTransaction = transactionRepository.save(transaction);
            
            // 5. send success message
            sendPaymentResponse(
                request.getOrderId(), 
                "SUCCESS", 
                savedTransaction.getId().toString(), 
                "Refund successful"
            );
            
        } catch (Exception e) {
            sendPaymentResponse(request.getOrderId(), "FAILED", null, "Refund failed: " + e.getMessage());
        }
    }

    public void sendPaymentResponse(Integer orderId, String status, String transactionId, String message) {
        rabbitMQService.sendPaymentResponse(orderId, status, transactionId, message);
    }
}
