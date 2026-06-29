package com.badwallet.dto;

import java.math.BigDecimal;

public class BillPaymentRequest {
    private String phoneNumber;
    private String serviceName;
    private BigDecimal amount;

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}