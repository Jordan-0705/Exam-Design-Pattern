package com.badwallet.dto;

import java.math.BigDecimal;

public class DepositRequest {
    private BigDecimal amount;
    private String paymentMethod;

    // Getters et Setters
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}