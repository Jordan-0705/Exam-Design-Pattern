package com.badwallet.dto;

import java.math.BigDecimal;

public class WithdrawRequest {
    private String phoneNumber;
    private BigDecimal amount;

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}