package com.badwallet.controller;

import com.badwallet.dto.WalletRequest;
import com.badwallet.model.Wallet;
import com.badwallet.repository.WalletRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.badwallet.model.Transaction;
import com.badwallet.model.TransactionType;
import com.badwallet.model.TransactionStatus;
import com.badwallet.repository.TransactionRepository;
import com.badwallet.dto.DepositRequest;
import com.badwallet.dto.FactureDTO;
import com.badwallet.dto.WithdrawRequest;
import com.badwallet.dto.TransferRequest;

import com.badwallet.client.PaymentServiceClient;
import com.badwallet.dto.BillPaymentRequest;
import com.badwallet.dto.SpecificBillPaymentRequest;
import java.util.ArrayList;
//import com.badwallet.dto.FactureDTO;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentServiceClient paymentServiceClient;

    public WalletController(WalletRepository walletRepository, 
                            TransactionRepository transactionRepository,
                            PaymentServiceClient paymentServiceClient) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.paymentServiceClient = paymentServiceClient;
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

    @GetMapping
    public Page<Wallet> listWallets(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return walletRepository.findAll(PageRequest.of(page, size));
    }

    // 1.4 Consulter un portefeuille par numéro de téléphone
    @GetMapping("/{phoneNumber}")
    public Wallet getWallet(@PathVariable String phoneNumber) {
        return walletRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new RuntimeException("Portefeuille non trouvé avec ce numéro de téléphone"));
    }

    // 1.5 Consulter uniquement le solde à jour
    @GetMapping("/{phoneNumber}/balance")
    public BigDecimal getBalance(@PathVariable String phoneNumber) {
        Wallet wallet = walletRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new RuntimeException("Portefeuille non trouvé avec ce numéro de téléphone"));
        return wallet.getBalance();
    }

    // 1.6 Effectuer un Dépôt
    @PostMapping("/{walletId}/deposit")
    public Transaction deposit(@PathVariable Long walletId, @RequestBody DepositRequest request) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new RuntimeException("Portefeuille non trouvé"));
        
        // Créer la transaction
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(request.getAmount());
        transaction.setFee(BigDecimal.ZERO);
        transaction.setReference("DEP-" + System.currentTimeMillis());
        transaction.setDescription("Dépôt par " + request.getPaymentMethod());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCreatedAt(LocalDateTime.now());
        
        // Mettre à jour le solde
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);
        
        return transactionRepository.save(transaction);
    }

    // 1.7 Effectuer un Retrait (frais de 1% du montant plafonnés à 5000 CFA)
    @PostMapping("/withdraw")
    public Transaction withdraw(@RequestBody WithdrawRequest request) {
        Wallet wallet = walletRepository.findByPhoneNumber(request.getPhoneNumber())
            .orElseThrow(() -> new RuntimeException("Portefeuille non trouvé"));
        
        // Calcul des frais (1% du montant, plafonné à 5000 CFA)
        BigDecimal fee = request.getAmount().multiply(new BigDecimal("0.01"));
        BigDecimal maxFee = new BigDecimal("5000");
        BigDecimal finalFee = fee.min(maxFee);
        BigDecimal totalDeduction = request.getAmount().add(finalFee);
        
        // Vérifier le solde suffisant
        if (wallet.getBalance().compareTo(totalDeduction) < 0) {
            throw new RuntimeException("Solde insuffisant");
        }
        
        // Créer la transaction
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(request.getAmount());
        transaction.setFee(finalFee);
        transaction.setReference("WTH-" + System.currentTimeMillis());
        transaction.setDescription("Retrait de " + request.getAmount() + " CFA (frais: " + finalFee + " CFA)");
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCreatedAt(LocalDateTime.now());
        
        // Mettre à jour le solde
        wallet.setBalance(wallet.getBalance().subtract(totalDeduction));
        walletRepository.save(wallet);
        
        return transactionRepository.save(transaction);
    }

    // 1.8 Effectuer un Transfert entre deux portefeuilles
    @PostMapping("/transfer")
    public Transaction transfer(@RequestBody TransferRequest request) {
        Wallet sender = walletRepository.findByPhoneNumber(request.getSenderPhone())
            .orElseThrow(() -> new RuntimeException("Emetteur non trouve"));
        Wallet receiver = walletRepository.findByPhoneNumber(request.getReceiverPhone())
            .orElseThrow(() -> new RuntimeException("Receveur non trouve"));
        
        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Solde insuffisant");
        }
        
        // Mettre à jour les soldes
        sender.setBalance(sender.getBalance().subtract(request.getAmount()));
        receiver.setBalance(receiver.getBalance().add(request.getAmount()));
        walletRepository.save(sender);
        walletRepository.save(receiver);
        
        Transaction transaction = new Transaction();
        transaction.setWallet(sender);
        transaction.setReceiverWallet(receiver);
        transaction.setType(TransactionType.TRANSFER);
        transaction.setAmount(request.getAmount().negate());
        transaction.setFee(BigDecimal.ZERO);
        transaction.setReference("TRF-" + System.currentTimeMillis());
        transaction.setDescription("Transfert vers " + receiver.getPhoneNumber());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCreatedAt(LocalDateTime.now());
        
        return transactionRepository.save(transaction);
    }

    // 1.11 Consulter l'historique des transactions par téléphone
    @GetMapping("/{phoneNumber}/transactions")
    public List<Transaction> getTransactionHistory(@PathVariable String phoneNumber) {
        Wallet wallet = walletRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new RuntimeException("Portefeuille non trouve"));
        
        return transactionRepository.findAllByWalletOrReceiverWalletOrderByCreatedAtDesc(wallet);
    }

    // 1.9 Payer une facture du mois en cours
    @PostMapping("/pay")
    public String payCurrentBill(@RequestBody BillPaymentRequest request) {
        Wallet wallet = walletRepository.findByPhoneNumber(request.getPhoneNumber())
            .orElseThrow(() -> new RuntimeException("Portefeuille non trouvé"));
        
        List<FactureDTO> factures = paymentServiceClient.getUnpaidFactures(wallet.getCode());
        
        String currentMonth = String.valueOf(LocalDate.now().getMonthValue());
        String currentYear = String.valueOf(LocalDate.now().getYear());
        
        for (FactureDTO facture : factures) {
            String provider = facture.getProvider();
            String reference = facture.getReference();
            BigDecimal amount = facture.getAmount();
            
            // Vérifier si la référence correspond au mois et année en cours
            // On cherche la présence de "-5-2026" dans la référence
            if (provider.equals(request.getServiceName()) && 
                reference.contains("-" + currentMonth + "-" + currentYear)) {
                
                if (wallet.getBalance().compareTo(amount) < 0) {
                    throw new RuntimeException("Solde insuffisant pour payer la facture de " + amount + " CFA");
                }
                
                // Payer la facture
                paymentServiceClient.payFacture(reference);
                
                // Débiter le wallet
                wallet.setBalance(wallet.getBalance().subtract(amount));
                walletRepository.save(wallet);
                
                // Créer la transaction
                Transaction transaction = new Transaction();
                transaction.setWallet(wallet);
                transaction.setType(TransactionType.PAYMENT);
                transaction.setAmount(amount.negate());
                transaction.setFee(BigDecimal.ZERO);
                transaction.setReference("PAY-" + System.currentTimeMillis());
                transaction.setDescription("Paiement " + provider + " - " + reference);
                transaction.setStatus(TransactionStatus.COMPLETED);
                transaction.setCreatedAt(LocalDateTime.now());
                transactionRepository.save(transaction);
                
                return "Facture " + reference + " payée avec succès";
            }
        }
        
        return "Aucune facture impayée trouvée pour " + request.getServiceName() + 
            " au mois " + currentMonth + "/" + currentYear;
    }

    // 1.10 Payer des factures spécifiques
    @PostMapping("/pay-factures")
    public String paySpecificBills(@RequestBody SpecificBillPaymentRequest request) {
        Wallet wallet = walletRepository.findByPhoneNumber(request.getPhoneNumber())
            .orElseThrow(() -> new RuntimeException("Portefeuille non trouvé"));
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<String> paidReferences = new ArrayList<>();
        List<FactureDTO> factures = paymentServiceClient.getUnpaidFactures(wallet.getCode());
        
        for (String reference : request.getFactureReferences()) {
            for (FactureDTO facture : factures) {
                String ref = facture.getReference();
                if (ref.equals(reference)) {
                    BigDecimal amount = facture.getAmount();
                    totalAmount = totalAmount.add(amount);
                    paidReferences.add(reference);
                    break;
                }
            }
        }
        
        if (wallet.getBalance().compareTo(totalAmount) < 0) {
            throw new RuntimeException("Solde insuffisant");
        }
        
        for (String reference : paidReferences) {
            paymentServiceClient.payFacture(reference);
        }
        
        wallet.setBalance(wallet.getBalance().subtract(totalAmount));
        walletRepository.save(wallet);
        
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.PAYMENT);
        transaction.setAmount(totalAmount.negate());
        transaction.setFee(BigDecimal.ZERO);
        transaction.setReference("PAY-M-" + System.currentTimeMillis());
        transaction.setDescription("Paiement de " + paidReferences.size() + " factures");
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
        
        return paidReferences.size() + " factures payées avec succès";
    }
}