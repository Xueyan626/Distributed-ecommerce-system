package com.example.bank.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "from_account", length = 20)
    private String fromAccount;
    
    @Column(name = "to_account", length = 20)
    private String toAccount;
    
    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionStatus status;
    
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "order_id")
    private Integer orderId;
    
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
    
    @ManyToOne
    @JoinColumn(name = "from_account", referencedColumnName = "account_number", insertable = false, updatable = false)
    private BankAccount fromAccountEntity;
    
    @ManyToOne
    @JoinColumn(name = "to_account", referencedColumnName = "account_number", insertable = false, updatable = false)
    private BankAccount toAccountEntity;
}
