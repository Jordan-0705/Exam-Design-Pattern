package com.payment.repository;

import com.payment.model.Facture;
import com.payment.model.FactureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
    List<Facture> findByWalletCodeAndStatus(String walletCode, FactureStatus status);
    List<Facture> findByWalletCodeAndProviderAndStatus(String walletCode, String provider, FactureStatus status);
    List<Facture> findByWalletCodeAndDueDateBetweenAndStatus(String walletCode, LocalDate debut, LocalDate fin, FactureStatus status);
    Optional<Facture> findByReference(String reference);
    boolean existsByReference(String reference);
}