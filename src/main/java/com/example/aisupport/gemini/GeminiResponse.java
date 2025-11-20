// src/main/java/com/example/aisupport/gemini/GeminiResponse.java
package com.example.aisupport.gemini;

import lombok.Data;

import java.util.List;

@Data
public class GeminiResponse {

    private List<Candidate> candidates;

    @Data
    public static class Candidate {
        private GeminiContent content;
        private String finishReason;
        private String safetyRatings;
    }
}
