package com.badwallet.dto;

import java.util.List;

public class SpecificBillPaymentRequest {
    private String phoneNumber;
    private String serviceName;
    private List<String> factureReferences;

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public List<String> getFactureReferences() { return factureReferences; }
    public void setFactureReferences(List<String> factureReferences) { this.factureReferences = factureReferences; }
}