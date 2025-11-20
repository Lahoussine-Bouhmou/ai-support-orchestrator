// src/main/java/com/example/aisupport/service/DummyAiClientImpl.java
package com.example.aisupport.service;

import com.example.aisupport.model.EmailAnalysisResult;
import com.example.aisupport.model.EmailCategory;
import com.example.aisupport.model.EmailRequest;
import org.springframework.stereotype.Component;

//@Component
public class DummyAiClientImpl implements AiClient {

    @Override
    public EmailAnalysisResult analyzeEmail(EmailRequest email) {
        EmailCategory category = guessCategory(email);
        String clientName = guessClientName(email);
        String invoiceNumber = guessInvoiceNumber(email);
        String summary = buildSummary(email, category);
        String suggestedReply = buildReply(email, category);

        return EmailAnalysisResult.builder()
                .category(category)
                .clientName(clientName)
                .invoiceNumber(invoiceNumber)
                .summary(summary)
                .suggestedReply(suggestedReply)
                .build();
    }

    private EmailCategory guessCategory(EmailRequest email) {
        String text = (email.getSubject() + " " + email.getBody()).toLowerCase();

        if (text.contains("facture") || text.contains("invoice")) {
            return EmailCategory.QUESTION_FACTURE;
        } else if (text.contains("bug") || text.contains("erreur") || text.contains("crash")) {
            return EmailCategory.BUG;
        } else if (text.contains("info") || text.contains("renseignement") || text.contains("prix")) {
            return EmailCategory.DEMANDE_INFO_PRODUIT;
        }
        return EmailCategory.AUTRE;
    }

    private String guessClientName(EmailRequest email) {
        // Pour l’instant : juste l’email expéditeur
        return email.getFrom();
    }

    private String guessInvoiceNumber(EmailRequest email) {
        // Très naïf : chercher "FACT-XXXX"
        String text = email.getBody();
        int idx = text.indexOf("FACT-");
        if (idx != -1 && idx + 9 <= text.length()) {
            return text.substring(idx, idx + 9);
        }
        return null;
    }

    private String buildSummary(EmailRequest email, EmailCategory category) {
        return "Email de catégorie " + category +
                " provenant de " + email.getFrom() +
                " avec l'objet : \"" + email.getSubject() + "\".";
    }

    private String buildReply(EmailRequest email, EmailCategory category) {
        return switch (category) {
            case QUESTION_FACTURE -> "Bonjour,\n\nMerci pour votre message concernant votre facture. " +
                    "Nous allons vérifier les informations et revenir vers vous rapidement.\n\nCordialement,\nSupport";
            case BUG -> "Bonjour,\n\nMerci de nous avoir signalé ce problème. " +
                    "Notre équipe technique va analyser le bug et nous reviendrons vers vous avec une solution.\n\nCordialement,\nSupport";
            case DEMANDE_INFO_PRODUIT -> "Bonjour,\n\nMerci pour votre intérêt. " +
                    "Nous vous envoyons ci-joint les informations détaillées sur nos produits.\n\nCordialement,\nSupport";
            default -> "Bonjour,\n\nMerci pour votre message. " +
                    "Nous reviendrons vers vous dans les plus brefs délais.\n\nCordialement,\nSupport";
        };
    }
}
