package com.example.aisupport.service;

import com.example.aisupport.gemini.*;
import com.example.aisupport.model.EmailAnalysisResult;
import com.example.aisupport.model.EmailCategory;
import com.example.aisupport.model.EmailRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClientImpl implements AiClient {

    // Endpoint REST pour Gemini 2.5 Flash (Developer API)
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Override
    public EmailAnalysisResult analyzeEmail(EmailRequest emailRequest) {
        // Si la clé est vide, on log et on passe direct en fallback
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            log.error("GEMINI_API_KEY manquante (gemini.api.key vide) → fallback.");
            return fallback(emailRequest);
        }

        try {
            String prompt = buildPrompt(emailRequest);

            GeminiRequest requestBody = new GeminiRequest(
                    List.of(new GeminiContent(
                            List.of(new GeminiPart(prompt))
                    ))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", geminiApiKey);

            HttpEntity<GeminiRequest> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<GeminiResponse> response;
            try {
                response = restTemplate.exchange(
                        GEMINI_URL,
                        HttpMethod.POST,
                        entity,
                        GeminiResponse.class
                );
            } catch (RestClientException e) {
                log.error("Erreur HTTP lors de l'appel Gemini", e);
                return fallback(emailRequest);
            }

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Appel Gemini non 2xx: status={}, body={}",
                        response.getStatusCode(), response.getBody());
                return fallback(emailRequest);
            }

            GeminiResponse body = response.getBody();
            if (body == null || body.getCandidates() == null || body.getCandidates().isEmpty()) {
                log.warn("Réponse Gemini vide → fallback.");
                return fallback(emailRequest);
            }

            GeminiResponse.Candidate candidate = body.getCandidates().get(0);
            if (candidate.getContent() == null
                    || candidate.getContent().getParts() == null
                    || candidate.getContent().getParts().isEmpty()) {
                log.warn("Candidat Gemini sans contenu → fallback.");
                return fallback(emailRequest);
            }

            String text = candidate.getContent().getParts().get(0).getText();
            log.info("Gemini raw content: {}", text); // temporaire pour debug

            String json = extractJson(text);

            log.info("Gemini extracted JSON: {}", json); // pour vérifier

            GeminiEmailOutput output = objectMapper.readValue(json, GeminiEmailOutput.class);

            EmailCategory category = mapCategory(output.getCategory());

            return EmailAnalysisResult.builder()
                    .category(category)
                    .clientName(output.getClientName())
                    .invoiceNumber(output.getInvoiceNumber())
                    .summary(output.getSummary())
                    .suggestedReply(output.getSuggestedReply())
                    .build();

        } catch (Exception e) {
            log.error("Exception générale lors de l'appel Gemini → fallback", e);
            return fallback(emailRequest);
        }
    }

    private String buildPrompt(EmailRequest email) {
        return """
                Tu es un assistant pour un service client d'entreprise.
                Tu analyses des emails de clients.

                OBJECTIF :
                À partir de l'email suivant, tu dois produire un JSON STRICT, sans texte avant ou après,
                avec exactement ce format :

                {
                  "category": "QUESTION_FACTURE | BUG | DEMANDE_INFO_PRODUIT | AUTRE",
                  "clientName": "<nom ou email du client>",
                  "invoiceNumber": "<numéro de facture ou null>",
                  "summary": "<résumé en une phrase en français>",
                  "suggestedReply": "<réponse email professionnelle en français>"
                }

                - "category" doit être exactement une des 4 valeurs.
                - Si tu ne trouves pas de numéro de facture, mets null.
                - Pas de commentaires, pas de texte hors JSON.

                Voici l'email :

                From: %s
                Subject: %s
                Body:
                %s
                """.formatted(email.getFrom(), email.getSubject(), email.getBody());
    }

    private String extractJson(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Réponse Gemini vide");
        }

        String trimmed = text.trim();

        // Si Gemini renvoie un bloc de code markdown ```json ... ```
        if (trimmed.startsWith("```")) {
            // On cherche le premier '{' et le dernier '}'
            int firstBrace = trimmed.indexOf('{');
            int lastBrace = trimmed.lastIndexOf('}');
            if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
                return trimmed.substring(firstBrace, lastBrace + 1);
            }
        }

        // Cas général : on cherche la première accolade ouvrante et la dernière fermante
        int firstBrace = trimmed.indexOf('{');
        int lastBrace = trimmed.lastIndexOf('}');
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return trimmed.substring(firstBrace, lastBrace + 1);
        }

        // Si on arrive ici, ce n'est pas du JSON exploitable
        throw new IllegalArgumentException("Impossible d'extraire un JSON valide de la réponse Gemini: " + trimmed);
    }

    private EmailCategory mapCategory(String rawCategory) {
        if (rawCategory == null) {
            return EmailCategory.AUTRE;
        }
        String normalized = rawCategory.trim().toUpperCase();
        try {
            return EmailCategory.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            if (normalized.contains("FACTURE")) return EmailCategory.QUESTION_FACTURE;
            if (normalized.contains("BUG") || normalized.contains("ERREUR")) return EmailCategory.BUG;
            if (normalized.contains("INFO") || normalized.contains("PRODUIT")) return EmailCategory.DEMANDE_INFO_PRODUIT;
            return EmailCategory.AUTRE;
        }
    }

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
