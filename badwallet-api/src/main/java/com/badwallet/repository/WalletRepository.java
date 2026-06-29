package com.badwallet.repository;

import com.badwallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByPhoneNumber(String phoneNumber);
    Optional<Wallet> findByCode(String code);  // ← AJOUTER CETTE LIGNE
    boolean existsByPhoneNumber(String phoneNumber);
}