// src/main/java/com/example/aisupport/openai/OpenAiEmailOutput.java
package com.example.aisupport.openai;

import lombok.Data;

@Data
public class OpenAiEmailOutput {
    private String category;        // QUESTION_FACTURE, BUG, DEMANDE_INFO_PRODUIT, AUTRE
    private String clientName;
    private String invoiceNumber;
    private String summary;
    private String suggestedReply;
}
