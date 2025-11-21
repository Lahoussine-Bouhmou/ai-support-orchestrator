// src/main/java/com/example/aisupport/service/AiClient.java
package com.example.aisupport.service;

import com.example.aisupport.model.EmailAnalysisResult;
import com.example.aisupport.model.EmailRequest;

public interface AiClient {
    EmailAnalysisResult analyzeEmail(EmailRequest emailRequest);
}
