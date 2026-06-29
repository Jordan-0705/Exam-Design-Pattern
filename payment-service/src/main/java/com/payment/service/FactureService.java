package com.payment.service;

import com.payment.model.Facture;
import com.payment.model.FactureStatus;
import com.payment.repository.FactureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class FactureService {

    private final FactureRepository factureRepository;

    public FactureService(FactureRepository factureRepository) {
        this.factureRepository = factureRepository;
    }

    @Transactional
    public void generateMonthlyBills(List<String> walletCodes) {
        List<Facture> factures = new ArrayList<>();
        String[] providers = {"ISM", "WOYAFAL"};

        for (String walletCode : walletCodes) {
            for (String provider : providers) {
                for (int i = 1; i <= 6; i++) {
                    YearMonth yearMonth = YearMonth.now().minusMonths(i);
                    String reference = String.format("FAC-%s-%s-%d-%d",
                        provider, walletCode.replace("WLT-", ""), yearMonth.getMonthValue(), yearMonth.getYear());
                    
                    if (!factureRepository.existsByReference(reference)) {
                        Facture facture = new Facture();
                        facture.setWalletCode(walletCode);
                        facture.setProvider(provider);
                        facture.setReference(reference);
                        facture.setAmount(generateAmount(provider));
                        facture.setDueDate(LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 15));
                        facture.setStatus(FactureStatus.UNPAID);
                        factures.add(facture);
                    }
                }
            }
        }
        factureRepository.saveAll(factures);
    }

    public List<Facture> getUnpaidFactures(String walletCode) {
        return factureRepository.findByWalletCodeAndStatus(walletCode, FactureStatus.UNPAID);
    }

    public List<Facture> getUnpaidFacturesByProvider(String walletCode, String provider) {
        return factureRepository.findByWalletCodeAndProviderAndStatus(walletCode, provider, FactureStatus.UNPAID);
    }

    public List<Facture> getUnpaidFacturesByPeriod(String walletCode, LocalDate debut, LocalDate fin) {
        return factureRepository.findByWalletCodeAndDueDateBetweenAndStatus(walletCode, debut, fin, FactureStatus.UNPAID);
    }

    @Transactional
    public void payFacture(String reference) {
        Facture facture = factureRepository.findByReference(reference)
            .orElseThrow(() -> new RuntimeException("Facture non trouvée"));
        facture.setStatus(FactureStatus.PAID);
        factureRepository.save(facture);
    }

    private BigDecimal generateAmount(String provider) {
        if ("ISM".equals(provider)) {
            return new BigDecimal(50000 + Math.random() * 20000);
        } else {
            return new BigDecimal(10000 + Math.random() * 15000);
        }
    }
}