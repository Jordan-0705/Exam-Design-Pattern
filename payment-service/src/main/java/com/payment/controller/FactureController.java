package com.payment.controller;

import com.payment.model.Facture;
import com.payment.service.FactureService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/factures")
public class FactureController {

    private final FactureService factureService;

    public FactureController(FactureService factureService) {
        this.factureService = factureService;
    }

    @PostMapping("/seed")
    public String seed(@RequestBody List<String> walletCodes) {
        factureService.generateMonthlyBills(walletCodes);
        return "Factures générées pour les wallets : " + walletCodes;
    }

    @GetMapping("/unpaid/{walletCode}")
    public List<Facture> getUnpaidFactures(
            @PathVariable String walletCode,
            @RequestParam(required = false) String unite) {
        if (unite != null) {
            return factureService.getUnpaidFacturesByProvider(walletCode, unite);
        }
        return factureService.getUnpaidFactures(walletCode);
    }

    @GetMapping("/unpaid/{walletCode}/period")
    public List<Facture> getUnpaidFacturesByPeriod(
            @PathVariable String walletCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate debut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fin) {
        return factureService.getUnpaidFacturesByPeriod(walletCode, debut, fin);
    }

    @PostMapping("/pay")
    public String payFacture(@RequestBody PayFactureRequest request) {
        factureService.payFacture(request.getFactureReference());
        return "Facture " + request.getFactureReference() + " payée avec succès";
    }
}

class PayFactureRequest {
    private String factureReference;

    public String getFactureReference() { return factureReference; }
    public void setFactureReference(String factureReference) { this.factureReference = factureReference; }
}