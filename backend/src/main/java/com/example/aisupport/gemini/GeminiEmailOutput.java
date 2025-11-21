// src/main/java/com/example/aisupport/gemini/GeminiEmailOutput.java
package com.example.aisupport.gemini;

import lombok.Data;

@Data
public class GeminiEmailOutput {
    private String category;        // QUESTION_FACTURE | BUG | DEMANDE_INFO_PRODUIT | AUTRE
    private String clientName;
    private String invoiceNumber;
    private String summary;
    private String suggestedReply;
}
