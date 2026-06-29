package com.badwallet.controller;

import com.badwallet.model.Wallet;
import com.badwallet.repository.WalletRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletRepository walletRepository;

    // Constructeur explicite pour l'injection de dépendance
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
}