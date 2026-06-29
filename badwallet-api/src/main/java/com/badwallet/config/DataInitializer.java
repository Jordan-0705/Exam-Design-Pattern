package com.badwallet.config;

import com.badwallet.model.Wallet;
import com.badwallet.repository.WalletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final WalletRepository walletRepository;

    public DataInitializer(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public void run(String... args) {
        if (walletRepository.count() == 0) {
            for (int i = 1; i <= 10; i++) {
                Wallet wallet = new Wallet();
                wallet.setPhoneNumber("+22177" + String.format("%07d", i));
                wallet.setEmail("user" + i + "@test.com");
                wallet.setCode("WLT-" + String.format("%07d", i));
                wallet.setBalance(new BigDecimal(10000 + Math.random() * 90000));
                wallet.setCurrency("XOF");
                wallet.setCreatedAt(LocalDateTime.now());
                walletRepository.save(wallet);
            }
            System.out.println("Ok 10 wallets créés automatiquement au démarrage !");
        }
    }
}