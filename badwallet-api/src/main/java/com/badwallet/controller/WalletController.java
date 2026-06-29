package com.badwallet.controller;

import com.badwallet.dto.WalletRequest;
import com.badwallet.model.Wallet;
import com.badwallet.repository.WalletRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletRepository walletRepository;

    public WalletController(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    // 1.1 Seeder la base de données
    @PostMapping("/seed")
    public String seedDatabase(@RequestParam int numWallets, @RequestParam int eventsPerWallet) {
        for (int i = 1; i <= numWallets; i++) {
            Wallet wallet = new Wallet();
            wallet.setPhoneNumber("+22177" + String.format("%07d", i));
            wallet.setEmail("user" + i + "@test.com");
            wallet.setCode("WLT-" + String.format("%07d", i));
            wallet.setBalance(new BigDecimal(10000 + Math.random() * 90000));
            wallet.setCurrency("XOF");
            wallet.setCreatedAt(LocalDateTime.now());
            walletRepository.save(wallet);
        }
        
        return "Ok " + numWallets + " wallets créés avec " + eventsPerWallet + " événements par wallet (simulé)";
    }

    // 1.2 Créer un nouveau portefeuille
    @PostMapping
    public Wallet createWallet(@RequestBody WalletRequest request) {
        // Vérifier que le téléphone n'existe pas déjà
        if (walletRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("Le numéro de téléphone existe déjà");
        }
        
        // Vérifier que le code n'existe pas déjà
        if (walletRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Le code du portefeuille existe déjà");
        }
        
        // Créer le nouveau wallet
        Wallet wallet = new Wallet();
        wallet.setPhoneNumber(request.getPhoneNumber());
        wallet.setEmail(request.getEmail());
        wallet.setCode(request.getCode());
        wallet.setBalance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO);
        wallet.setCurrency(request.getCurrency() != null ? request.getCurrency() : "XOF");
        wallet.setCreatedAt(LocalDateTime.now());
        
        return walletRepository.save(wallet);
    }
}