package com.badwallet.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FactureDTO {
    private Long id;
    private String walletCode;
    private String provider;
    private String reference;
    private BigDecimal amount;
    private LocalDate dueDate;
    private String status;
    private String createdAt;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getWalletCode() { return walletCode; }
    public void setWalletCode(String walletCode) { this.walletCode = walletCode; }
    
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}