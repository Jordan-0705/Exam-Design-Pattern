package com.badwallet.controller;

import com.badwallet.client.PaymentServiceClient;
import com.badwallet.dto.FactureDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/external/factures")
public class ExternalController {

    private final PaymentServiceClient paymentServiceClient;

    public ExternalController(PaymentServiceClient paymentServiceClient) {
        this.paymentServiceClient = paymentServiceClient;
    }

    // 2.2 Consulter les factures impayées du mois en cours
    @GetMapping("/{walletCode}/current")
    public List<FactureDTO> getCurrentFactures(
            @PathVariable String walletCode,
            @RequestParam(required = false) String unite) {
        if (unite != null) {
            return paymentServiceClient.getUnpaidFacturesByProvider(walletCode, unite);
        }
        return paymentServiceClient.getUnpaidFactures(walletCode);
    }

    // 2.4 Consulter les factures impayées sur une période
    @GetMapping("/{walletCode}/periode")
    public List<FactureDTO> getFacturesByPeriod(
            @PathVariable String walletCode,
            @RequestParam String debut,
            @RequestParam String fin) {
        return paymentServiceClient.getUnpaidFacturesByPeriod(walletCode, debut, fin);
    }
}