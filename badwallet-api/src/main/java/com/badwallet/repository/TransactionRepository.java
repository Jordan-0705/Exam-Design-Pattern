// badwallet-api/src/main/java/com/badwallet/repository/TransactionRepository.java

package com.badwallet.repository;

import com.badwallet.model.Transaction;
import com.badwallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByWalletOrderByCreatedAtDesc(Wallet wallet);
    @Query("SELECT t FROM Transaction t WHERE t.wallet = :wallet OR t.receiverWallet = :wallet ORDER BY t.createdAt DESC")
    List<Transaction> findAllByWalletOrReceiverWalletOrderByCreatedAtDesc(@Param("wallet") Wallet wallet);
    
    List<Transaction> findByReceiverWalletOrderByCreatedAtDesc(Wallet wallet);
}