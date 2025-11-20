// src/main/java/com/example/aisupport/model/EmailAnalysisResult.java
package com.example.aisupport.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailAnalysisResult {
    private EmailCategory category;
    private String clientName;
    private String invoiceNumber;
    private String summary;
    private String suggestedReply;
}
