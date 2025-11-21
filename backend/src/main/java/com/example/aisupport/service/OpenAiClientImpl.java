// src/main/java/com/example/aisupport/service/OpenAiClientImpl.java
package com.example.aisupport.service;

import com.example.aisupport.model.EmailAnalysisResult;
import com.example.aisupport.model.EmailCategory;
import com.example.aisupport.model.EmailRequest;
import com.example.aisupport.openai.OpenAiChatRequest;
import com.example.aisupport.openai.OpenAiChatResponse;
import com.example.aisupport.openai.OpenAiEmailOutput;
import com.example.aisupport.openai.OpenAiMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
//@Component
@RequiredArgsConstructor
public class OpenAiClientImpl implements AiClient {

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o-mini";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Override
    public EmailAnalysisResult analyzeEmail(EmailRequest emailRequest) {
        try {
            String userPrompt = buildUserPrompt(emailRequest);

            OpenAiChatRequest requestBody = new OpenAiChatRequest(
                    MODEL,
                    List.of(
                            new OpenAiMessage(
                                    "system",
                                    """
                                    Tu es un assistant pour un service client d'entreprise.
                                    Analyse des emails de clients.

                                    Tu DOIS répondre STRICTEMENT en JSON, sans texte autour, 
                                    avec le format suivant :
                                    {
                                      "category": "QUESTION_FACTURE | BUG | DEMANDE_INFO_PRODUIT | AUTRE",
                                      "clientName": "<nom ou email du client>",
                                      "invoiceNumber": "<numéro de facture ou null>",
                                      "summary": "<résumé en une phrase en français>",
                                      "suggestedReply": "<réponse email professionnelle en français>"
                                    }
                                    """
                            ),
                            new OpenAiMessage("user", userPrompt)
                    ),
                    0.2
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAiApiKey);

            HttpEntity<OpenAiChatRequest> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<OpenAiChatResponse> response = restTemplate.exchange(
                    OPENAI_URL,
                    HttpMethod.POST,
                    entity,
                    OpenAiChatResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && !response.getBody().getChoices().isEmpty()) {

                String content = response.getBody()
                        .getChoices()
                        .get(0)
                        .getMessage()
                        .getContent()
                        .trim();

                log.debug("OpenAI raw content: {}", content);

                OpenAiEmailOutput output = objectMapper.readValue(content, OpenAiEmailOutput.class);

                EmailCategory category = mapCategory(output.getCategory());

                return EmailAnalysisResult.builder()
                        .category(category)
                        .clientName(output.getClientName())
                        .invoiceNumber(output.getInvoiceNumber())
                        .summary(output.getSummary())
                        .suggestedReply(output.getSuggestedReply())
                        .build();
            } else {
                log.warn("Réponse OpenAI invalide, fallback sur la logique simple.");
                return fallback(emailRequest);
            }

        } catch (Exception e) {
            log.error("Erreur lors de l'appel OpenAI, fallback sur la logique simple", e);
            return fallback(emailRequest);
        }
    }

    private String buildUserPrompt(EmailRequest email) {
        return """
                Voici un email reçu d'un client.

                From: %s
                Subject: %s
                Body:
                %s

                Analyse cet email et remplis les champs demandés.
                Rappelle-toi : tu dois répondre STRICTEMENT en JSON, sans texte avant ou après.
                """.formatted(email.getFrom(), email.getSubject(), email.getBody());
    }

    private EmailCategory mapCategory(String rawCategory) {
        if (rawCategory == null) {
            return EmailCategory.AUTRE;
        }
        String normalized = rawCategory.trim().toUpperCase();
        try {
            return EmailCategory.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            // Si le modèle renvoie un texte comme "question facture"
            if (normalized.contains("FACTURE")) return EmailCategory.QUESTION_FACTURE;
            if (normalized.contains("BUG") || normalized.contains("ERREUR")) return EmailCategory.BUG;
            if (normalized.contains("INFO") || normalized.contains("PRODUIT")) return EmailCategory.DEMANDE_INFO_PRODUIT;
            return EmailCategory.AUTRE;
        }
    }

    // Fallback : copie simplifiée de l'ancienne logique "dummy"
    private EmailAnalysisResult fallback(EmailRequest email) {
        String text = (email.getSubject() + " " + email.getBody()).toLowerCase();

        EmailCategory category;
        if (text.contains("facture") || text.contains("invoice")) {
            category = EmailCategory.QUESTION_FACTURE;
        } else if (text.contains("bug") || text.contains("erreur") || text.contains("crash")) {
            category = EmailCategory.BUG;
        } else if (text.contains("info") || text.contains("renseignement") || text.contains("prix")) {
            category = EmailCategory.DEMANDE_INFO_PRODUIT;
        } else {
            category = EmailCategory.AUTRE;
        }

        String invoiceNumber = null;
        int idx = email.getBody().indexOf("FACT-");
        if (idx != -1 && idx + 9 <= email.getBody().length()) {
            invoiceNumber = email.getBody().substring(idx, idx + 9);
        }

        String summary = "Email de catégorie " + category +
                " provenant de " + email.getFrom() +
                " avec l'objet : \"" + email.getSubject() + "\".";

        String suggestedReply = switch (category) {
            case QUESTION_FACTURE -> "Bonjour,\n\nMerci pour votre message concernant votre facture. " +
                    "Nous allons vérifier les informations et revenir vers vous rapidement.\n\nCordialement,\nSupport";
            case BUG -> "Bonjour,\n\nMerci de nous avoir signalé ce problème. " +
                    "Notre équipe technique va analyser le bug et nous reviendrons vers vous avec une solution.\n\nCordialement,\nSupport";
            case DEMANDE_INFO_PRODUIT -> "Bonjour,\n\nMerci pour votre intérêt. " +
                    "Nous vous envoyons ci-joint les informations détaillées sur nos produits.\n\nCordialement,\nSupport";
            default -> "Bonjour,\n\nMerci pour votre message. " +
                    "Nous reviendrons vers vous dans les plus brefs délais.\n\nCordialement,\nSupport";
        };

        return EmailAnalysisResult.builder()
                .category(category)
                .clientName(email.getFrom())
                .invoiceNumber(invoiceNumber)
                .summary(summary)
                .suggestedReply(suggestedReply)
                .build();
    }
}
