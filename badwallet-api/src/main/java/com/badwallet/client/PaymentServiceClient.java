package com.badwallet.client;

import com.badwallet.dto.FactureDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class PaymentServiceClient {

    private final WebClient webClient;

    public PaymentServiceClient(@Value("${payment.service.url:http://localhost:8081}") String paymentServiceUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(paymentServiceUrl)
            .build();
    }

    public List<FactureDTO> getUnpaidFactures(String walletCode) {
        return webClient.get()
            .uri("/api/factures/unpaid/" + walletCode)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<FactureDTO>>() {})
            .block();
    }

    public List<FactureDTO> getUnpaidFacturesByProvider(String walletCode, String provider) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/factures/unpaid/" + walletCode)
                .queryParam("unite", provider)
                .build())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<FactureDTO>>() {})
            .block();
    }

    public List<FactureDTO> getUnpaidFacturesByPeriod(String walletCode, String debut, String fin) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/factures/unpaid/" + walletCode + "/period")
                .queryParam("debut", debut)
                .queryParam("fin", fin)
                .build())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<FactureDTO>>() {})
            .block();
    }

    public String payFacture(String factureReference) {
        Map<String, String> request = Map.of("factureReference", factureReference);
        return webClient.post()
            .uri("/api/factures/pay")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }
}