// badwallet-api/src/main/java/com/badwallet/model/Transaction.java

package com.badwallet.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet; // Wallet émetteur
    
    @ManyToOne
    @JoinColumn(name = "receiver_wallet_id")
    private Wallet receiverWallet; // Wallet destinataire
    
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal fee;
    private String reference;
    private String description;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // ============ GETTERS ET SETTERS ============
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Wallet getWallet() { return wallet; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }
    
    public Wallet getReceiverWallet() { return receiverWallet; }
    public void setReceiverWallet(Wallet receiverWallet) { this.receiverWallet = receiverWallet; }
    
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
    
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}