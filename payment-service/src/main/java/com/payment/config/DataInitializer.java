package com.payment.config;

import com.payment.model.Facture;
import com.payment.model.FactureStatus;
import com.payment.repository.FactureRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final FactureRepository factureRepository;

    public DataInitializer(FactureRepository factureRepository) {
        this.factureRepository = factureRepository;
    }

    @Override
    public void run(String... args) {
        if (factureRepository.count() == 0) {
            generateBillsForAllWallets();
            System.out.println("✅ Factures générées automatiquement au démarrage !");
        }
    }

    private void generateBillsForAllWallets() {
        // Liste des wallets (à adapter selon vos besoins)
        List<String> walletCodes = List.of(
            "WLT-0000001", "WLT-0000002", "WLT-0000003", 
            "WLT-0000004", "WLT-0000005", "WLT-0000006",
            "WLT-0000007", "WLT-0000008", "WLT-0000009", "WLT-0000010"
        );
        
        generateMonthlyBills(walletCodes);
    }

    public void generateMonthlyBills(List<String> walletCodes) {
        List<Facture> factures = new ArrayList<>();
        String[] providers = {"ISM", "WOYAFAL"};

        for (String walletCode : walletCodes) {
            for (String provider : providers) {
                // Générer les 6 derniers mois
                for (int i = 1; i <= 6; i++) {
                    YearMonth yearMonth = YearMonth.now().minusMonths(i);
                    String reference = String.format("FAC-%s-%s-%d-%d",
                        provider, walletCode.replace("WLT-", ""), 
                        yearMonth.getMonthValue(), yearMonth.getYear());
                    
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
        System.out.println("Ok " + factures.size() + " factures générées !");
    }

    private BigDecimal generateAmount(String provider) {
        if ("ISM".equals(provider)) {
            return new BigDecimal(50000 + Math.random() * 20000); // 50k - 70k CFA
        } else { // WOYAFAL
            return new BigDecimal(10000 + Math.random() * 15000); // 10k - 25k CFA
        }
    }
}